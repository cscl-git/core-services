package org.egov.pg.service.gateways.axis;

import java.net.URI;
import java.util.Map;

import org.egov.pg.models.RefundTransaction;
import org.egov.pg.models.Transaction;
import org.egov.pg.models.Transaction.TxnStatusEnum;
import org.egov.pg.service.Gateway;
import org.egov.pg.service.gateways.axis.request.CreateOrderRequest;
import org.egov.pg.service.gateways.axis.request.GetOrderStatusRequest;
import org.egov.pg.service.gateways.axis.response.GetOrderStatusResponse;
import org.egov.pg.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * AXIS Gateway implementation
 */
@Component
@Slf4j
public class AxisGateway implements Gateway {

	private static final String GATEWAY_NAME = "AXIS";

	private final String API_KEY;
	private final String MERCHANT_URL_PAY;
	private final String MERCHANT_URL_STATUS;
	private final String MERCHANT_ID;
	private final boolean ACTIVE;
	private final String CURRENCY;

	private final RestTemplate restTemplate;
	private ObjectMapper objectMapper;

	/**
	 * Initialize by populating all required config parameters
	 *
	 * @param restTemplate
	 *            rest template instance to be used to make REST calls
	 * @param environment
	 *            containing all required config parameters
	 */
	@Autowired
	public AxisGateway(RestTemplate restTemplate, Environment environment, ObjectMapper objectMapper) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;

		API_KEY = environment.getRequiredProperty("axis.api.key");
		MERCHANT_ID = environment.getRequiredProperty("axis.merchant.id");
		MERCHANT_URL_PAY = environment.getRequiredProperty("axis.url.debit");
		MERCHANT_URL_STATUS = environment.getRequiredProperty("axis.url.status");
		ACTIVE = Boolean.valueOf(environment.getRequiredProperty("axis.active"));
		CURRENCY = environment.getRequiredProperty("axis.currency");
	}

	@Override
	public URI generateRedirectURI(Transaction transaction) {
		JusPayPaymentService juspayService = new JusPayPaymentService().withKey(API_KEY).setBaseUrl(MERCHANT_URL_PAY)
				.withMerchantId(MERCHANT_ID);

		CreateOrderRequest createOrderRequest = new CreateOrderRequest().withOrderId(transaction.getTxnId())
				.withAmount(Double.valueOf(transaction.getTxnAmount()));
		createOrderRequest.setCustomerId(transaction.getUser().getId().toString());
		createOrderRequest.setCurrency(CURRENCY);
		createOrderRequest.setReturnUrl(transaction.getCallbackUrl());

		String createOrderResponse = juspayService.createOrder(createOrderRequest);
		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(createOrderResponse).build().encode();
		return uriComponents.toUri();
	}

	@Override
	public Transaction fetchStatus(Transaction currentStatus, Map<String, String> params) {
		JusPayPaymentService juspayService = new JusPayPaymentService().withKey(API_KEY).setBaseUrl(MERCHANT_URL_STATUS)
				.withMerchantId(MERCHANT_ID);
		GetOrderStatusRequest orderStatusRequest = new GetOrderStatusRequest();
		orderStatusRequest.withOrderId(currentStatus.getTxnId());
		GetOrderStatusResponse orderStatusResponse = juspayService.getOrderStatus(orderStatusRequest);
		return getStatusTransaction(orderStatusResponse);
	}

	public Transaction getStatusTransaction(GetOrderStatusResponse orderStatusResponse) {

		if (orderStatusResponse.getStatus().equalsIgnoreCase("CHARGED")) {
			return Transaction.builder().txnId(orderStatusResponse.getTxnId())
					.txnAmount(orderStatusResponse.getAmount().toString()).txnStatus(TxnStatusEnum.SUCCESS)
					.gatewayTxnId(orderStatusResponse.getOrderId()).gatewayPaymentMode("")
					.gatewayStatusCode(orderStatusResponse.getStatusId().toString())
					.gatewayStatusMsg(orderStatusResponse.getStatus()).responseJson(orderStatusResponse).build();
		} else {
			return Transaction.builder().txnId(orderStatusResponse.getTxnId())
					.txnAmount(Utils.convertPaiseToRupee(orderStatusResponse.getAmount().toString()))
					.txnStatus(TxnStatusEnum.FAILURE).gatewayTxnId(orderStatusResponse.getOrderId())
					.gatewayPaymentMode("").gatewayStatusCode(orderStatusResponse.getStatusId().toString())
					.gatewayStatusMsg(orderStatusResponse.getStatus()).responseJson(orderStatusResponse).build();
		}
	}

	@Override
	public boolean isActive() {
		return ACTIVE;
	}

	@Override
	public String gatewayName() {
		return GATEWAY_NAME;
	}

	@Override
	public String transactionIdKeyInResponse() {
		return "";
	}

	@Override
	public RefundTransaction initiateRefund(RefundTransaction transaction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RefundTransaction fetchRefundStatus(RefundTransaction currentStatus) {
		// TODO Auto-generated method stub
		return null;
	}
}
