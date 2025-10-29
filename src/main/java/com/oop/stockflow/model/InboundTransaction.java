package com.oop.stockflow.model;

import java.util.Date;

/**
 * Represents an inbound transaction for receiving stock into the warehouse.
 * Extends the base Transaction class to handle incoming inventory movements.
 */
public class InboundTransaction extends Transaction {

    /**
     * Constructs an InboundTransaction with all transaction details.
     * Used when loading existing inbound transactions from the database.
     *
     * @param id The unique transaction identifier.
     * @param sku The stock keeping unit of the product being received.
     * @param staffId The ID of the staff member processing the transaction.
     * @param quantity The quantity of items being received.
     * @param date The date and time of the transaction.
     * @param shippingType The shipping method used for delivery.
     * @param status The current status of the transaction.
     * @param type The type of transaction (should be inbound-related).
     */
    public InboundTransaction(int id, int sku, int staffId, int quantity, Date date, ShippingType shippingType, TransactionStatus status, TransactionType type) {
        this.id = id;
        this.shippingType = shippingType;
        this.status = status;
        this.quantity = quantity;
        this.date = date;
        this.staffId = staffId;
        this.sku = sku;
        this.type = type;
    }
}
