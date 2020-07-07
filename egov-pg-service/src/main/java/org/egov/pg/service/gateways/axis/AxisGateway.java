package org.egov.pg.service.gateways.axis;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.egov.pg.models.Transaction;
import org.egov.pg.models.Transaction.TxnStatusEnum;
import org.egov.pg.service.Gateway;
import org.egov.pg.service.gateways.axis.request.CreateOrderRequest;
import org.egov.pg.service.gateways.axis.request.GetOrderStatusRequest;
import org.egov.pg.service.gateways.axis.response.GetOrderStatusResponse;
import org.egov.pg.utils.Utils;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
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
	}

	@Override
	public URI generateRedirectURI(Transaction transaction) {

		JusPayPaymentService juspayService = new JusPayPaymentService().withKey(API_KEY).setBaseUrl(MERCHANT_URL_PAY)
				.withMerchantId(MERCHANT_ID);
		CreateOrderRequest createOrderRequest = new CreateOrderRequest().withOrderId(transaction.getTxnId())
				.withAmount(Double.valueOf(transaction.getTxnAmount()));

		createOrderRequest
				.setCustomerEmail(transaction.getUser().getEmailId() == null ? "" : transaction.getUser().getEmailId());
		createOrderRequest.setCustomerPhone(transaction.getUser().getMobileNumber());
		createOrderRequest.setCustomerId(transaction.getUser().getUuid());
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

		if (orderStatusResponse.getStatus().equalsIgnoreCase("ORDER_SUCCEEDED")) {
			return Transaction.builder().txnId(orderStatusResponse.getTxnId())
					.txnAmount(Utils.convertPaiseToRupee(orderStatusResponse.getAmount().toString()))
					.txnStatus(TxnStatusEnum.SUCCESS).gatewayTxnId(orderStatusResponse.getOrderId())
					.gatewayPaymentMode("").gatewayStatusCode(orderStatusResponse.getStatusId().toString())
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
		return "vpc_MerchTxnRef";
	}

	private Transaction fetchStatusFromGateway(Transaction currentStatus) {
		Map<String, String> fields = new HashMap<>();

		String txnRef = StringUtils.isEmpty(currentStatus.getModule()) ? currentStatus.getTxnId()
				: currentStatus.getModule() + "-" + currentStatus.getTxnId();

		// fields.put("vpc_Version", VPC_VERSION);
		// fields.put("vpc_Command", VPC_COMMAND_STATUS);
		// fields.put("vpc_AccessCode", VPC_ACCESS_CODE);
		// fields.put("vpc_Merchant", MERCHANT_ID);
		// fields.put("vpc_MerchTxnRef", txnRef);
		// fields.put("vpc_User", AMA_USER);
		// fields.put("vpc_Password", AMA_PWD);

		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		fields.forEach(queryParams::add);

		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(MERCHANT_URL_STATUS).queryParams(queryParams)
				.build().encode();

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(uriComponents.toUriString(), "", String.class);

			log.info(response.getBody());

			Map<String, List<String>> responseParams = AxisUtils.splitQuery(response.getBody());

			log.info(responseParams.toString());

			return transformRawResponse(responseParams, currentStatus);
		} catch (RestClientException e) {
			log.error("Unable to fetch status from payment gateway for txnid: " + currentStatus.getTxnId(), e);
			throw new ServiceCallException("Error occurred while fetching status from payment gateway");
		}
	}

	private Transaction transformRawResponse(Map<String, List<String>> resp, Transaction currentStatus) {

		Transaction.TxnStatusEnum status;

		String respMsg = "";
		List<String> respCodeList = resp.get("vpc_TxnResponseCode");
		if (Objects.isNull(respCodeList) || respCodeList.isEmpty()) {
			log.error("Transaction not found in the payment gateway");
			return currentStatus;
		}

		String respCode = respCodeList.get(0);
		// TODO Handle error conditions where we dont have response codes?

		switch (respCode) {
		case "0":
			respMsg = "Transaction Successful";
			break;
		case "1":
			respMsg = "Transaction Declined";
			break;
		case "2":
			respMsg = "Bank Declined Transaction";
			break;
		case "3":
			respMsg = "No Reply from Bank";
			break;
		case "4":
			respMsg = "Expired Card";
			break;
		case "5":
			respMsg = "Insufficient Funds";
			break;
		case "6":
			respMsg = "Error Communicating with Bank";
			break;
		case "7":
			respMsg = "Payment Server detected an error";
			break;
		case "8":
			respMsg = "Transaction Type Not Supported";
			break;
		case "9":
			respMsg = "Bank declined transaction (Do not contact Bank)";
			break;
		case "A":
			respMsg = "Transaction Aborted";
			break;
		case "B":
			respMsg = "Transaction Declined - Contact the Bank";
			break;
		case "C":
			respMsg = "Transaction Cancelled";
			break;
		case "D":
			respMsg = "Deferred transaction has been received and is awaiting processing";
			break;
		case "E":
			respMsg = "Transaction Declined - Refer to card issuer";
			break;
		case "F":
			respMsg = "3-D Secure Authentication failed";
			break;
		case "I":
			respMsg = "Card Security Code verification failed";
			break;
		case "L":
			respMsg = "Shopping Transaction Locked (Please try the transaction again later)";
			break;
		case "M":
			respMsg = "Transaction Submitted (No response from acquirer)";
			break;
		case "N":
			respMsg = "Cardholder is not enrolled in Authentication scheme";
			break;
		case "P":
			respMsg = "Transaction has been received by the Payment Adaptor and is being processed";
			break;
		case "R":
			respMsg = "Transaction was not processed - Reached limit of retry attempts allowed";
			break;
		case "S":
			respMsg = "Duplicate SessionID";
			break;
		case "T":
			respMsg = "Address Verification Failed";
			break;
		case "U":
			respMsg = "Card Security Code Failed";
			break;
		case "V":
			respMsg = "Address Verification and Card Security Code Failed";
			break;
		case "?":
			respMsg = "Transaction status is unknown";
			break;
		default:
			respMsg = "Unable to be determined";
			break;
		}

		if (respCode.equalsIgnoreCase("0")) {
			status = Transaction.TxnStatusEnum.SUCCESS;
			return Transaction.builder().txnId(currentStatus.getTxnId())
					.txnAmount(Utils.convertPaiseToRupee(resp.get("vpc_Amount").get(0))).txnStatus(status)
					.gatewayTxnId(resp.get("vpc_TransactionNo").get(0)).gatewayPaymentMode(resp.get("vpc_Card").get(0))
					.gatewayStatusCode(respCode).gatewayStatusMsg(respMsg).responseJson(resp).build();
		} else {
			status = Transaction.TxnStatusEnum.FAILURE;
			return Transaction.builder().txnId(currentStatus.getTxnId())
					.txnAmount(Utils.convertPaiseToRupee(resp.get("vpc_Amount").get(0))).txnStatus(status)
					.gatewayTxnId(resp.get("vpc_TransactionNo").get(0)).gatewayStatusCode(respCode)
					.gatewayStatusMsg(respMsg).responseJson(resp).build();
		}

	}

}
