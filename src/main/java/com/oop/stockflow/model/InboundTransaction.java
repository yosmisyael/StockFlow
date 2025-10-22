package com.oop.stockflow.model;

import java.util.Date;

public class InboundTransaction extends Transaction {
    public InboundTransaction(int sku, int staffId, int quantity, Date date, ShippingType shippingType, TransactionStatus status) {
        this.shippingType = shippingType;
        this.status = status;
        this.quantity = quantity;
        this.date = date;
        this.staffId = staffId;
        this.sku = sku;
    }
}
