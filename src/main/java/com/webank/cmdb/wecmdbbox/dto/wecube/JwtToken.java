package com.webank.cmdb.wecmdbbox.dto.wecube;

import lombok.Data;

@Data
public class JwtToken {
    private String token;
    private String tokenType;
    private long expiration;
}
