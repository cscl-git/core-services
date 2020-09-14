package org.egov.pg.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.egov.pg.models.AuditDetails;
import org.egov.pg.models.RefundTransaction;
import org.egov.pg.models.TaxAndPayment;
import org.egov.pg.models.Transaction;
import org.egov.pg.web.models.User;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.util.Objects.isNull;

public class RefundTransactionRowMapper implements RowMapper<RefundTransaction> {

	private static ObjectMapper objectMapper = new ObjectMapper();
	private static ObjectReader taxAndPaymentsReader = objectMapper
			.readerFor(objectMapper.getTypeFactory().constructCollectionType(List.class, TaxAndPayment.class));

	@Override
	public RefundTransaction mapRow(ResultSet resultSet, int i) throws SQLException {

		AuditDetails auditDetails = new AuditDetails(resultSet.getString("created_by"),
				resultSet.getLong("created_time"), resultSet.getString("last_modified_by"),
				resultSet.getLong("last_modified_time"));

		JsonNode additionalDetails = null;

		if (!isNull(resultSet.getObject("refund_additional_details"))) {
			String additionalDetailsJson = ((PGobject) resultSet.getObject("refund_additional_details")).getValue();
			try {
				additionalDetails = objectMapper.readTree(additionalDetailsJson);
			} catch (IOException e) {
				throw new CustomException("REFUND_TXN_FETCH_FAILED", "Failed to deserialize data");
			}
		}

		return RefundTransaction.builder().txnRefundId(resultSet.getString("txn_refund_id"))
				.txnId(resultSet.getString("txn_id")).txnAmount(resultSet.getString("txn_amount"))
				.refundAmount(resultSet.getString("txn_refund_amount"))
				.txnStatus(resultSet.getString("txn_refund_status"))
				.txnStatusMsg(resultSet.getString("txn_refund_status_msg")).gateway(resultSet.getString("gateway"))
				.tenantId(resultSet.getString("tenant_id")).gatewayTxnId(resultSet.getString("gateway_txn_id"))
				.gatewayRefundTxnId(resultSet.getString("gateway_refund_txn_id"))
				.gatewayRefundStatusCode(resultSet.getString("gateway_refund_status_code"))
				.gatewayRefundStatusMsg(resultSet.getString("gateway_refund_status_msg"))
				.additionalDetails(additionalDetails).auditDetails(auditDetails).build();
	}
}
