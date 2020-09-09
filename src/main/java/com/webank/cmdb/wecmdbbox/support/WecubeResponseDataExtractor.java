package com.webank.cmdb.wecmdbbox.support;

import com.webank.cmdb.wecmdbbox.dto.cmdb.ResponseDto;
import com.webank.cmdb.wecmdbbox.dto.wecube.WecubeResponse;
import com.webank.cmdb.wecmdbbox.service.ResponseDataExtractor;
import com.webank.cmdb.wecmdbbox.utils.RemoteInvokeException;

public class WecubeResponseDataExtractor implements ResponseDataExtractor<WecubeResponse> {
    @Override
    public Object extract(WecubeResponse wrapObj) {
        if(!"OK".equals(wrapObj.getStatus())){
            throw new RemoteInvokeException(wrapObj.getMessage());
        }
        return wrapObj.getData();
    }
}
