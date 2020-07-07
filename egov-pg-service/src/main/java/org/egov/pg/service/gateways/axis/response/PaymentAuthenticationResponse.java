package org.egov.pg.service.gateways.axis.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentAuthenticationResponse {
    private String url;
    private String method;
    private Map<String, String> postParams = new LinkedHashMap<String, String>();
    private String serializedPostParams;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Map<String, String> getPostParams() {
        return postParams;
    }

    public void setPostParams(Map<String, String> postParams) {
        this.postParams = postParams;
    }

    public String getSerializedPostParams() {

        return serializedPostParams;
    }

    public void setSerializedPostParams(String serializedPostParams) {
        this.serializedPostParams = serializedPostParams;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }


    @Override
    public String toString() {
        return "PaymentAuthenticationResponse{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", postParams=" + postParams +
                ", serializedPostParams='" + serializedPostParams + '\'' +
                '}';
    }
}
