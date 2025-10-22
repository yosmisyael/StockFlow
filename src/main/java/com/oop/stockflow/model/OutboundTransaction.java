package com.oop.stockflow.model;

import java.util.Date;

public class OutboundTransaction extends Transaction {
    private String destinationAddress;

    public OutboundTransaction(int id, int sku, int staffId, int quantity, Date date, ShippingType shippingType, TransactionStatus status, String destinationAddress) {
        this.id = id;
        this.shippingType = shippingType;
        this.status = status;
        this.quantity = quantity;
        this.date = date;
        this.staffId = staffId;
        this.sku = sku;
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
}
