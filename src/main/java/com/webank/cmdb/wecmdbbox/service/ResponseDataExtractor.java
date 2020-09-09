package com.webank.cmdb.wecmdbbox.service;

public interface ResponseDataExtractor<WRAP> {
    Object extract(WRAP wrapObj);
}
