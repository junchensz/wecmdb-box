package com.webank.cmdb.wecmdbbox.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StorageService {
    private Map<String,Object> storageMap = new ConcurrentHashMap();

    public void put(String key,String value){
        storageMap.put(key,value);
    }

    public Object get(String key){
        return storageMap.get(key);
    }
}
