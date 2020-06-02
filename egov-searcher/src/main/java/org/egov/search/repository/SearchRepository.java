package org.egov.search.repository;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.egov.search.model.Definition;
import org.egov.search.model.SearchRequest;
import org.egov.search.utils.SearchUtils;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;



@Repository
@Slf4j
public class SearchRepository {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Value("${max.sql.execution.time.millisec:15000}")
	private Long maxExecutionTime;
	
	@Value(("${report.query.timeout}"))
    public int queryExecutionTimeout;
	
	@Autowired
	private SearchUtils searchUtils;
	
	@PostConstruct
    private void init(){
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
        jdbcTemplate.setQueryTimeout(queryExecutionTimeout);
    }
			
	public List<String> fetchData(SearchRequest searchRequest, Definition definition) {
        Map<String, Object> preparedStatementValues = new HashMap<>();
        String query = searchUtils.buildQuery(searchRequest, definition.getSearchParams(), definition.getQuery(), preparedStatementValues);
		List<PGobject> maps = null;
		Long startTime = new Date().getTime();
		try {

			maps = namedParameterJdbcTemplate.queryForList(query, preparedStatementValues, PGobject.class);
        } catch (DataAccessResourceFailureException ex) {
            log.info("Query Execution Failed Due To Timeout: ", ex);
            PSQLException cause = (PSQLException) ex.getCause();
            if (cause != null && cause.getSQLState().equals("57014")) {
                throw new CustomException("QUERY_EXECUTION_TIMEOUT", "Query failed, as it took more than: "+ (queryExecutionTimeout) + " seconds to execute");
            } else {
                throw ex;
            }
        } catch (Exception e) {
            log.info("Query Execution Failed: ", e);
            throw e;
        }

        Long endTime = new Date().getTime();
        Long totalExecutionTime = endTime - startTime;
        log.info("total query execution time taken in millisecount:" + totalExecutionTime);
        if (endTime - startTime > maxExecutionTime)
            log.error("Sql query is taking time query:" + query);
		
		
		log.info("Searcher query result:"+maps);
		return searchUtils.convertPGOBjects(maps);
	}

}
