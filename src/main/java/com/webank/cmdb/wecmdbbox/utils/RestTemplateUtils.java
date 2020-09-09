package com.webank.cmdb.wecmdbbox.utils;

import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author howechen
 */
public class RestTemplateUtils {

    /**
     * Send get request to url with params
     *
     * @param restTemplate restTemplate
     * @param requestUri   target uri
     * @param headers      request headers
     * @return String
     */
    public static ResponseEntity<String> sendGetRequestWithUrlParamMap(RestTemplate restTemplate, String requestUri, HttpHeaders headers) {
        HttpMethod method = HttpMethod.GET;
        // set content type as form
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // setup http request entity
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        // send request and exchange the response to target class
        return restTemplate.exchange(requestUri, method, requestEntity, String.class);
    }

    /**
     * Send get request to url for downloading files
     *
     * @param restTemplate restTemplate
     * @param requestUri   target uri
     * @param headers      request headers
     * @return Resource type ResponseEntity
     */
    public static ResponseEntity<Resource> sendGetRequestForDownloading(RestTemplate restTemplate, String requestUri, HttpHeaders headers) {
        HttpMethod method = HttpMethod.GET;
        // set Accept:
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        // setup http request entity
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
        // send request and exchange the response to target class
        return restTemplate.exchange(requestUri, method, requestEntity, Resource.class);
    }

    /**
     * Send get request to url with params
     *
     * @param restTemplate restTemplate
     * @param requestUri   target uri
     * @param headers      request headers
     * @return String
     */
    public static ResponseEntity<String> sendGetRequestWithUrlParamMap(RestTemplate restTemplate, URI requestUri, HttpHeaders headers) {
        return sendGetRequestWithUrlParamMap(restTemplate, requestUri.getPath(), headers);
    }

    /**
     * Send get request to url without params
     *
     * @param restTemplate restTemplate
     * @param url          target url
     * @return String
     */
    public ResponseEntity<String> sendGetRequestWithoutUrlParamMap(RestTemplate restTemplate, String url) {
        return restTemplate.getForEntity(url, String.class);
    }

    /**
     * Send post request to url with params
     *
     * @param restTemplate restTemplate
     * @param requestUri   target uri
     * @param headers      request headers
     * @return String
     */
    public static ResponseEntity<String> sendPostRequestWithBody(RestTemplate restTemplate, String requestUri, HttpHeaders headers, List<Map<String, Object>> requestParamMap) {

        HttpMethod method = HttpMethod.POST;
        // set content type as form
        headers.setContentType(MediaType.APPLICATION_JSON);
        // setup http request entity
        HttpEntity<List<Map<String, Object>>> requestEntity = new HttpEntity<>(requestParamMap, headers);

        // send request and exchange the response to target class
        return restTemplate.exchange(requestUri, method, requestEntity, String.class);
    }

    /**
     * Send post request to url with params
     *
     * @param restTemplate restTemplate
     * @param requestUri   target uri
     * @param headers      request headers
     * @return String
     */
    public static ResponseEntity<String> sendPostRequestWithBody(RestTemplate restTemplate, String requestUri, HttpHeaders headers, Object requestBody) {

        HttpMethod method = HttpMethod.POST;
        // set content type as form
        headers.setContentType(MediaType.APPLICATION_JSON);
        // setup http request entity
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        // send request and exchange the response to target class
        return restTemplate.exchange(requestUri, method, requestEntity, String.class);
    }

    /**
     * Send post request to url with params
     *
     * @param restTemplate restTemplate
     * @param requestUri   target uri
     * @param headers      request headers
     * @return String
     */
    public static ResponseEntity<String> sendDeleteWithoutBody(RestTemplate restTemplate, String requestUri, HttpHeaders headers) {

        HttpMethod method = HttpMethod.DELETE;
        // set content type as form
        headers.setContentType(MediaType.APPLICATION_JSON);
        // setup http request entity
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        // send request and exchange the response to target class
        return restTemplate.exchange(requestUri, method, requestEntity, String.class);
    }

    /**
     * Send post request to url with params
     *
     * @param restTemplate restTemplate
     * @param requestUri   target uri
     * @param headers      request headers
     * @return String
     */
    public static ResponseEntity<String> sendDeleteWithBody(RestTemplate restTemplate, String requestUri, HttpHeaders headers, Object requestBody) {

        HttpMethod method = HttpMethod.DELETE;
        // set content type as form
        headers.setContentType(MediaType.APPLICATION_JSON);
        // setup http request entity
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        // send request and exchange the response to target class
        return restTemplate.exchange(requestUri, method, requestEntity, String.class);
    }


    public static <T> void checkResponseEntity(ResponseEntity<T> responseEntity) {
        if (StringUtils.isEmpty(responseEntity.getBody()) || responseEntity.getStatusCode().isError()) {
            if (responseEntity.getStatusCode().is4xxClientError()) {
                throw new PluginException(String.format("The target server returned error code: [%s].", responseEntity.getStatusCode().toString()));
            }

            if (responseEntity.getStatusCode().is5xxServerError()) {
                throw new PluginException(String.format("The target server returned error code: [%s], which is an target server's internal error.", responseEntity.getStatusCode().toString()));
            }
        }
    }
}
