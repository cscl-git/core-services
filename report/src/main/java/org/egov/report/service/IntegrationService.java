package org.egov.report.service;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.domain.model.RequestInfoWrapper;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.report.repository.ServiceRequestRepository;
import org.egov.report.utils.ReportConstants;
import org.egov.swagger.model.ColumnDetail;
import org.egov.swagger.model.ColumnDetail.TypeEnum;
import org.egov.swagger.model.MetadataResponse;
import org.egov.swagger.model.ReportDefinition;
import org.egov.swagger.model.SearchColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

@Slf4j
@Service
public class IntegrationService {
	
	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndpoint;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
	private ServiceRequestRepository serviceRequestRepository;

    public MetadataResponse getData(ReportDefinition reportDefinition, MetadataResponse metadataResponse, RequestInfo requestInfo, String moduleName) {


        List<SearchColumn> searchColumns = reportDefinition.getSearchParams();
        List<ColumnDetail> columnDetails = metadataResponse.getReportDetails().getSearchParams();
        Map<String, ColumnDetail> colNameMap = columnDetails.stream().collect(Collectors.toMap(ColumnDetail::getName, Function.identity()));

        for (SearchColumn searchColumn : searchColumns) {

            if (searchColumn.getType().equals(TypeEnum.SINGLEVALUELIST) || searchColumn.getType().equals(TypeEnum.SINGLEVALUELISTAC) || searchColumn.getType().equals(TypeEnum.MULTIVALUELIST) || searchColumn.getType().equals(TypeEnum.MULTIVALUELISTAC)) {
                log.info("if searchColumn:" + searchColumn);
                log.info("Pattern is:" + searchColumn.getColName());

                String[] patterns = searchColumn.getPattern().split("\\|");
                log.info("patterns:" + patterns.toString());
                String url = patterns[0];
                //url = url.replaceAll("\\$tenantid",metadataResponse.getTenantId());
                log.info("url:" + url);
                ColumnDetail columnDetail = colNameMap.get(searchColumn.getName());

                if (url != null && url.startsWith("list://")) {
                    //consider this as fixed value and send this after removing list://
                    url = url.substring(7);
                    Map<Object, Object> map = new LinkedHashMap<>();
                    String[] pairs = url.split(",");
                    for (String str : pairs) {
                        String[] keyValue = str.split(":");
                        map.put(keyValue[0].replace('_', ','), keyValue[1]);

                    }
                    columnDetail.setDefaultValue(map);
                } else {

                    String res = "";
                    String[] stateid = null;

                    url = url.replaceAll("\\$currentTime", Long.toString(getCurrentTime()));

                    log.info("url:" + url);

                    if (searchColumn.getStateData() && (!metadataResponse.getTenantId().equals("default"))) {
                        stateid = metadataResponse.getTenantId().split("\\.");
                        url = url.replaceAll("\\$tenantid", stateid[0]);
                    } else {

                        url = url.replaceAll("\\$tenantid", metadataResponse.getTenantId());
                    }

                    try {
                        if (searchColumn.getWrapper()) {
                            RequestInfoWrapper riw = generateRequestInfoWrapper(requestInfo);
                            URI uri = URI.create(url);
                            res = restTemplate.postForObject(uri, riw, String.class);

                        } else {

                            res = restTemplate.postForObject(url, requestInfo, String.class);
                        }


                        Object document = Configuration.defaultConfiguration().jsonProvider().parse(res);

                        List<Object> keys = JsonPath.read(document, patterns[1]);
                        List<Object> values = JsonPath.read(document, patterns[2]);
                        if (searchColumn.getLocalisationRequired()) {
                            List<Object> keysAfterLoc = new ArrayList<>();
                            List<Object> valuesAfterLoc = new ArrayList<>();
                            for (int i = 0; i < keys.size(); i++) {
                                String servicecode = ((String) keys.get(i)).replaceAll("\\..*", "").toUpperCase();
                                String localisationLabel = searchColumn.getLocalisationPrefix() + servicecode;
                                if (!valuesAfterLoc.contains(localisationLabel)) {
                                    keysAfterLoc.add(servicecode);
                                    valuesAfterLoc.add(localisationLabel);
                                }
                            }
                            keys = keysAfterLoc;
                            values = valuesAfterLoc;
                        }
                        Map<Object, Object> map = new LinkedHashMap<>();
                        for (int i = 0; i < keys.size(); i++) {
                            map.put(keys.get(i), values.get(i));
                        }

                        columnDetail.setDefaultValue(map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return metadataResponse;
    }

    private RequestInfoWrapper generateRequestInfoWrapper(RequestInfo requestInfo) {
        RequestInfoWrapper riw = new RequestInfoWrapper();
        org.egov.swagger.model.RequestInfo ri = new org.egov.swagger.model.RequestInfo();
        ri.setAction(requestInfo.getAction());
        ri.setAuthToken(requestInfo.getAuthToken());
        ri.apiId(requestInfo.getApiId());
        ri.setVer(requestInfo.getVer());
        ri.setTs(1L);
        ri.setDid(requestInfo.getDid());
        ri.setKey(requestInfo.getKey());
        ri.setMsgId(requestInfo.getMsgId());
        ri.setRequesterId(requestInfo.getRequesterId());
        riw.setRequestInfo(ri);
        return riw;
    }

    public long getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar.getTimeInMillis();
    }
    
    public Map<String,List<String>> fetchCategoriesForEscalationOfficer(RequestInfo requestInfo, String tenantId) {
		Map<String,List<String>> categoryMap = new HashMap<String, List<String>>();
		List<String> categoryList1=null;
		List<String> categoryList2=null;
		try {
			//Get category list for escalationOfficer1
			Object result = fetchCategoriesFromAutoroutingEscalationMap(requestInfo, tenantId);
			
			if(null != result) {
				List objList = JsonPath.read(result, ReportConstants.JSONPATH_AUTOROUTING_MAP_CODES);
				if(CollectionUtils.isEmpty(objList)) {
					return null;
				}
				
				//Category list of escalation officer1
				for (int i = 0; i < objList.size(); i++) {
					List<String> escalationOfficer1List = JsonPath.read(objList.get(i), ReportConstants.AUTOROUTING_ESCALATING_OFFICER1_JSONPATH);
					if(!CollectionUtils.isEmpty(escalationOfficer1List)) {
						if(escalationOfficer1List.contains(requestInfo.getUserInfo().getUserName())) {
							if(CollectionUtils.isEmpty(categoryList1))
								categoryList1=new ArrayList<String>();
							categoryList1.add(JsonPath.read(objList.get(i), ReportConstants.AUTOROUTING_CATEGORY_JSONPATH));
						}
					}
				}
				
				//Category list of escalation officer2
				for (int i = 0; i < objList.size(); i++) {
					List<String> escalationOfficer2List = JsonPath.read(objList.get(i), ReportConstants.AUTOROUTING_ESCALATING_OFFICER2_JSONPATH);
					if(!CollectionUtils.isEmpty(escalationOfficer2List)) {
						if(escalationOfficer2List.contains(requestInfo.getUserInfo().getUserName())) {
							if(CollectionUtils.isEmpty(categoryList2))
								categoryList2=new ArrayList<String>();
							categoryList2.add(JsonPath.read(objList.get(i), ReportConstants.AUTOROUTING_CATEGORY_JSONPATH));
						}
					}
				}
			}
			
		} catch (Exception e) {
			log.error("Exception while fetching fetchCategoriesForEscalationOfficer: " + e);
		}
		
		categoryMap.put(ReportConstants.MDMS_AUTOROUTING_ESCALATION_OFFICER1_NAME, categoryList1);
		categoryMap.put(ReportConstants.MDMS_AUTOROUTING_ESCALATION_OFFICER2_NAME, categoryList2);
		
		return categoryMap;

	}
    
    
    private Object fetchCategoriesFromAutoroutingEscalationMap(RequestInfo requestInfo, String tenantId) {
    	requestInfo.setTs(null);
		StringBuilder uri = new StringBuilder();
		MdmsCriteriaReq mdmsCriteriaReq = prepareCategoryMdmsRequestByEscalationOfficer(uri, tenantId, requestInfo);
		Object response = null;
		try {
			response = serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq);
		} catch (Exception e) {
			log.error("Exception while fetching fetchCategoriesFromAutoroutingEscalationMap: " + e);
		}
		return response;

	}
    
    private MdmsCriteriaReq prepareCategoryMdmsRequestByEscalationOfficer(StringBuilder uri, String tenantId, RequestInfo requestInfo) {
    	
		uri.append(mdmsHost).append(mdmsEndpoint);
		MasterDetail masterDetail = org.egov.mdms.model.MasterDetail.builder()
				.name(ReportConstants.MDMS_AUTOROUTING_ESCALATION_MAP_MASTER_NAME)
				.filter("[?(@.active == true)]")
				.build();
			
		List<MasterDetail> masterDetails = new ArrayList<>();
		masterDetails.add(masterDetail);
		ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(ReportConstants.MDMS_PGR_MOD_NAME)
				.masterDetails(masterDetails).build();
		List<ModuleDetail> moduleDetails = new ArrayList<>();
		moduleDetails.add(moduleDetail);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
		return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	}
}
