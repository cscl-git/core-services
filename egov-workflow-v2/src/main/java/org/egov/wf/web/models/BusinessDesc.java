package org.egov.wf.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BusinessDesc {


    @Size(max=256)
    @JsonProperty("tenantId")
    private String tenantId = null;

    @Size(max=256)
    @JsonProperty("business")
    private String business = null;
    
    @JsonProperty("businessService")
    private List<BusinessServiceDesc> businessService = null;

    public BusinessDesc addBusinessService(BusinessServiceDesc businessServiceDesc) {
        if (this.businessService == null) {
        this.businessService = new ArrayList<>();
        }
    this.businessService.add(businessServiceDesc);
    return this;
    }
}
