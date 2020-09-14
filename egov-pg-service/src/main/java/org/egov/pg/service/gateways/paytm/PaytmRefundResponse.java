package org.egov.pg.service.gateways.paytm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaytmRefundResponse {

	@JsonProperty("orderId")
	private String orderId;

	@JsonProperty("mid")
	private String mid;

	@JsonProperty("refId")
	private String refId;

	@JsonProperty("refundId")
	private String refundId;

	@JsonProperty("txnId")
	private String txnId;

	@JsonProperty("refundAmount")
	private String refundAmount;

	@JsonProperty("resultInfo")
	private PaytmRefunResultInfo resultInfo;

	@JsonProperty("source")
	private String source;

	@JsonProperty("userCreditInitiateStatus")
	private String userCreditInitiateStatus;

	@JsonProperty("acceptRefundTimestamp")
	private String acceptRefundTimestamp;

	@JsonProperty("txnTimestamp")
	private String txnTimestamp;

	@JsonProperty("acceptRefundStatus")
	private String acceptRefundStatus;

	@JsonProperty("totalRefundAmount")
	private String totalRefundAmount;

}
