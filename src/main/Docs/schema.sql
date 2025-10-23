-- =========================================================
-- PostgreSQL Database Schema for StockFlow
-- =========================================================

DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS staff CASCADE;
DROP TABLE IF EXISTS warehouses CASCADE;
DROP TABLE IF EXISTS managers CASCADE;
DROP TABLE IF EXISTS sessions CASCADE;
DROP TYPE IF EXISTS user_role;
DROP TYPE IF EXISTS warehouse_status;
DROP TYPE IF EXISTS product_type;
DROP TYPE IF EXISTS transaction_type;
DROP TYPE IF EXISTS transaction_status;
DROP TYPE IF EXISTS shipping_method;
DROP TYPE IF EXISTS user_role;

CREATE TYPE user_role AS ENUM ('manager', 'staff');

-- Manager Table
CREATE TABLE managers
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(100)        NOT NULL,
    email    VARCHAR(100) UNIQUE NOT NULL,
    company  VARCHAR(100),
    password VARCHAR(255)        NOT NULL
);

-- Warehouses Table
CREATE TYPE warehouse_status AS ENUM ('active', 'inactive', 'maintenance');

CREATE TABLE warehouses
(
    id                     BIGSERIAL PRIMARY KEY,
    name                   VARCHAR(100)     NOT NULL,
    city                   VARCHAR(100)     NOT NULL,
    state                  VARCHAR(100)     NOT NULL,
    postal_code            VARCHAR(10)      NOT NULL,
    address                TEXT             NOT NULL,
    status                 warehouse_status NOT NULL,
    max_capacity_volume_m3 NUMERIC(12, 2),
    max_capacity_weight_kg NUMERIC(12, 2),
    manager_id             BIGINT           REFERENCES managers (id)
                                                ON UPDATE CASCADE ON DELETE SET NULL
);

-- Staff Table
CREATE TABLE staff
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100)        NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    password     VARCHAR(255)        NOT NULL,
    warehouse_id BIGINT              REFERENCES warehouses (id)
                                         ON UPDATE CASCADE ON DELETE SET NULL
);

-- Sessions Table
CREATE TABLE sessions
(
    session_id BIGSERIAL PRIMARY KEY,
    user_id    BIGINT              NOT NULL,
    user_type  user_role           NOT NULL,
    token      VARCHAR(255) UNIQUE NOT NULL
);

CREATE TYPE product_type AS ENUM (
    'dry good',
    'fresh'
    );

-- Products Table
CREATE TABLE products
(
    sku                         SERIAL PRIMARY KEY,
    name                        VARCHAR(100) NOT NULL,
    brand                       VARCHAR(100),
    description                 TEXT,
    purchase_price              NUMERIC(12, 2),
    weight_per_unit_kg          NUMERIC(10, 3),
    volume_per_unit_m3          NUMERIC(10, 3),
    quantity                    INT     DEFAULT 0,
    product_type                product_type,
    warehouse_id                BIGINT           REFERENCES warehouses (id)
                                             ON UPDATE CASCADE ON DELETE SET NULL,
    -- dry good product
    reorder_point               INTEGER DEFAULT 0,
    reorder_quantity            INTEGER DEFAULT 0,
    units_per_case              INTEGER,
    -- fresh product
    required_temp               NUMERIC(5, 2),
    days_to_alert_before_expiry INTEGER
);


CREATE TYPE transaction_type AS ENUM (
    'inbound',
    'outbound'
    );

CREATE TYPE shipping_method AS ENUM (
    'standard ground',
    'express air',
    'sea freight'
    );

CREATE TYPE transaction_status AS ENUM (
    'voided',
    'committed',
    'pending'
    );

-- Transactions Table
CREATE TABLE transactions
(
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT           REFERENCES staff (id)
                                             ON UPDATE CASCADE ON DELETE SET NULL,
    date                TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transaction_type    transaction_type NOT NULL,
    destination_address TEXT             NULL,
    shipping_method     shipping_method  NOT NULL,
    product_sku         INT REFERENCES products (sku)
        ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
    quantity            INT NOT NULL DEFAULT 0,
    status              transaction_status NOT NULL
);

-- Indexes for Faster Lookup
CREATE INDEX idx_transactions_user ON transactions (user_id);

-- trigger for product quantity
CREATE OR REPLACE FUNCTION update_product_quantity_v2()
    RETURNS TRIGGER AS $$
DECLARE
    delta_quantity INTEGER := 0;
BEGIN
    IF (TG_OP = 'INSERT') THEN
        IF NEW.status = 'committed' THEN
            IF NEW.transaction_type = 'inbound' THEN
                delta_quantity := NEW.quantity;
            ELSIF NEW.transaction_type = 'outbound' THEN
                delta_quantity := -NEW.quantity;
            END IF;
        END IF;

    ELSIF (TG_OP = 'UPDATE') THEN
        IF OLD.status = 'committed' THEN
            IF OLD.transaction_type = 'inbound' THEN
                delta_quantity := delta_quantity - OLD.quantity;
            ELSIF OLD.transaction_type = 'outbound' THEN
                delta_quantity := delta_quantity + OLD.quantity;
            END IF;
        END IF;

        IF NEW.status = 'committed' THEN
            IF NEW.transaction_type = 'inbound' THEN
                delta_quantity := delta_quantity + NEW.quantity;
            ELSIF NEW.transaction_type = 'outbound' THEN
                delta_quantity := delta_quantity - NEW.quantity;
            END IF;
        END IF;

        IF OLD.product_sku != NEW.product_sku AND OLD.status != NEW.status THEN
            RAISE WARNING 'product_sku diubah PADA SAAT status berubah (Transaksi ID: %), trigger kuantitas mungkin tidak akurat.', NEW.id;
            delta_quantity := 0;
            IF NEW.status = 'committed' THEN
                IF NEW.transaction_type = 'inbound' THEN
                    delta_quantity := NEW.quantity;
                ELSIF NEW.transaction_type = 'outbound' THEN
                    delta_quantity := -NEW.quantity;
                END IF;
                UPDATE products
                SET quantity = quantity + delta_quantity
                WHERE sku = NEW.product_sku;
                delta_quantity := 0;
            END IF;
        END IF;

    END IF;

    IF delta_quantity != 0 THEN
        UPDATE products
        SET quantity = quantity + delta_quantity
        WHERE sku = NEW.product_sku;

    IF (SELECT quantity FROM products WHERE sku = NEW.product_sku) < 0 THEN
        RAISE EXCEPTION 'product quantity (SKU: %) can not be negative after transaction ID %', NEW.product_sku, NEW.id;
    END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS transactions_after_insert_update ON transactions;
CREATE TRIGGER transactions_after_insert_update_v2
    AFTER INSERT OR UPDATE ON transactions
    FOR EACH ROW
EXECUTE FUNCTION update_product_quantity_v2();