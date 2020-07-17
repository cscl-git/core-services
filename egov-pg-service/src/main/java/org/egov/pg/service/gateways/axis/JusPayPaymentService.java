package org.egov.pg.service.gateways.axis;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.egov.pg.service.gateways.axis.model.Refund;
import org.egov.pg.service.gateways.axis.request.CreateOrderRequest;
import org.egov.pg.service.gateways.axis.request.GetOrderStatusRequest;
import org.egov.pg.service.gateways.axis.response.GetOrderStatusResponse;
import org.egov.pg.service.gateways.axis.response.PaymentCardResponse;
import org.egov.pg.service.gateways.axis.response.PaymentGatewayResponse;
import org.egov.pg.utils.ISO8601DateParser;
import org.egov.pg.web.controllers.TransactionsApiController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JusPayPaymentService {

	private static final Logger LOG = Logger.getLogger(JusPayPaymentService.class);

	private int connectionTimeout = 5 * 1000;
	private int readTimeout = 5 * 1000;
	private String baseUrl;
	private String key;
	private String merchantId;
	// private Environment environment;

	public void setKey(String key) {
		this.key = key;
	}

	public JusPayPaymentService withKey(String key) {
		this.key = key;
		return this;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public JusPayPaymentService withMerchantId(String merchantId) {
		this.merchantId = merchantId;
		return this;
	}

	public JusPayPaymentService setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	private String serializeParams(Map<String, String> parameters) {

		StringBuilder bufferUrl = new StringBuilder();
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			bufferUrl.append(entry.getKey());
			bufferUrl.append("=");
			bufferUrl.append(entry.getValue());
			bufferUrl.append("&");
		}

		return bufferUrl.substring(0, bufferUrl.length() - 1);
	}

	/**
	 * It opens the connection to the given endPoint and returns the http response
	 * as String.
	 *
	 * @param endPoint
	 *            - The HTTP URL of the request
	 * @return HTTP response as string
	 */
	private String makeServiceCall(String endPoint, String encodedParams) {
		log.info("Axis Gatway makeServiceCall() Create Order Request Endpoint : " + endPoint);
		log.info("Axis Gatway makeServiceCall() Create Order Request Parameters : " + encodedParams);
		HttpsURLConnection connection = null;
		StringBuilder buffer = new StringBuilder();

		try {
			URL url = new URL(endPoint);
			connection = (HttpsURLConnection) url.openConnection();

			String encodedKey = new String(Base64.encodeBase64(this.key.getBytes()));
			encodedKey = encodedKey.replaceAll("\n", "");
			connection.setRequestProperty("Authorization", "Basic " + encodedKey);
			connection.setRequestProperty("version", "2015-08-18");

			connection.setConnectTimeout(connectionTimeout);
			connection.setReadTimeout(readTimeout);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(encodedParams.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Setup the POST payload
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(encodedParams);
			wr.flush();
			wr.close();

			// Read the response
			InputStream inputStream = connection.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line;
			while ((line = in.readLine()) != null)
				buffer.append(line);

			log.info("Axis Gatway makeServiceCall() Create Order Request Success : " + buffer.toString());

			return buffer.toString();
		} catch (IOException e) {
			// lets read error stream and print out the reason for failure
			InputStream errorStream = connection.getErrorStream();
			BufferedReader in = null;
			String line;
			StringBuffer errorBuffer = new StringBuffer();
			try {
				if (errorStream != null) {
					in = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"));
					while ((line = in.readLine()) != null)
						errorBuffer.append(line);
					System.out.println(errorBuffer.toString());
				}
			} catch (Exception readException) {
				log.info("Axis Gatway makeServiceCall() Create Order Request Failed : "
						+ readException.getStackTrace());
				throw new RuntimeException("Exception while trying to make service call to Juspay", e);
			}
			log.info("Axis Gatwate makeServiceCall() Create Order Request Failed : " + e.getStackTrace());
			throw new RuntimeException("Exception while trying to make service call to Juspay", e);
		} catch (Exception e) {
			log.info("Axis Gatway makeServiceCall() Create Order Request Failed : " + e.getStackTrace());
			throw new RuntimeException("Exception while trying to make service call to Juspay", e);
		}
	}

	/**
	 * Creates a new order and returns the CreateOrderResponse associated with that.
	 *
	 * @param createOrderRequest
	 *            - CreateOrderRequest with all required params
	 * @return CreateOrderResponse for the given request
	 */
	public String createOrder(CreateOrderRequest createOrderRequest) {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("order_id", createOrderRequest.getOrderId() == null ? "" : createOrderRequest.getOrderId());
		params.put("amount",
				String.valueOf(createOrderRequest.getAmount() == null ? 0 : createOrderRequest.getAmount()));
		params.put("customer_id", createOrderRequest.getCustomerId());
		params.put("currency", createOrderRequest.getCurrency() == null ? "" : createOrderRequest.getCurrency());
		params.put("return_url", createOrderRequest.getReturnUrl() == null ? "" : createOrderRequest.getReturnUrl());

		/* Below are additional request parameter */
		params.put("customer_email", createOrderRequest.getCustomerEmail());
		params.put("customer_phone",
				createOrderRequest.getCustomerPhone() == null ? "" : createOrderRequest.getCustomerPhone());
		if (createOrderRequest.getGatewayId() != 0) {
			params.put("gateway_id", Integer.toString(createOrderRequest.getGatewayId()));
		}
		params.put("currency", createOrderRequest.getCurrency() == null ? "" : createOrderRequest.getCurrency());
		params.put("product_id", createOrderRequest.getProductId() == null ? "" : createOrderRequest.getProductId());
		params.put("description",
				createOrderRequest.getDescription() == null ? "" : createOrderRequest.getDescription());

		// Billing and shipping addresses
		params.put("billing_address_first_name", createOrderRequest.getBillingAddressFirstName() == null ? ""
				: createOrderRequest.getBillingAddressFirstName());
		params.put("billing_address_last_name", createOrderRequest.getBillingAddressLastName() == null ? ""
				: createOrderRequest.getBillingAddressLastName());
		params.put("billing_address_line1",
				createOrderRequest.getBillingAddressLine1() == null ? "" : createOrderRequest.getBillingAddressLine1());
		params.put("billing_address_line2",
				createOrderRequest.getBillingAddressLine2() == null ? "" : createOrderRequest.getBillingAddressLine2());
		params.put("billing_address_line3",
				createOrderRequest.getBillingAddressLine3() == null ? "" : createOrderRequest.getBillingAddressLine3());
		params.put("billing_address_city",
				createOrderRequest.getBillingAddressCity() == null ? "" : createOrderRequest.getBillingAddressCity());
		params.put("billing_address_state",
				createOrderRequest.getBillingAddressState() == null ? "" : createOrderRequest.getBillingAddressState());
		params.put("billing_address_postal_code", createOrderRequest.getBillingAddressPostalCode() == null ? ""
				: createOrderRequest.getBillingAddressPostalCode());
		params.put("billing_address_phone",
				createOrderRequest.getBillingAddressPhone() == null ? "" : createOrderRequest.getBillingAddressPhone());
		params.put("billing_address_country_code_iso", createOrderRequest.getBillingAddressCountryCodeIso() == null ? ""
				: createOrderRequest.getBillingAddressCountryCodeIso());
		params.put("billing_address_country", createOrderRequest.getBillingAddressCountry() == null ? ""
				: createOrderRequest.getBillingAddressCountry());
		params.put("shipping_address_first_name", createOrderRequest.getShippingAddressFirstName() == null ? ""
				: createOrderRequest.getShippingAddressFirstName());
		params.put("shipping_address_last_name", createOrderRequest.getShippingAddressLastName() == null ? ""
				: createOrderRequest.getShippingAddressLastName());
		params.put("shipping_address_line1", createOrderRequest.getShippingAddressLine1() == null ? ""
				: createOrderRequest.getShippingAddressLine1());
		params.put("shipping_address_line2", createOrderRequest.getShippingAddressLine2() == null ? ""
				: createOrderRequest.getShippingAddressLine2());
		params.put("shipping_address_line3", createOrderRequest.getShippingAddressLine3() == null ? ""
				: createOrderRequest.getShippingAddressLine3());
		params.put("shipping_address_city",
				createOrderRequest.getShippingAddressCity() == null ? "" : createOrderRequest.getShippingAddressCity());
		params.put("shipping_address_state", createOrderRequest.getShippingAddressState() == null ? ""
				: createOrderRequest.getShippingAddressState());
		params.put("shipping_address_postal_code", createOrderRequest.getShippingAddressPostalCode() == null ? ""
				: createOrderRequest.getShippingAddressPostalCode());
		params.put("shipping_address_phone", createOrderRequest.getShippingAddressPhone() == null ? ""
				: createOrderRequest.getShippingAddressPhone());
		params.put("shipping_address_country_code_iso",
				createOrderRequest.getShippingAddressCountryCodeIso() == null ? ""
						: createOrderRequest.getShippingAddressCountryCodeIso());
		params.put("shipping_address_country", createOrderRequest.getShippingAddressCountry() == null ? ""
				: createOrderRequest.getShippingAddressCountry());

		// Optional parameters
		params.put("udf1", createOrderRequest.getUdf1() == null ? "" : createOrderRequest.getUdf1());
		params.put("udf2", createOrderRequest.getUdf2() == null ? "" : createOrderRequest.getUdf2());
		params.put("udf3", createOrderRequest.getUdf3() == null ? "" : createOrderRequest.getUdf3());
		params.put("udf4", createOrderRequest.getUdf4() == null ? "" : createOrderRequest.getUdf4());
		params.put("udf5", createOrderRequest.getUdf5() == null ? "" : createOrderRequest.getUdf5());
		params.put("udf6", createOrderRequest.getUdf6() == null ? "" : createOrderRequest.getUdf6());
		params.put("udf7", createOrderRequest.getUdf7() == null ? "" : createOrderRequest.getUdf7());
		params.put("udf8", createOrderRequest.getUdf8() == null ? "" : createOrderRequest.getUdf8());
		params.put("udf9", createOrderRequest.getUdf9() == null ? "" : createOrderRequest.getUdf9());
		params.put("udf10", createOrderRequest.getUdf10() == null ? "" : createOrderRequest.getUdf10());

		String serializedParams = serializeParams(params);
		// System.out.println("serializedParams : " + serializedParams);
		String url = baseUrl + "/orders";
		String response = makeServiceCall(url, serializedParams);
		// System.out.println("response :" + response);
		JSONObject jsonResponse = (JSONObject) JSONValue.parse(response);
		String status = (String) jsonResponse.get("status");
		JSONObject redirectResponse = (JSONObject) jsonResponse.get("payment_links");

		String redirectUrls = "";
		if (status.equals("CREATED") || status.equals("NEW")) {
			redirectUrls = (String) redirectResponse.get("web");
		}
		return redirectUrls;
	}

	protected Double getDoubleValue(Object inputObject) {
		if (inputObject instanceof Long) {
			return ((Long) inputObject).doubleValue();
		} else if (inputObject instanceof Double) {
			return ((Double) inputObject);
		} else {
			LOG.warn("Can't seem to understand the input");
			return null;
		}
	}

	public GetOrderStatusResponse getOrderStatus(GetOrderStatusRequest orderStatusRequest) {
		LinkedHashMap<String, String> params = new LinkedHashMap<>();
		params.put("order_id", orderStatusRequest.getOrderId());
		if (orderStatusRequest.getForce() != null && orderStatusRequest.getForce()) {
			params.put("force", orderStatusRequest.getForce().toString());
		}
		String serializedParams = "";
		String url = baseUrl + "/orders/" + orderStatusRequest.getOrderId();

		String response = makeServiceCall(url, serializedParams);
		JSONObject jsonResponse = (JSONObject) JSONValue.parse(response);

		return assembleOrderStatusResponse(jsonResponse, new GetOrderStatusResponse());
	}

	protected GetOrderStatusResponse assembleOrderStatusResponse(JSONObject jsonResponse,
			GetOrderStatusResponse target) {
		GetOrderStatusResponse orderStatusResponse = target;
		orderStatusResponse.setMerchantId((String) jsonResponse.get("merchant_id"));
		orderStatusResponse.setOrderId((String) jsonResponse.get("order_id"));
		orderStatusResponse.setStatus((String) jsonResponse.get("status"));
		orderStatusResponse.setStatusId((Long) jsonResponse.get("status_id"));
		orderStatusResponse.setTxnId((String) jsonResponse.get("txn_id"));
		orderStatusResponse.setGatewayId((Long) jsonResponse.get("gateway_id"));

		orderStatusResponse.setAmount(getDoubleValue(jsonResponse.get("amount")));
		orderStatusResponse.setBankErrorCode((String) jsonResponse.get("bank_error_code"));
		orderStatusResponse.setBankErrorMessage((String) jsonResponse.get("bank_error_message"));

		orderStatusResponse.setUdf1((String) jsonResponse.get("udf1"));
		orderStatusResponse.setUdf2((String) jsonResponse.get("udf2"));
		orderStatusResponse.setUdf3((String) jsonResponse.get("udf3"));
		orderStatusResponse.setUdf4((String) jsonResponse.get("udf4"));
		orderStatusResponse.setUdf5((String) jsonResponse.get("udf5"));
		orderStatusResponse.setUdf6((String) jsonResponse.get("udf6"));
		orderStatusResponse.setUdf7((String) jsonResponse.get("udf7"));
		orderStatusResponse.setUdf8((String) jsonResponse.get("udf8"));
		orderStatusResponse.setUdf9((String) jsonResponse.get("udf9"));
		orderStatusResponse.setUdf10((String) jsonResponse.get("udf10"));

		orderStatusResponse.setAmountRefunded(getDoubleValue(jsonResponse.get("amount_refunded")));
		orderStatusResponse.setRefunded((Boolean) jsonResponse.get("refunded"));

		JSONObject gatewayResponse = (JSONObject) jsonResponse.get("payment_gateway_response");

		JSONObject cardResponse = (JSONObject) jsonResponse.get("card");

		if (cardResponse != null) {
			PaymentCardResponse paymentCardResponse = new PaymentCardResponse();
			paymentCardResponse.setLastFourDigits((String) cardResponse.get("last_four_digits"));
			paymentCardResponse.setCardIsin((String) cardResponse.get("card_isin"));
			paymentCardResponse.setExpiryMonth((String) cardResponse.get("expiry_month"));
			paymentCardResponse.setExpiryYear((String) cardResponse.get("expiry_year"));
			paymentCardResponse.setNameOnCard((String) cardResponse.get("name_on_card"));
			paymentCardResponse.setCardType((String) cardResponse.get("card_type"));
			paymentCardResponse.setCardIssuer((String) cardResponse.get("card_issuer"));
			paymentCardResponse.setCardBrand((String) cardResponse.get("card_brand"));
			orderStatusResponse.setPaymentCardResponse(paymentCardResponse);
		}

		if (gatewayResponse != null) {
			PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse();
			paymentGatewayResponse.setRootReferenceNumber((String) gatewayResponse.get("rrn"));
			paymentGatewayResponse.setResponseCode((String) gatewayResponse.get("resp_code"));
			paymentGatewayResponse.setResponseMessage((String) gatewayResponse.get("resp_message"));
			paymentGatewayResponse.setTxnId((String) gatewayResponse.get("txn_id"));
			paymentGatewayResponse.setExternalGatewayTxnId((String) gatewayResponse.get("epg_txn_id"));
			paymentGatewayResponse.setAuthIdCode((String) gatewayResponse.get("auth_id_code"));
			orderStatusResponse.setPaymentGatewayResponse(paymentGatewayResponse);
		}

		JSONObject promotionResponse = (JSONObject) jsonResponse.get("promotion");
		if (promotionResponse != null) {
			// Promotion promotion = assemblePromotionObjectFromJSON(promotionResponse);
			// orderStatusResponse.setPromotion(promotion);
		}

		JSONArray refunds = (JSONArray) jsonResponse.get("refunds");
		if (refunds != null && refunds.size() > 0) {
			List<Refund> refundList = new ArrayList<Refund>(refunds.size());
			for (Iterator refundIter = refunds.iterator(); refundIter.hasNext();) {
				JSONObject refundEntry = (JSONObject) refundIter.next();
				Refund refund = new Refund();
				refund.setId((String) refundEntry.get("id"));
				refund.setReference((String) refundEntry.get("ref"));
				refund.setAmount(getDoubleValue(refundEntry.get("amount")));
				refund.setUniqueRequestId((String) refundEntry.get("unique_request_id"));
				refund.setStatus((String) refundEntry.get("status"));
				try {
					refund.setCreated(ISO8601DateParser.parse((String) refundEntry.get("created")));
				} catch (ParseException e) {
					LOG.error("Exception while trying to parse date created. Skipping the field", e);
				}
				refundList.add(refund);
			}
			orderStatusResponse.setRefunds(refundList);
		}
		return orderStatusResponse;
	}

}
