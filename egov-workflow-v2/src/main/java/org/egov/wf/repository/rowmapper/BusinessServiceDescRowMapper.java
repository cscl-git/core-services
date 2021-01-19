package org.egov.wf.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.wf.web.models.BusinessDesc;
import org.egov.wf.web.models.BusinessServiceDesc;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class BusinessServiceDescRowMapper implements ResultSetExtractor<List<BusinessDesc>> {

	@Override
	public List<BusinessDesc> extractData(ResultSet rs) throws SQLException, DataAccessException {
		  Map<String,BusinessDesc> businessServiceMap = new HashMap<>();

	        while (rs.next()){
	            String business = rs.getString("business");
	            BusinessDesc businessService = businessServiceMap.get(business);
	            if(businessService==null){
	               
	                businessService = BusinessDesc.builder()
	                        .tenantId(rs.getString("tenantId"))
	                        .business((business))
	                        .build();
	                businessServiceMap.put(business,businessService);
	            }
	            addChildrenToBusinessService(rs,businessService);
	        }
		  
	    	return new LinkedList<>(businessServiceMap.values());
	}

	private void addChildrenToBusinessService(ResultSet rs, BusinessDesc businessService) throws SQLException{
		String businessServiceName = rs.getString("businessservice");

		if (businessServiceName != null) {
			BusinessServiceDesc businessServiceDesc = BusinessServiceDesc.builder().businessService(rs.getString("businessservice")).businessServiceDescription(rs.getString("description")).build();
			businessService.addBusinessService(businessServiceDesc);
		}
		
	}

}
