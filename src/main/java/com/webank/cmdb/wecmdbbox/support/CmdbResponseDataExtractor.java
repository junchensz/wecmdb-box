package com.webank.cmdb.wecmdbbox.support;

import com.webank.cmdb.wecmdbbox.dto.cmdb.ResponseDto;
import com.webank.cmdb.wecmdbbox.service.ResponseDataExtractor;
import com.webank.cmdb.wecmdbbox.utils.RemoteInvokeException;

public class CmdbResponseDataExtractor implements ResponseDataExtractor<ResponseDto> {
    @Override
    public Object extract(ResponseDto wrapObj) {
        if(!ResponseDto.STATUS_OK.equals(wrapObj.getStatusCode())){
            throw new RemoteInvokeException(wrapObj.getStatusMessage());
        }
        return wrapObj.getData();
    }
}
