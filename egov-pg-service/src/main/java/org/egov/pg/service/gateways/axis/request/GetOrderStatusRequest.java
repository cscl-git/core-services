package org.egov.pg.service.gateways.axis.request;

public class GetOrderStatusRequest {

    private String orderId;
    private Boolean force;

    public Boolean getForce() {
        return force;
    }

    public void setForce(Boolean force) {
        this.force = force;
    }

    public GetOrderStatusRequest shouldForceFetchRefundStatus () {
        setForce(true);
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public GetOrderStatusRequest withOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }
}
