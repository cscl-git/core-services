package org.egov.pg.service.gateways.axis.model;

import java.util.Date;

public class Refund {

    private String id;
    private String reference;
    private Double amount;
    private Date created;
    private String uniqueRequestId;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getUniqueRequestId() {
        return uniqueRequestId;
    }

    public void setUniqueRequestId(String uniqueRequestId) {
        this.uniqueRequestId = uniqueRequestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Refund{" +
                "id='" + id + '\'' +
                ", reference='" + reference + '\'' +
                ", amount=" + amount +
                ", created=" + created +
                ", unique_request_id=" + uniqueRequestId +
                ", status=" + status +
                '}';
    }
}
