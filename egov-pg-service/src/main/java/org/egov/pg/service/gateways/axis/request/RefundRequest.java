package org.egov.pg.service.gateways.axis.request;

public class RefundRequest {

    private String orderId;
    private Double amount;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public RefundRequest withOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public RefundRequest withAmount(Double amount) {
        this.amount = amount;
        return this;
    }
}
