package com.webank.cmdb.wecmdbbox.remote;

import com.webank.cmdb.wecmdbbox.dto.wecube.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

public interface WecubeApiService {
    static final String STORAGE_TOKEN = "access_token";

    @PostMapping("/auth/v1/api/login")
    @ResponseBody
    List<JwtToken> login(@RequestBody LoginDto loginDto);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginDto{
        private String username;
        private String password;
    }

}
