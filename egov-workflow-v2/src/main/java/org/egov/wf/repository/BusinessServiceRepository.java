package org.egov.wf.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.wf.config.WorkflowConfig;
import org.egov.wf.repository.querybuilder.BusinessServiceQueryBuilder;
import org.egov.wf.repository.rowmapper.BusinessServiceDescRowMapper;
import org.egov.wf.repository.rowmapper.BusinessServiceRowMapper;
import org.egov.wf.web.models.BusinessDesc;
import org.egov.wf.web.models.BusinessService;
import org.egov.wf.web.models.BusinessServiceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class BusinessServiceRepository {


    private BusinessServiceQueryBuilder queryBuilder;

    private JdbcTemplate jdbcTemplate;

    private BusinessServiceRowMapper rowMapper;

    private BusinessServiceDescRowMapper descRowMapper;
    
    private WorkflowConfig config;


    @Autowired
    public BusinessServiceRepository(BusinessServiceQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate,
                                     BusinessServiceRowMapper rowMapper, WorkflowConfig config, BusinessServiceDescRowMapper descRowMapper) {
        this.queryBuilder = queryBuilder;
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
        this.config = config;
        this.descRowMapper = descRowMapper;
    }






    public List<BusinessService> getBusinessServices(BusinessServiceSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query;
        if(config.getIsStateLevel()){
            BusinessServiceSearchCriteria stateLevelCriteria = new BusinessServiceSearchCriteria(criteria);
            stateLevelCriteria.setTenantId(criteria.getTenantId().split("\\.")[0]);
            query = queryBuilder.getBusinessServices(stateLevelCriteria, preparedStmtList);
        }
        else{
            query = queryBuilder.getBusinessServices(criteria, preparedStmtList);
        }
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }

	public List<BusinessDesc> getBusinessServicesDesc(BusinessServiceSearchCriteria searchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query;
        if(config.getIsStateLevel()){
            BusinessServiceSearchCriteria stateLevelCriteria = new BusinessServiceSearchCriteria(searchCriteria);
            stateLevelCriteria.setTenantId(searchCriteria.getTenantId().split("\\.")[0]);
            query = queryBuilder.getBusinessServicesDesc(stateLevelCriteria, preparedStmtList);
        }
        else{
            query = queryBuilder.getBusinessServicesDesc(searchCriteria, preparedStmtList);
        }
				
        return jdbcTemplate.query(query, preparedStmtList.toArray(), descRowMapper);
    }


}
