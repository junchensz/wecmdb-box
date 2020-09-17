package com.webank.cmdb.wecmdbbox.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.cmdb.wecmdbbox.dto.cmdb.*;
import com.webank.cmdb.wecmdbbox.remote.CmdbApiV2Service;
import com.webank.cmdb.wecmdbbox.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.util.List;

@ShellComponent
public class WecmdbCommand {
    @Autowired
    private CmdbApiV2Service cmdbApiV2Service;

    @ShellMethod(value = "Fetch ci types",key = {"fetch_ci_types"})
    public void fetchCiTypes(){
        QueryResponse<CiTypeDto> response = cmdbApiV2Service.retrieveCiTypes(QueryRequest.defaultQueryObject());
        List<CiTypeDto> ciTypeDtoList = response.getContents();
        System.out.println(JsonUtils.toPrettyJsonString(ciTypeDtoList));
    }

    @ShellMethod(value = "Execute integrate query by name", key={"exec_int_by_name"})
    public void execIntByName(String name){
        QueryResponse response = cmdbApiV2Service.queryIntByName(name,QueryRequest.defaultQueryObject());
        printPrettyJson(response.getContents());
    }

    @ShellMethod(value = "Execute adhoc integrate qeury", key={"exec_adhoc_int"})
    public void execAdhocQuery(String adhocQueryBody) throws IOException {
        IntegrationQueryDto integrationQueryDto = JsonUtils.fromString(adhocQueryBody, IntegrationQueryDto.class);
        QueryResponse response = cmdbApiV2Service.queryAdhocInt(new AdhocIntegrationQueryDto(integrationQueryDto,QueryRequest.defaultQueryObject()));
        printPrettyJson(response.getContents());
    }

    @ShellMethod(value = "List integrae query for a specified ci type", key={"list_int_query"})
    public void listIntQuery(int ciTypeId){
        List<IdNamePairDto> idNamePairDtos = cmdbApiV2Service.searchIntQuery(ciTypeId,null,null);
        printPrettyJson(idNamePairDtos);
    }

    @ShellMethod(value="Fetch integrate query by name", key="fetch_int_query")
    public void fetchIntQuery(String name){
        IntegrationQueryDto integrationQueryDto = cmdbApiV2Service.getIntQueryByName(name);
        printPrettyJson(integrationQueryDto);
    }

    private void printPrettyJson(Object obj){
        System.out.println(JsonUtils.toPrettyJsonString(obj));
    }
}
