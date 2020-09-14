package org.egov.pg.web.models;

import java.util.List;

import javax.validation.Valid;

import org.egov.pg.models.RefundTransaction;

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
public class RefundTransactionResponse {

	@JsonProperty("ResponseInfo")
	@Valid
	private ResponseInfo responseInfo;

	@JsonProperty("RefundTransaction")
	@Valid
	private List<RefundTransaction> refundTransaction;
}
