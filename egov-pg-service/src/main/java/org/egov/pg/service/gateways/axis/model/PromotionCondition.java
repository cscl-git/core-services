package org.egov.pg.service.gateways.axis.model;

/**
 * Represents a condition under which the promotion is applied. Typically used for representing card_number
 * which has to be used for completing an order.
 */
public class PromotionCondition {
    String dimension;
    String value;

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
