package org.egov.pg.service.gateways.axis.response;

public class PaymentResponse {
    private String status;
    private String txnId;
    private String orderId;
    private String merchantId;

    PaymentAuthenticationResponse authentication;

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTxnId() {
        return txnId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public PaymentAuthenticationResponse getAuthentication() {
        return authentication;
    }

    public void setAuthentication(PaymentAuthenticationResponse authentication) {
        this.authentication = authentication;
    }

    public PaymentResponse withStatus(String status) {
        this.status = status;
        return this;
    }

    public PaymentResponse withTxnId(String txnId) {
        this.txnId = txnId;
        return this;
    }

    public PaymentResponse withMerchantId(String merchantId) {
        this.merchantId = merchantId;
        return this;
    }

    public PaymentResponse withUrl(String url) {
        this.authentication.setUrl(url);
        return this;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "status='" + status + '\'' +
                ", txnId='" + txnId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", authentication=" + authentication +
                '}';
    }
}
