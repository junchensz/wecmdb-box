package com.webank.cmdb.wecmdbbox.command;

import com.webank.cmdb.wecmdbbox.common.ApplicationConstants;
import com.webank.cmdb.wecmdbbox.dto.wecube.JwtToken;
import com.webank.cmdb.wecmdbbox.remote.WecubeApiService;
import com.webank.cmdb.wecmdbbox.service.StorageService;
import com.webank.cmdb.wecmdbbox.support.RestTemplateInterceptor;
import com.webank.cmdb.wecmdbbox.utils.StringUtilsEx;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Date;
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

    @ShellMethod(value="Gnerate access token",key={"generate_token"})
    public void generate_access_token(String userName){
        JwtToken token = buildAccessToken(userName,new Date(2050-1900,0,1));
        System.out.println("Generated token:"+token.toString());
    }

    public JwtToken buildAccessToken(String userName, Date expireTime) {
        String sAuthorities = "ALL";
        String signingKey = "Platform+Auth+Server+Secret";

        Date now = new Date();
        String clientType = ApplicationConstants.ClientType.USER;

        String accessToken = Jwts //
                .builder() //
                .setSubject(userName) //
                .setIssuedAt(now) //
                .claim(ApplicationConstants.JwtInfo.CLAIM_KEY_TYPE, ApplicationConstants.JwtInfo.TOKEN_TYPE_ACCESS) //
                .claim(ApplicationConstants.JwtInfo.CLAIM_KEY_CLIENT_TYPE, clientType) //
                .setExpiration(expireTime) //
                .claim(ApplicationConstants.JwtInfo.CLAIM_KEY_AUTHORITIES, sAuthorities) //
                .signWith(SignatureAlgorithm.HS512, StringUtilsEx.decodeBase64(signingKey)) //
                .compact(); //
        return new JwtToken(accessToken, ApplicationConstants.JwtInfo.TOKEN_TYPE_ACCESS, expireTime.getTime());
    }
}
