package com.oop.stockflow.model;

import java.util.Date;

/**
 * Represents an outbound transaction for shipping stock out of the warehouse.
 * Extends the base Transaction class to handle outgoing inventory movements with destination tracking.
 */
public class OutboundTransaction extends Transaction {
    private String destinationAddress;

    /**
     * Constructs an OutboundTransaction with all transaction details including destination.
     * Used when loading existing outbound transactions from the database.
     *
     * @param id The unique transaction identifier.
     * @param sku The stock keeping unit of the product being shipped.
     * @param staffId The ID of the staff member processing the transaction.
     * @param quantity The quantity of items being shipped.
     * @param date The date and time of the transaction.
     * @param shippingType The shipping method used for delivery.
     * @param status The current status of the transaction.
     * @param destinationAddress The address where the items are being shipped to.
     * @param type The type of transaction (should be outbound-related).
     */
    public OutboundTransaction(int id, int sku, int staffId, int quantity, Date date, ShippingType shippingType, TransactionStatus status, String destinationAddress, TransactionType type) {
        this.id = id;
        this.shippingType = shippingType;
        this.status = status;
        this.quantity = quantity;
        this.date = date;
        this.staffId = staffId;
        this.sku = sku;
        this.destinationAddress = destinationAddress;
        this.type = type;
    }

    /**
     * Retrieves the destination address for this outbound transaction.
     *
     * @return The address where items are being shipped to.
     */
    public String getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * Sets the destination address for this outbound transaction.
     *
     * @param destinationAddress The address where items will be shipped to.
     */
    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
}
