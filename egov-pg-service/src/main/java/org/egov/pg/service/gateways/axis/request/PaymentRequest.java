package org.egov.pg.service.gateways.axis.request;

import sun.nio.cs.ext.EUC_CN;

public class PaymentRequest {

    public enum NetBanking {
        AXIS("NB_AXIS"),    //Axis Bank
        BOI("NB_BOM"),      //Bank of Maharashtra
        CBI("NB_CBI"),      //Central Bank Of India
        CORP("NB_CORP"),    //Corporation Bank
        DCB("NB_DCB"),      //Development Credit Bank
        FED("NB_FED"),      //Federal Bank
        HDFC("NB_HDFC"),    //HDFC Bank
        ICICI("NB_ICICI"),  //ICICI Netbanking
        IDBI("NB_IDBI"),    //Industrial Development Bank of India
        INDB("NB_INDB"),    //Indian Bank
        INDUS("NB_INDUS"),  //IndusInd Bank
        IOB("NB_IOB"),      //Indian Overseas Bank
        JNK("NB_JNK"),      //Jammu and Kashmir Bank
        KARN("NB_KARN"),    //Karnataka Bank
        KVB("NB_KVB"),      //Karur Vysya
        SBBJ("NB_SBBJ"),    //State Bank of Bikaner and Jaipur
        SBH("NB_SBH"),      //State Bank of Hyderabad
        SBI("NB_SBI"),      //State Bank of India
        SBM("NB_SBM"),      //State Bank of Mysore
        SBT("NB_SBT"),      //State Bank of Travancore
        SOIB("NB_SOIB"),    //South Indian Bank
        UBI("NB_UBI"),      //Union Bank of India
        UNIB("NB_UNIB"),    //United Bank Of India
        VJYB("NB_VJYB"),    //Vijaya Bank
        YESB("NB_YESB"),    //Yes Bank
        CUB("NB_CUB"),      //CityUnion
        CANR("NB_CANR"),    //Canara Bank
        SBP("NB_SBP"),      //State Bank of Patiala
        CITI("NB_CITI"),   //Citi Bank NetBanking
        DEUT("NB_DEUT"),   //Deutsche Bank
        KOTAK("NB_KOTAK"),  //Kotak Bank
        DLS("NB_DLS"),    //Dhanalaxmi Bank
        ING("NB_ING");    //ING Vysya Bank

        private final String bankCode;

        private NetBanking(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getBankCode() {
            return bankCode;
        }
    }

    public enum EmiBanks {
        HDFC("HDFC"), CITI("CITI"), ICICI("ICICI"), SBI("SBI"), AXIS("AXIS"), SCB("SCB"),
        KOTAK("KOTAK"), HSBC("HSBC"), AMEX("AMEX"), INDUS("INDUSIND");

        private final String emiBank;

        private EmiBanks(String bankCode) {
            this.emiBank = bankCode;
        }

        public String getEmiBanks() {
            return emiBank;
        }

    }

    private String merchantId;
    private String orderId;
    private String cardNumber;
    private String cardExpYear;
    private String cardExpMonth;
    private String cardSecurityCode;
    private String cardToken;
    private String paymentMethodType;
    private String paymentMethod;
    private String nameOnCard;
    private Boolean saveToLocker;
    private Boolean redirectAfterPayment;
    private String format;

    private Boolean isEmi;
    private int emiTenure;
    private String emiBank;

    public String getMerchantId() {
        return merchantId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardExpYear() {
        return cardExpYear;
    }

    public String getCardExpMonth() {
        return cardExpMonth;
    }

    public String getCardSecurityCode() {
        return cardSecurityCode;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCardExpYear(String cardExpYear) {
        this.cardExpYear = cardExpYear;
    }

    public void setCardExpMonth(String cardExpMonth) {
        this.cardExpMonth = cardExpMonth;
    }

    public void setCardSecurityCode(String cardSecurityCode) {
        this.cardSecurityCode = cardSecurityCode;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public Boolean getSaveToLocker() {
        return saveToLocker;
    }

    public void setSaveToLocker(Boolean saveToLocker) {
        this.saveToLocker = saveToLocker;
    }

    public Boolean getRedirectAfterPayment() {
        return redirectAfterPayment;
    }

    public void setRedirectAfterPayment(Boolean redirectAfterPayment) {
        this.redirectAfterPayment = redirectAfterPayment;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    //to chain the calls
    public PaymentRequest withMerchantId(String merchantId) {
        this.merchantId = merchantId;
        return this;
    }

    public PaymentRequest withOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public PaymentRequest withCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public PaymentRequest withCardExpYear(String cardExpYear) {
        this.cardExpYear = cardExpYear;
        return this;
    }

    public PaymentRequest withCardExpMonth(String cardExpMonth) {
        this.cardExpMonth = cardExpMonth;
        return this;
    }

    public PaymentRequest withCardSecurityCode(String cardSecurityCode) {
        this.cardSecurityCode = cardSecurityCode;
        return this;
    }

    public PaymentRequest withCardToken(String cardToken) {
        this.cardToken = cardToken;
        return this;
    }

    public PaymentRequest withNetBanking(String bankCode) {
        setPaymentMethodType("NB");
        setPaymentMethod(NetBanking.valueOf(bankCode).getBankCode());
        return this;
    }

    public PaymentRequest withEmi(String bank, int tenure) {
        setIsEmi(true);
        setEmiBank(bank);
        setEmiTenure(tenure);
        return this;
    }

    public Boolean getIsEmi() {
        return isEmi;
    }

    public void setIsEmi(Boolean isEmi) {
        this.isEmi = isEmi;
    }

    public int getEmiTenure() {
        return emiTenure;
    }

    public void setEmiTenure(int emiTenure) {
        this.emiTenure = emiTenure;
    }

    public String getEmiBank() {
        return emiBank;
    }

    public void setEmiBank(String emiBank) {
        this.emiBank = EmiBanks.valueOf(emiBank).getEmiBanks();
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "merchantId='" + merchantId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardExpYear='" + cardExpYear + '\'' +
                ", cardExpMonth='" + cardExpMonth + '\'' +
                ", cardSecurityCode='" + cardSecurityCode + '\'' +
                ", cardToken='" + cardToken + '\'' +
                ", paymentMethodType='" + paymentMethodType + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", nameOnCard='" + nameOnCard + '\'' +
                ", saveToLocker=" + saveToLocker +
                ", redirectAfterPayment=" + redirectAfterPayment +
                ", format='" + format + '\'' +
                ", isEmi=" + isEmi +
                ", emiTenure='" + emiTenure + '\'' +
                ", emiBank='" + emiBank + '\'' +
                '}';
    }
}
