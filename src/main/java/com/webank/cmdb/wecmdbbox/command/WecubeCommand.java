package com.webank.cmdb.wecmdbbox.command;

import com.webank.cmdb.wecmdbbox.dto.wecube.JwtToken;
import com.webank.cmdb.wecmdbbox.remote.WecubeApiService;
import com.webank.cmdb.wecmdbbox.service.StorageService;
import com.webank.cmdb.wecmdbbox.support.RestTemplateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;

@ShellComponent
public class WecubeCommand {
    @Autowired
    private WecubeApiService wecubeApiService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private RestTemplateInterceptor restTemplateInterceptor;

    @ShellMethod("Login wecube")
    public void login(String userName,String password){
        List<JwtToken> tokens = wecubeApiService.login(new WecubeApiService.LoginDto(userName,password));
        System.out.println("Got token successfully.");
//        storageService.put(WecubeApiService.STORAGE_TOKEN,getAccessToken(tokens));
        restTemplateInterceptor.setToken("Bearer " + getAccessToken(tokens));
    }

    private String getAccessToken(List<JwtToken> tokens){
        String accessToken="";
        for (JwtToken token : tokens) {
            if("accessToken".equals(token.getTokenType())){
                accessToken = token.getToken();
            }
        }
        return accessToken;
    }
}
