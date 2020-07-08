package org.egov.pg.service.gateways.axis.response;

import java.util.List;

import org.egov.pg.service.gateways.axis.model.Promotion;
import org.egov.pg.service.gateways.axis.model.Refund;

public class GetOrderStatusResponse {

    private String merchantId;
    private String orderId;
    private Double amount;
    private String status;
    private Long statusId;
    private String txnId;
    private Long gatewayId;
    private String bankErrorCode;
    private String bankErrorMessage;

    /* Refund information */
    private List<Refund> refunds;
    private Double amountRefunded;
    private Boolean refunded;

    /* Promotion information */
    private Promotion promotion;

    /**
     * UDF fields
     */
    private String udf1;
    private String udf2;
    private String udf3;
    private String udf4;
    private String udf5;
    private String udf6;
    private String udf7;
    private String udf8;
    private String udf9;
    private String udf10;

    PaymentGatewayResponse paymentGatewayResponse;
    PaymentCardResponse paymentCardResponse;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public Long getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Long gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getBankErrorCode() {
        return bankErrorCode;
    }

    public void setBankErrorCode(String bankErrorCode) {
        this.bankErrorCode = bankErrorCode;
    }

    public String getBankErrorMessage() {
        return bankErrorMessage;
    }

    public void setBankErrorMessage(String bankErrorMessage) {
        this.bankErrorMessage = bankErrorMessage;
    }

    public PaymentGatewayResponse getPaymentGatewayResponse() {
        return paymentGatewayResponse;
    }

    public void setPaymentCardResponse(PaymentCardResponse paymentCardResponse) {
        this.paymentCardResponse = paymentCardResponse;
    }

    public PaymentCardResponse getPaymentCardResponse() {
        return paymentCardResponse;
    }

    public void setPaymentGatewayResponse(PaymentGatewayResponse paymentGatewayResponse) {
        this.paymentGatewayResponse = paymentGatewayResponse;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

    public String getUdf3() {
        return udf3;
    }

    public void setUdf3(String udf3) {
        this.udf3 = udf3;
    }

    public String getUdf4() {
        return udf4;
    }

    public void setUdf4(String udf4) {
        this.udf4 = udf4;
    }

    public String getUdf5() {
        return udf5;
    }

    public void setUdf5(String udf5) {
        this.udf5 = udf5;
    }

    public String getUdf6() {
        return udf6;
    }

    public void setUdf6(String udf6) {
        this.udf6 = udf6;
    }

    public String getUdf7() {
        return udf7;
    }

    public void setUdf7(String udf7) {
        this.udf7 = udf7;
    }

    public String getUdf8() {
        return udf8;
    }

    public void setUdf8(String udf8) {
        this.udf8 = udf8;
    }

    public String getUdf9() {
        return udf9;
    }

    public void setUdf9(String udf9) {
        this.udf9 = udf9;
    }

    public String getUdf10() {
        return udf10;
    }

    public void setUdf10(String udf10) {
        this.udf10 = udf10;
    }

    public Double getAmountRefunded() {
        return amountRefunded;
    }

    public void setAmountRefunded(Double amountRefunded) {
        this.amountRefunded = amountRefunded;
    }

    public Boolean getRefunded() {
        return refunded;
    }

    public void setRefunded(Boolean refunded) {
        this.refunded = refunded;
    }

    public List<Refund> getRefunds() {
        return refunds;
    }

    public void setRefunds(List<Refund> refunds) {
        this.refunds = refunds;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    @Override
    public String toString() {
        return "GetOrderStatusResponse{" +
                "merchantId='" + merchantId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", statusId=" + statusId +
                ", txnId='" + txnId + '\'' +
                ", gatewayId=" + gatewayId +
                ", bankErrorCode='" + bankErrorCode + '\'' +
                ", bankErrorMessage='" + bankErrorMessage + '\'' +
                ", refunds=" + refunds +
                ", amountRefunded=" + amountRefunded +
                ", refunded=" + refunded +
                ", promotion=" + promotion +
                ", udf1='" + udf1 + '\'' +
                ", udf2='" + udf2 + '\'' +
                ", udf3='" + udf3 + '\'' +
                ", udf4='" + udf4 + '\'' +
                ", udf5='" + udf5 + '\'' +
                ", udf6='" + udf6 + '\'' +
                ", udf7='" + udf7 + '\'' +
                ", udf8='" + udf8 + '\'' +
                ", udf9='" + udf9 + '\'' +
                ", udf10='" + udf10 + '\'' +
                ", paymentGatewayResponse=" + paymentGatewayResponse +
                ", paymentCardResponse=" + paymentCardResponse +
                '}';
    }
}
