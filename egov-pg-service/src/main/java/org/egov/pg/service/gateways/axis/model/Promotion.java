package org.egov.pg.service.gateways.axis.model;

import java.util.List;

/**
 * Represents a promotional discount upon one or more conditions.
 */
public class Promotion {
    String id;
    String orderId;
    Double discountAmount;
    PromotionStatus status;
    List<PromotionCondition> promotionConditions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public List<PromotionCondition> getPromotionConditions() {
        return promotionConditions;
    }

    public void setPromotionConditions(List<PromotionCondition> promotionConditions) {
        this.promotionConditions = promotionConditions;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "id='" + id + '\'' +
                ", orderId='" + orderId + '\'' +
                ", discountAmount=" + discountAmount +
                ", status=" + status +
                ", promotionConditions=" + promotionConditions +
                '}';
    }
}
