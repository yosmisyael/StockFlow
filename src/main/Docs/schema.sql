-- =========================================================
-- PostgreSQL Database Schema for StockFlow
-- =========================================================

-- Drop tables in reverse order of dependency (for reruns)
DROP TABLE IF EXISTS transaction_details CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS stock_ledger CASCADE;
DROP TABLE IF EXISTS storage_locations CASCADE;
DROP TABLE IF EXISTS batches CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS suppliers CASCADE;
DROP TABLE IF EXISTS staff CASCADE;
DROP TABLE IF EXISTS warehouses CASCADE;
DROP TABLE IF EXISTS manager CASCADE;
DROP TABLE IF EXISTS sessions CASCADE;
DROP TYPE user_role;
DROP TYPE warehouse_status;

SELECT * FROM warehouses;

-- =========================================================
-- Manager Table
-- =========================================================
CREATE TABLE manager
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(100)        NOT NULL,
    email    VARCHAR(100) UNIQUE NOT NULL,
    company  VARCHAR(100),
    password VARCHAR(255)        NOT NULL
);

-- =========================================================
-- Warehouses Table
-- =========================================================
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
    manager_id             BIGINT           REFERENCES manager (id)
                                                ON UPDATE CASCADE ON DELETE SET NULL
);

-- =========================================================
-- Staff Table
-- =========================================================
CREATE TABLE staff
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100)        NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    password     VARCHAR(255)        NOT NULL,
    warehouse_id BIGINT              REFERENCES warehouses (id)
                                         ON UPDATE CASCADE ON DELETE SET NULL
);

-- =========================================================
-- Suppliers Table
-- =========================================================
CREATE TABLE suppliers
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    address      TEXT,
    contact_info TEXT
);

-- =========================================================
-- Products Table
-- =========================================================
CREATE TABLE products
(
    sku                         VARCHAR(50) PRIMARY KEY,
    name                        VARCHAR(100) NOT NULL,
    brand                       VARCHAR(100),
    description                 TEXT,
    purchase_price              NUMERIC(12, 2),
    weight_per_unit_kg          NUMERIC(10, 3),
    volume_per_unit_m3          NUMERIC(10, 3),
    product_type                VARCHAR(50),
    reorder_point               INTEGER DEFAULT 0,
    reorder_quantity            INTEGER DEFAULT 0,
    units_per_case              INTEGER,
    shelf_life_days             INTEGER,
    days_to_alert_before_expiry INTEGER,
    sold_by_weight              BOOLEAN DEFAULT FALSE,
    required_temp               NUMERIC(5, 2),
    supplier_id                 BIGINT       REFERENCES suppliers (id)
                                                 ON UPDATE CASCADE ON DELETE SET NULL
);

-- =========================================================
-- Batches Table
-- =========================================================
CREATE TABLE batches
(
    batch_id     BIGSERIAL PRIMARY KEY,
    product_sku  VARCHAR(50) REFERENCES products (sku)
        ON UPDATE CASCADE ON DELETE CASCADE,
    batch_number VARCHAR(100),
    expiry_date  DATE,
    quantity     INTEGER CHECK (quantity >= 0)
);

-- =========================================================
-- Storage Locations Table
-- =========================================================
CREATE TABLE storage_locations
(
    location_id   BIGSERIAL PRIMARY KEY,
    location_code VARCHAR(50) UNIQUE NOT NULL,
    location_type VARCHAR(50),
    status        VARCHAR(30) DEFAULT 'available',
    warehouse_id  BIGINT REFERENCES warehouses (id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- =========================================================
-- Stock Ledger Table
-- =========================================================
CREATE TABLE stock_ledger
(
    id          BIGSERIAL PRIMARY KEY,
    product_sku VARCHAR(50) REFERENCES products (sku)
        ON UPDATE CASCADE ON DELETE CASCADE,
    location_id BIGINT REFERENCES storage_locations (location_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    batch_id    BIGINT REFERENCES batches (batch_id)
                           ON UPDATE CASCADE ON DELETE SET NULL,
    quantity    INTEGER CHECK (quantity >= 0)
);

-- =========================================================
-- Transactions Table
-- =========================================================
CREATE TABLE transactions
(
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT      REFERENCES staff (id)
                                            ON UPDATE CASCADE ON DELETE SET NULL,
    supplier_id             BIGINT      REFERENCES suppliers (id)
                                            ON UPDATE CASCADE ON DELETE SET NULL,
    date                    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    transaction_type        VARCHAR(50) NOT NULL CHECK (transaction_type IN ('inbound', 'outbound', 'internal')),
    notes                   TEXT,
    status                  VARCHAR(30) DEFAULT 'pending',
    supplier_invoice_number VARCHAR(100),
    delivery_order_number   VARCHAR(100),
    internal_request_id     VARCHAR(100),
    destination_address     TEXT,
    shipping_method         VARCHAR(50),
    tracking_number         VARCHAR(100)
);

-- =========================================================
-- Transaction Details Table
-- =========================================================
CREATE TABLE transaction_details
(
    id             BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT REFERENCES transactions (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    product_sku    VARCHAR(50) REFERENCES products (sku)
        ON UPDATE CASCADE ON DELETE CASCADE,
    quantity       INTEGER NOT NULL CHECK (quantity > 0)
);

-- =========================================================
-- Indexes for Faster Lookup
-- =========================================================
CREATE INDEX idx_products_supplier ON products (supplier_id);
CREATE INDEX idx_batches_product ON batches (product_sku);
CREATE INDEX idx_stockledger_product ON stock_ledger (product_sku);
CREATE INDEX idx_stockledger_location ON stock_ledger (location_id);
CREATE INDEX idx_transactiondetails_transaction ON transaction_details (transaction_id);
CREATE INDEX idx_transactiondetails_product ON transaction_details (product_sku);
CREATE INDEX idx_transactions_user ON transactions (user_id);

-- =========================================================
-- Example Integrity Rules
-- =========================================================
-- (Optional) Prevent duplicate product-supplier pairs
ALTER TABLE products
    ADD CONSTRAINT unique_supplier_product UNIQUE (supplier_id, name);

-- (Optional) Prevent same product in multiple batches with identical batch_number
ALTER TABLE batches
    ADD CONSTRAINT unique_product_batch UNIQUE (product_sku, batch_number);


-- sessions table
CREATE TYPE user_role AS ENUM ('manager', 'staff');

CREATE TABLE sessions
(
    session_id BIGSERIAL PRIMARY KEY,
    user_id    BIGINT              NOT NULL,
    user_type  user_role           NOT NULL,
    token      VARCHAR(255) UNIQUE NOT NULL
);

SELECT * FROM manager;