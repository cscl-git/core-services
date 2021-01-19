package org.egov.wf.web.models;

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
public class BusinessServiceDesc {

    @Size(max=256)
    @JsonProperty("businessService")
    private String businessService = null;

    @JsonProperty("businessServiceDescription")
    private String businessServiceDescription = null;
}
