package org.egov.pg.service.gateways.atom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.egov.pg.models.Transaction;
import org.egov.tracer.model.CustomException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ResponseParsingUtils {
	
	@Getter
	@Setter
	@Builder
	public static class RedirectionURLResponse {
		
		@Builder.Default
		private String url = "";
		
		@Builder.Default
		private List<Param> params = new ArrayList<Param>(4);
		
		public String toURLString() {
			return url+"?"+params.stream().map(param -> param.name+"="+param.value).reduce("", (a,b) -> a+"&"+b);
		}
		
		public String toString() {
			return this.toURLString();
		}
	}
	
	@Getter
	@Setter
	@Builder
	public static class Param {
		private String name;
		private String value;
	}

	static String constructRedirectURI(String xmlString) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(Charset.forName("UTF-8")));
		Document document = builder.parse(inputStream);
		NodeList responseList = document.getElementsByTagName("RESPONSE");
		if (responseList.getLength() == 0) {
			throw new CustomException("ATOM_EXCEPTION","Invalid response from ATOM Gateway");
		}
		RedirectionURLResponse redirectionURLResponse = RedirectionURLResponse.builder().build();
		Node responseElement = responseList.item(0);
		NodeList children = responseElement.getChildNodes();
		for(int i=0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeName() == "url") {
				redirectionURLResponse.setUrl(child.getTextContent());
			} else if (child.getNodeName() == "param") {
				String name = child.getAttributes().getNamedItem("name").getTextContent();
				redirectionURLResponse.getParams().add(Param.builder().name(name).value(child.getTextContent()).build());
			}
		}
		
		return redirectionURLResponse.toURLString();
	}

	/**
	 * <VerifyOutput MerchantID="160" MerchantTxnID="PB_PG_2020_04_20_000022_14" AMT="700.0" VERIFIED="FAILED" 
	 * 	BID="7000049903081" bankname="Atom Bank" atomtxnId="700004990308" discriminator="NB" surcharge="0.00" 
	 *  CardNumber="" TxnDate="2020-04-20 10:23:23" UDF9="null" reconstatus="NRNS" sdt="null"/>
	 * @param xmlString
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static StatusResponse parseStatusResponse(String xmlString) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(Charset.forName("UTF-8")));
		Document document = builder.parse(inputStream);
		Node rootNode = document.getDocumentElement();
		NamedNodeMap nodeMap = rootNode.getAttributes();
		StatusResponse statusResponse = new StatusResponse();
		for(int i=0; i<nodeMap.getLength(); i++) {
			Node node = nodeMap.item(i);
			switch(node.getNodeName()) {
				case "MerchantTxnID":
					statusResponse.setTransactionId(node.getTextContent());
					break;
				case "AMT":
					statusResponse.setAmount(node.getTextContent());
					break;
				case "VERIFIED":
					statusResponse.setGatewayTransactionStatus(node.getTextContent());
					Transaction.TxnStatusEnum statusEnum = convertStatusToEnum(node.getTextContent());
					statusResponse.setTransactionStatus(statusEnum);
					if (statusEnum == Transaction.TxnStatusEnum.SUCCESS || statusEnum == Transaction.TxnStatusEnum.FAILURE) {						
						statusResponse.setGatewayStatusCode("200");
					} else {
						statusResponse.setGatewayStatusCode("400");
					}
					break;
				case "atomtxnId":
					statusResponse.setGatewayTransactionId(node.getTextContent());
					break;
				case "BID":
					statusResponse.setBankId(node.getTextContent());
					break;
				case "CardNumber":
					statusResponse.setCardNumber(node.getTextContent());
					break;
				case "bankname":
					statusResponse.setBankName(node.getTextContent());
				case "discriminator":
					statusResponse.setGatewayPaymentMode(getGatewayPaymentMode(node.getTextContent()));
					break;
			}
		}
		return statusResponse;
	}
	
	private static String getGatewayPaymentMode(String discriminator) {
		switch(discriminator) {
			case "NB":
				return "NetBanking";
			case "CC":
				return "Credit Card";
			case "DC":
				return "Debit Card";
			case "MX":
				return "Amex Card";
			default:
				return "Unknown";
		}
	}
	
	private static Transaction.TxnStatusEnum convertStatusToEnum(String status) {
		switch(status) {
			case "SUCCESS":
				return Transaction.TxnStatusEnum.SUCCESS;
			case "FAILED":
				return Transaction.TxnStatusEnum.FAILURE;
			case "Invalid date format":
				return Transaction.TxnStatusEnum.PENDING;
			case "NODATA":
				return Transaction.TxnStatusEnum.PENDING;
			default:
				return Transaction.TxnStatusEnum.PENDING;
		}
	}
	
	@Getter
	@Setter
	public static class StatusResponse {
		private String transactionId;
		private Transaction.TxnStatusEnum transactionStatus;
		private String bankId;
		private String bankName;
		private String gatewayTransactionId;
		private String gatewayTransactionStatus;
		private String cardNumber;
		private String amount;
		private String gatewayPaymentMode;
		private String gatewayStatusCode;
	}
	
	
}
