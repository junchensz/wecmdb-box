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
        //System.out.println("Generated token:"+token.toString());
        System.out.println("Access authorization token: Bearer "+token.getToken());
    }

    @ShellMethod(value = "Set access token",key = {"set_access_token"})
    public void setAccessToken(String token){
        restTemplateInterceptor.setToken(token);
    }


    public JwtToken buildAccessToken(String userName, Date expireTime) {
        String sAuthorities = "[SUPER_ADMIN,IMPLEMENTATION_WORKFLOW_EXECUTION,IMPLEMENTATION_BATCH_EXECUTION,IMPLEMENTATION_ARTIFACT_MANAGEMENT,MONITOR_MAIN_DASHBOARD,MONITOR_METRIC_CONFIG,MONITOR_CUSTOM_DASHBOARD,MONITOR_ALARM_CONFIG,MONITOR_ALARM_MANAGEMENT,COLLABORATION_PLUGIN_MANAGEMENT,COLLABORATION_WORKFLOW_ORCHESTRATION,ADMIN_SYSTEM_PARAMS,ADMIN_RESOURCES_MANAGEMENT,ADMIN_USER_ROLE_MANAGEMENT,ADMIN_CMDB_MODEL_MANAGEMENT,CMDB_ADMIN_BASE_DATA_MANAGEMENT,ADMIN_QUERY_LOG,MENU_ADMIN_PERMISSION_MANAGEMENT,MENU_IDC_PLANNING_DESIGN,MENU_DESIGNING_CI_INTEGRATED_QUERY_EXECUTION,MENU_DESIGNING_CI_INTEGRATED_QUERY_MANAGEMENT,MENU_DESIGNING_CI_DATA_ENQUIRY,MENU_ADMIN_QUERY_LOG,MENU_ADMIN_CMDB_MODEL_MANAGEMENT,MENU_IDC_RESOURCE_PLANNING,MENU_APPLICATION_DEPLOYMENT_DESIGN,MENU_APPLICATION_ARCHITECTURE_QUERY,MENU_APPLICATION_ARCHITECTURE_DESIGN,MENU_CMDB_ADMIN_BASE_DATA_MANAGEMENT,MENU_DESIGNING_CI_DATA_MANAGEMENT,JOBS_SERVICE_CATALOG_MANAGEMENT,JOBS_TASK_MANAGEMENT]";
        String devAuthorities = "[APP_DEV,JOBS_SERVICE_CATALOG_MANAGEMENT,JOBS_TASK_MANAGEMENT,MENU_DESIGNING_CI_DATA_MANAGEMENT,MENU_DESIGNING_CI_INTEGRATED_QUERY_EXECUTION,MENU_DESIGNING_CI_INTEGRATED_QUERY_MANAGEMENT,MENU_IDC_PLANNING_DESIGN,MENU_IDC_RESOURCE_PLANNING,MENU_APPLICATION_ARCHITECTURE_DESIGN,MENU_APPLICATION_ARCHITECTURE_QUERY,MENU_APPLICATION_DEPLOYMENT_DESIGN,IMPLEMENTATION_WORKFLOW_EXECUTION,IMPLEMENTATION_BATCH_EXECUTION,IMPLEMENTATION_ARTIFACT_MANAGEMENT,MONITOR_MAIN_DASHBOARD,MONITOR_ALARM_CONFIG,MONITOR_METRIC_CONFIG,MONITOR_ALARM_MANAGEMENT,MONITOR_CUSTOM_DASHBOARD,MENU_ADMIN_QUERY_LOG,MENU_CMDB_ADMIN_BASE_DATA_MANAGEMENT]";
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
                .claim(ApplicationConstants.JwtInfo.CLAIM_KEY_AUTHORITIES, devAuthorities) //
                .signWith(SignatureAlgorithm.HS512, StringUtilsEx.decodeBase64(signingKey)) //
                .compact(); //
        return new JwtToken(accessToken, ApplicationConstants.JwtInfo.TOKEN_TYPE_ACCESS, expireTime.getTime());
    }
}
