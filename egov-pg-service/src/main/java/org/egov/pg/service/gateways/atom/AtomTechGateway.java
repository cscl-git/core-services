package org.egov.pg.service.gateways.atom;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.egov.pg.models.RefundTransaction;
import org.egov.pg.models.Transaction;
import org.egov.pg.service.Gateway;
import org.egov.pg.service.gateways.atom.ResponseParsingUtils.StatusResponse;
import org.egov.pg.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * AXIS Gateway implementation
 */
@Component
@Slf4j
public class AtomTechGateway implements Gateway {

	private final boolean ACTIVE;
	private final String CURRENCY;
	private final String MERCHANT_ID;
	private final String ATOM_PASSWORD;
	private final String MDD;
	private final String CLIENT_CODE;
	private final String TRANSACTION_TYPE;
	private final String PRODUCT_ID;
	private final String REQ_HASH_KEY;
	private final String RESP_HASH_KEY;
	private final String GATEWAY_URL;
	private final String TXN_STATUS_URL;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private static final String GATEWAY_NAME = "atom";

	/**
	 * 
	 * atom.txnscamt=0 atom.custacc=100000036600
	 * 
	 * atom.AESRequestKey=8E41C78439831010F81F61C344B7BFC7
	 * atom.AESResponseKey=8E41C78439831010F81F61C344B7BFC7
	 * atom.requestIV_Salt=8E41C78439831010F81F61C344B7BFC7
	 * atom.responseIV_Salt=8E41C78439831010F81F61C344B7BFC7
	 * 
	 * @param environment
	 * @param restTemplate
	 * @param objectMapper
	 */
	@Autowired
	public AtomTechGateway(Environment environment, RestTemplate restTemplate, ObjectMapper objectMapper) {

		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;

		ACTIVE = Boolean.valueOf(environment.getRequiredProperty("atom.active"));
		CURRENCY = environment.getRequiredProperty("atom.txncurr");
		MERCHANT_ID = environment.getRequiredProperty("atom.login");
		ATOM_PASSWORD = environment.getRequiredProperty("atom.pass");
		TRANSACTION_TYPE = environment.getRequiredProperty("atom.ttype");
		PRODUCT_ID = environment.getRequiredProperty("atom.prodid");
		CLIENT_CODE = environment.getRequiredProperty("atom.clientcode");
		MDD = environment.getRequiredProperty("atom.mdd");
		REQ_HASH_KEY = environment.getRequiredProperty("atom.reqHashKey");
		RESP_HASH_KEY = environment.getRequiredProperty("atom.respHashKey");

		GATEWAY_URL = environment.getRequiredProperty("atom.gateway.url");
		TXN_STATUS_URL = environment.getRequiredProperty("atom.reconcile.url");
	}

	private String getOrDefault(String value, String defaultValue) {
		return value == null ? defaultValue : value;
	}

	@Override
	public URI generateRedirectURI(Transaction transaction) {
		Map<String, String> paramMap = new HashMap<String, String>();

		String transactionId = transaction.getTxnId();
		String amount = String.valueOf(Utils.formatAmtAsRupee(transaction.getTxnAmount()));

		String signature = AtomTechUtils.getEncodedValueWithSha2(REQ_HASH_KEY, MERCHANT_ID, ATOM_PASSWORD,
				TRANSACTION_TYPE, PRODUCT_ID, transactionId, amount, CURRENCY);

		URI uri = UriComponentsBuilder.fromHttpUrl(GATEWAY_URL).queryParam("login", MERCHANT_ID)
				.queryParam("pass", ATOM_PASSWORD).queryParam("ttype", TRANSACTION_TYPE)
				.queryParam("prodid", PRODUCT_ID).queryParam("amt", amount).queryParam("txncurr", CURRENCY)
				.queryParam("txnscamt", amount)
				.queryParam("clientcode", Base64.getEncoder().encodeToString(CLIENT_CODE.getBytes()))
				.queryParam("txnid", transactionId)
				.queryParam("date", new SimpleDateFormat("dd/MM/yy").format(new Date()))
				.queryParam("custacc", "001002003004")
				.queryParam("udf1", getOrDefault(transaction.getUser().getName(), "Citizen"))
				.queryParam("udf2", getOrDefault(transaction.getUser().getEmailId(), "noreply@egovernments.org"))
				.queryParam("udf3", getOrDefault(transaction.getUser().getMobileNumber(), "Citizen"))
				.queryParam("udf4", getOrDefault(transaction.getUser().getTenantId(), "Chandigarh"))
				.queryParam("ru", transaction.getCallbackUrl()).queryParam("signature", signature).build(false).toUri();
		ResponseEntity<String> response = this.restTemplate.getForEntity(uri, String.class);

		try {
			return new URI(ResponseParsingUtils.constructRedirectURI(response.getBody()));
		} catch (URISyntaxException | SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Transaction fetchStatus(Transaction currentStatus, Map<String, String> params) {
		String transactionId = currentStatus.getTxnId();
		long createdTime = currentStatus.getAuditDetails().getCreatedTime();
		URI uri = UriComponentsBuilder.fromHttpUrl(TXN_STATUS_URL).queryParam("merchantid", MERCHANT_ID)
				.queryParam("merchanttxnid", transactionId).queryParam("amt", currentStatus.getTxnAmount())
				.queryParam("tdate", new SimpleDateFormat("yyyy-MM-dd").format(new Date(createdTime))).build(false)
				.toUri();
		ResponseEntity<String> response = this.restTemplate.getForEntity(uri, String.class);
		try {
			String body = response.getBody();
			StatusResponse statusResponse = ResponseParsingUtils.parseStatusResponse(body);
			Transaction newTransaction = Transaction.builder().txnId(currentStatus.getTxnId())
					.txnAmount(Utils.formatAmtAsRupee(statusResponse.getAmount()))
					.txnStatus(statusResponse.getTransactionStatus())
					.gatewayTxnId(statusResponse.getGatewayTransactionId())
					.gatewayPaymentMode(statusResponse.getGatewayPaymentMode())
					.gatewayStatusCode(statusResponse.getGatewayStatusCode())
					.gatewayStatusMsg(statusResponse.getGatewayTransactionStatus()).responseJson("").build();
			return newTransaction;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isActive() {

		return true;
	}

	@Override
	public String gatewayName() {
		return GATEWAY_NAME;
	}

	@Override
	public String transactionIdKeyInResponse() {
		return "transactionId";
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
