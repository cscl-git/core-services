//package com.water_services;
//
//import java.util.TimeZone;
//
//import org.egov.tracer.config.TracerConfiguration;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Import;
//import org.springframework.web.client.RestTemplate;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.MapperFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@SpringBootApplication(scanBasePackages="org.egov.waterConnection")
//@EnableAutoConfiguration
//@Import({ TracerConfiguration.class })
//public class WaterApplication {
//	  @Value("${app.timezone}")
//	    private String timeZone;
//
//    public static void main(String[] args) {
//        SpringApplication.run(WaterApplication.class, args);
//        
//        }
//    
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
//    
//    @Bean
//    public ObjectMapper objectMapper(){
//        return new ObjectMapper()
//                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
//                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//                .setTimeZone(TimeZone.getTimeZone(timeZone));
//    }
//}
