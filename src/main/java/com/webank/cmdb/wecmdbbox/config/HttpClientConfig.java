package com.webank.cmdb.wecmdbbox.config;

import com.webank.cmdb.wecmdbbox.common.HttpClientProperties;
import com.webank.cmdb.wecmdbbox.dto.cmdb.ResponseDto;
import com.webank.cmdb.wecmdbbox.dto.wecube.WecubeResponse;
import com.webank.cmdb.wecmdbbox.remote.CmdbApiV2Service;
import com.webank.cmdb.wecmdbbox.remote.WecubeApiService;
import com.webank.cmdb.wecmdbbox.support.CmdbResponseDataExtractor;
import com.webank.cmdb.wecmdbbox.service.RemoteServiceInvocationHandler;
import com.webank.cmdb.wecmdbbox.support.RestTemplateInterceptor;
import com.webank.cmdb.wecmdbbox.support.WecubeResponseDataExtractor;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class HttpClientConfig {

    private static final Logger log = LoggerFactory.getLogger(HttpClientConfig.class);

    @Value("${box.wecube-url}")
    private String weubeUrl;

    @Value("${box.wecmdb-url}")
    private String wecmdbUrl;

    @Autowired
    private HttpClientProperties httpClientProperties;

    @Autowired
    private RestTemplateInterceptor restTemplateInterceptor;

    public HttpClientConfig() {
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
        }

        SSLConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(builder.build());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingConnectionManager.setMaxTotal(httpClientProperties.getMaxTotalConnections());
        return poolingConnectionManager;
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();

                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return httpClientProperties.getDefaultKeepAliveTimeMillis();
            }
        };
    }

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(httpClientProperties.getRequestTimeout())
                .setConnectTimeout(httpClientProperties.getConnectTimeout())
                .setSocketTimeout(httpClientProperties.getSocketTimeout())
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();
    }

    @Bean
    public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = 10000)
            public void run() {
                try {
                    if (connectionManager != null) {
                        log.trace("run IdleConnectionMonitor - Closing expired and idle connections...");
                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections(httpClientProperties.getCloseIdleConnectionWaitTimeSecs(), TimeUnit.SECONDS);
                    } else {
                        log.trace("run IdleConnectionMonitor - Http Client Connection manager is not initialised");
                    }
                } catch (Exception e) {
                    log.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}", e.getMessage(), e);
                }
            }
        };
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient());
        return clientHttpRequestFactory;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = restTemplateBuilder().build();
        template.setInterceptors(Collections.singletonList(restTemplateInterceptor));
        return template;
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder(customRestTemplateCustomizer());
    }

    @Bean
    public CustomRestTemplateCustomizer customRestTemplateCustomizer() {
        return new CustomRestTemplateCustomizer();
    }

    private class CustomRestTemplateCustomizer implements RestTemplateCustomizer {
        @Override
        public void customize(RestTemplate restTemplate) {
            restTemplate.setRequestFactory(clientHttpRequestFactory());
            restTemplate.getInterceptors().add(new CustomClientHttpRequestInterceptor());
        }
    }

    @Bean
    public CmdbApiV2Service cmdbApiV2Service(RestTemplate restTemplate){
        return (CmdbApiV2Service)Proxy.newProxyInstance(HttpClientConfig.class.getClassLoader(),new Class[]{CmdbApiV2Service.class},
                new RemoteServiceInvocationHandler(restTemplate, wecmdbUrl,CmdbApiV2Service.class, ResponseDto.class, new CmdbResponseDataExtractor()));
    }

    @Bean
    public WecubeApiService wecubeApiService(RestTemplate restTemplate){
        return (WecubeApiService)Proxy.newProxyInstance(HttpClientConfig.class.getClassLoader(),new Class[]{WecubeApiService.class},
                new RemoteServiceInvocationHandler(restTemplate, weubeUrl,WecubeApiService.class, WecubeResponse.class, new WecubeResponseDataExtractor()));
    }

    private class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            logRequestDetails(request);
            ClientHttpResponse response = execution.execute(request, body);
            logResponseDetails(response);
            return response;
        }

        private void logRequestDetails(HttpRequest request) {
            log.debug("Request Headers: {}", request.getHeaders());
            log.debug("Request Method: {}", request.getMethod());
            log.debug("Request URI: {}", request.getURI());
        }

        private void logResponseDetails(ClientHttpResponse response) {
            log.debug("Response Headers: {}", response.getHeaders());
        }
    }
}
