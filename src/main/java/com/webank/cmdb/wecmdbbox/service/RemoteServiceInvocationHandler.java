package com.webank.cmdb.wecmdbbox.service;

import com.webank.cmdb.wecmdbbox.utils.ParameterizedTypeImpl;
import com.webank.cmdb.wecmdbbox.utils.PluginException;
import com.webank.cmdb.wecmdbbox.utils.RemoteInvokeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

public class RemoteServiceInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RemoteServiceInvocationHandler.class);

    @Autowired
    private RestTemplate restTemplate;

    private String baseUrl = null;
    private Class<?> invokeInterface = null;
    private Class<?> respWrapClass = null;
    private ResponseDataExtractor respDataExtractor;

    public RemoteServiceInvocationHandler(RestTemplate restTemplate, String baseUrl, Class<?> invokeInterface, Class<?> respWrapClass, ResponseDataExtractor respDataExtractor){
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.invokeInterface = invokeInterface;
        this.respWrapClass = respWrapClass;
        this.respDataExtractor = respDataExtractor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestMapping clzzMapping = invokeInterface.getAnnotation(RequestMapping.class);
        HttpMethod requestMethod = null;
        String rootPath = "";
        if(clzzMapping != null){
            String[] paths = clzzMapping.value();
            if(paths != null && paths.length > 0){
                rootPath = paths[0];
            }
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        String requestPath = "";
        if(postMapping != null){
            String[] paths = postMapping.value();
            if(paths!=null && paths.length>0){
                requestPath = rootPath + paths[0];
            }
            requestMethod = HttpMethod.POST;
        }else if(getMapping != null){
            String[] paths = getMapping.value();
            if (paths != null && paths.length > 0) {
                requestPath = rootPath + "/" + paths[0];
            }
            requestMethod = HttpMethod.GET;
        }else {
            Method[] methods = Object.class.getMethods();
            for(Method m:methods){
                if(m.equals(method)){
                    return m.invoke(this,args);
                }
            }
            throw new PluginException(String.format("Can not get http method for method: %s",method.toString()));
        }

        List<Object> pathVars = new LinkedList<>();
        Object requestObj = null;
        Map<String,Object> requestParams = new HashMap<>();

        Type returnType = method.getGenericReturnType();
        Parameter[] params = method.getParameters();
        for(int i=0;i< params.length; i++){
            Parameter param = params[i];
            if(param.getAnnotation(PathVariable.class) != null){
                pathVars.add(args[i]);
            }else if(param.getAnnotation(RequestParam.class) != null){
                String paramName = param.getAnnotation(RequestParam.class).value();
                requestParams.put(paramName,args[i]);
            }else if(param.getAnnotation(RequestBody.class) != null){
                requestObj = args[i];
            }else{
                logger.warn(String.format("Parameter (%s) has not been processed.",param.toString()));
            }
        }

        Object responseObj = remoteCall(requestMethod, requestPath, pathVars, requestParams, requestObj, returnType);
        return responseObj;
    }

    private Object remoteCall(HttpMethod requestMethod, String requestPath, List<Object> pathVars,Map<String,Object> requestParams, Object requestObj, Type returnType) {
        String destUrl = baseUrl+requestPath;
        Object respObj = null;

        Type responseType = null;
        if(respWrapClass != null){
            responseType = new ParameterizedTypeImpl(null,respWrapClass,new Type[]{returnType});
        }else{
            responseType = returnType;
        }
        switch (requestMethod){
            case POST:
                if(returnType == void.class){
                    restTemplate.postForLocation(destUrl,requestObj,pathVars);
                }else{
                    String url = baseUrl+requestPath;
                    respObj = postForObject(url,requestObj,responseType,pathVars.toArray(new Object[]{}));
                }
                break;
            case GET:
                if(returnType == void.class){
                    throw new PluginException(String.format("Do not support void return for GET method."));
                }else{
                    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(destUrl);
                    if(requestParams!=null && requestParams.size()>0){
                        for(Map.Entry<String,Object> param:requestParams.entrySet()){
                            String name = param.getKey();
                            Object value = param.getValue();
                            uriComponentsBuilder.queryParam(name,value);
                        }
                    }
                    //ParameterizedTypeImpl parameterizedType = new ParameterizedTypeImpl(null,respWrapClass,new Type[]{returnType});
                    respObj =  getForObject(uriComponentsBuilder.toUriString(), responseType,pathVars);
                }
                break;
        }

        Object returnObj = null;
        try{
            if(respDataExtractor != null){
                returnObj = respDataExtractor.extract(respObj);
            }else{
                returnObj = respObj;
            }
        }catch(RemoteInvokeException ex){
            throw new PluginException(String.format("Fail to convert response obj for remote interface (%s), error message:%s",
                    destUrl, ex));
        }
        return returnObj;
    }

    private Object postForObject(String url, Object requestObj, Type responseType, Object... pathVars){
        HttpMessageConverterExtractor responseExtractor =
                new HttpMessageConverterExtractor(responseType,restTemplate.getMessageConverters());
        logger.info("Sending POST object ({}) to url ({}) (pathVars:{})",String.valueOf(requestObj),url, Arrays.toString(pathVars));
        try {
            return restTemplate.execute(url, HttpMethod.POST, restTemplate.httpEntityCallback(requestObj), responseExtractor, pathVars);
        }catch(RestClientException ex){
            throw new RemoteInvokeException(String.format("Fail to get response from url (%s) by POST.",url),ex);
        }
    }

    private Object getForObject(String url, Type responseType, Object... pathVars){
        HttpMessageConverterExtractor responseExtractor =
                new HttpMessageConverterExtractor(responseType,restTemplate.getMessageConverters());
        logger.info("Sending GET to url ({}) (pathVars:{})",url,Arrays.toString(pathVars));
        try {
            return restTemplate.execute(url, HttpMethod.GET, null, responseExtractor, pathVars);
        }catch(RestClientException ex){
            throw new RemoteInvokeException(String.format("Fail to get response from url (%s) by GET.",url),ex);
        }
    }

}
