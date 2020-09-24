package com.webank.cmdb.wecmdbbox.dto.wecube;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JwtToken {
    private String token;
    private String tokenType;
    private long expiration;
}
