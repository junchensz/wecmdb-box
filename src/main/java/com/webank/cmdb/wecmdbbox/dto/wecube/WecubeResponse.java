package com.webank.cmdb.wecmdbbox.dto.wecube;

import lombok.Data;

@Data
public class WecubeResponse<T> {
    private String status;
    private String message;
    private T data;
}
