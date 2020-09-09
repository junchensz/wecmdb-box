package com.webank.cmdb.wecmdbbox.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HttpClientUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

//    public OpsResponseDto initPostRequest(String url, Object data, RestTemplate restTemplate) {
//        logger.info("Sending data: {}", data.toString());
//        ResponseEntity<String> responseFromRequest = RestTemplateUtils.sendPostRequestWithBody(restTemplate, url, new HttpHeaders(), data);
//        return RestTemplateUtils.checkResponse(responseFromRequest);
//    }

    static public ResponseEntity<Resource> initDownloadRequest(String url, RestTemplate restTemplate) {
        ResponseEntity<Resource> responseFromRequest = RestTemplateUtils.sendGetRequestForDownloading(restTemplate, url, new HttpHeaders());
        RestTemplateUtils.checkResponseEntity(responseFromRequest);
        return responseFromRequest;
    }
}
