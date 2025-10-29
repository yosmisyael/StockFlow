package com.oop.stockflow.model;

import java.util.Date;

/**
 * Abstract base class representing a transaction in the warehouse system.
 * Serves as the foundation for specific transaction types (e.g., InboundTransaction, OutboundTransaction).
 * Contains common properties shared by all transaction types including product, staff, and shipping details.
 */
public abstract class Transaction {
    protected int id;
    protected int sku;
    protected int staffId;
    protected Date date;
    protected int quantity;
    protected TransactionType type;
    protected TransactionStatus status;
    protected ShippingType shippingType;

    /**
     * Retrieves the unique identifier of this transaction.
     *
     * @return The transaction ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this transaction.
     *
     * @param id The transaction ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the type of this transaction.
     *
     * @return The TransactionType enum (e.g., INBOUND, OUTBOUND).
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Sets the type of this transaction.
     *
     * @param type The TransactionType enum (e.g., INBOUND, OUTBOUND).
     */
    public void  setType(TransactionType type) {
        this.type = type;
    }

    /**
     * Retrieves the quantity of items involved in this transaction.
     *
     * @return The quantity of items.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of items involved in this transaction.
     *
     * @param quantity The quantity of items.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Retrieves the stock keeping unit (SKU) of the product in this transaction.
     *
     * @return The product SKU.
     */
    public int getSku() {
        return sku;
    }

    /**
     * Sets the stock keeping unit (SKU) of the product in this transaction.
     *
     * @param sku The product SKU.
     */
    public void setSku(int sku) {
        this.sku = sku;
    }

    /**
     * Retrieves the ID of the staff member who processed this transaction.
     *
     * @return The staff member's ID.
     */
    public int getStaffId() {
        return staffId;
    }

    /**
     * Sets the ID of the staff member who processed this transaction.
     *
     * @param staffId The staff member's ID.
     */
    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    /**
     * Retrieves the date and time when this transaction occurred.
     *
     * @return The transaction date.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date and time when this transaction occurred.
     *
     * @param date The transaction date.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Retrieves the current status of this transaction.
     *
     * @return The TransactionStatus enum (e.g., PENDING, COMPLETED, CANCELLED).
     */
    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of this transaction.
     *
     * @param status The TransactionStatus enum (e.g., PENDING, COMPLETED, CANCELLED).
     */
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    /**
     * Retrieves the shipping method used for this transaction.
     *
     * @return The ShippingType enum (e.g., STANDARD_GROUND, EXPRESS_AIR).
     */
    public ShippingType getShippingType() {
        return shippingType;
    }

    /**
     * Sets the shipping method used for this transaction.
     *
     * @param shippingType The ShippingType enum (e.g., STANDARD_GROUND, EXPRESS_AIR).
     */
    public void setShippingType(ShippingType shippingType) {
        this.shippingType = shippingType;
    }
}
