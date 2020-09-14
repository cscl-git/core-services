CREATE TABLE "eg_pg_transactions_refund"
(
  txn_refund_id character varying(128) NOT NULL,
  txn_id character varying(128) NOT NULL,
  txn_amount numeric(15,2) NOT NULL,
  txn_refund_amount numeric(15,2) NOT NULL,
  txn_refund_status character varying(256) NOT NULL,
  txn_refund_status_msg character varying(256),
  gateway character varying(256) NOT NULL,
  gateway_txn_id character varying(128) DEFAULT NULL::character varying,
  gateway_refund_txn_id character varying(128) DEFAULT NULL::character varying,
  gateway_refund_status_code character varying(128) DEFAULT NULL::character varying,
  gateway_refund_status_msg character varying(128) DEFAULT NULL::character varying,
  created_by character varying(256),
  created_time bigint,
  last_modified_by character varying(256),
  last_modified_time bigint,
  refund_additional_details jsonb,
  tenant_id character varying(50),
  CONSTRAINT eg_pg_transactions_refund_pkey PRIMARY KEY (txn_refund_id),
  CONSTRAINT eg_pg_transactions_refund_unique_key UNIQUE (txn_refund_id, txn_id, tenant_id)
);