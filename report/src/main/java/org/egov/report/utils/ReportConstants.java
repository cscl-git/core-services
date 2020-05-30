package org.egov.report.utils;

import org.springframework.stereotype.Component;


@Component
public class ReportConstants {
	
	private ReportConstants() {}

	public static final String MDMS_PGR_MOD_NAME = "RAINMAKER-PGR";
	public static final String MDMS_AUTOROUTING_ESCALATION_MAP_MASTER_NAME = "AutoroutingEscalationMap";
	public static final String JSONPATH_AUTOROUTING_MAP_CODES = "$.MdmsRes.RAINMAKER-PGR.AutoroutingEscalationMap";
	public static final String AUTOROUTING_ESCALATING_OFFICER1_JSONPATH = "$.escalationOfficer1";
	public static final String AUTOROUTING_ESCALATING_OFFICER2_JSONPATH = "$.escalationOfficer2";
	public static final String AUTOROUTING_CATEGORY_JSONPATH = "$.category";
	public static final String MDMS_AUTOROUTING_ESCALATION_OFFICER1_NAME = "escalationOfficer1";
	public static final String MDMS_AUTOROUTING_ESCALATION_OFFICER2_NAME = "escalationOfficer2";
	public static final String PGR_MODULE = "rainmaker-pgr";
	public static final String PGR_ESCALATION_OFFICER_REPORT = "EscalationOfficerReport";
	public static final String ROLE_ESCALATION_OFFICER1 = "Escalation Officer1";
	public static final String ROLE_ESCALATION_OFFICER2 = "Escalation Officer2";
	public static final String PGR_EMPLOYEE_REPORT = "EmployeeReport";
		
}