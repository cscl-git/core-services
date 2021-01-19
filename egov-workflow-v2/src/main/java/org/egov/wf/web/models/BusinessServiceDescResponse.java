package org.egov.wf.web.models;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
@Data
@Builder
@ToString
public class BusinessServiceDescResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("businessServiceDescription")
    @Valid
    @NotNull
    private List<BusinessDesc> businessServiceDescription;
}
