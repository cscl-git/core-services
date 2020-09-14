package org.egov.pg.service.gateways.paytm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaytmRefunResultInfo {

	@JsonProperty("resultStatus")
	private String resultStatus;

	@JsonProperty("resultCode")
	private String resultCode;

	@JsonProperty("resultMsg")
	private String resultMsg;
}
