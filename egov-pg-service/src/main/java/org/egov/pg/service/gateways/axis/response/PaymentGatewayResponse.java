package org.egov.pg.service.gateways.axis.response;

public class PaymentGatewayResponse {

    String rootReferenceNumber;
    String responseCode;
    String responseMessage;
    String txnId;
    String externalGatewayTxnId;
    String authIdCode;

    public String getRootReferenceNumber() {
        return rootReferenceNumber;
    }

    public void setRootReferenceNumber(String rootReferenceNumber) {
        this.rootReferenceNumber = rootReferenceNumber;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getExternalGatewayTxnId() {
        return externalGatewayTxnId;
    }

    public void setExternalGatewayTxnId(String externalGatewayTxnId) {
        this.externalGatewayTxnId = externalGatewayTxnId;
    }

    public String getAuthIdCode() {
        return authIdCode;
    }

    public void setAuthIdCode(String authIdCode) {
        this.authIdCode = authIdCode;
    }

    @Override
    public String toString() {
        return "PaymentGatewayResponse{" +
                "rootReferenceNumber='" + rootReferenceNumber + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responseMessage='" + responseMessage + '\'' +
                ", txnId='" + txnId + '\'' +
                ", externalGatewayTxnId='" + externalGatewayTxnId + '\'' +
                ", authIdCode='" + authIdCode + '\'' +
                '}';
    }
}