package org.egov.pg.service.gateways.axis.response;

public class PaymentCardResponse {

    String lastFourDigits;
    String cardIsin;
    String expiryMonth;
    String expiryYear;
    String nameOnCard;
    String cardType;
    String cardIssuer;
    String cardBrand;

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public String getCardIsin() {
        return cardIsin;
    }

    public void setCardIsin(String cardIsin) {
        this.cardIsin = cardIsin;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear (String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardIssuer() {
        return cardIssuer;
    }

    public void setCardIssuer(String cardIssuer) {
        this.cardIssuer = cardIssuer;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }


    @Override
    public String toString() {
        return "PaymentCardResponse{" +
                "cardIsin='" + cardIsin + '\'' +
                ", expiryMonth='" + expiryMonth + '\'' +
                ", expiryYear='" + expiryYear + '\'' +
                ", nameOnCard='" + nameOnCard + '\'' +
                ", cardType='" + cardType + '\'' +
                ", cardIssuer='" + cardIssuer + '\'' +
                ", cardBrand='" + cardBrand + '\'' +
                ", lastFourDigits='" + lastFourDigits + '\'' +
                '}';
    }
}
