package com.webank.cmdb.wecmdbbox.remote;

import com.webank.cmdb.wecmdbbox.dto.cmdb.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v2")
public interface CmdbApiV2Service {
    @PostMapping("/ci/{ciTypeId}/create")
    public List<Map<String, Object>> createCis(@PathVariable("ciTypeId") int ciTypeId, @RequestBody List<Map<String, Object>> request);

    @PostMapping("/intQuery/executeByName/{queryName}")
    public QueryResponse queryIntByName(@PathVariable("queryName") String queryName, @RequestBody QueryRequest queryRequest);

    @GetMapping("/intQuery/headerByName")
    public List<IntQueryResponseHeader> queryIntHeader(@RequestParam("queryName") String queryName);

    // Ci Type
    @PostMapping("/ciTypes/retrieve")
    public QueryResponse<CiTypeDto> retrieveCiTypes(@RequestBody QueryRequest request);

    // Ci type attribute
    @PostMapping("/ciTypeAttrs/retrieve")
    public QueryResponse<CiTypeAttrDto> retrieveCiTypeAttrs(@RequestBody QueryRequest request);

    // CI
    @PostMapping("/ci/{ciTypeId}/retrieve")
    public QueryResponse<CiData> retrieveCis(@PathVariable("ciTypeId") int ciTypeId, @RequestBody QueryRequest request);

    @PostMapping("/intQuery/adhoc/execute")
    public QueryResponse queryAdhocInt(@RequestBody AdhocIntegrationQueryDto adhocQueryRequest);

    @GetMapping("/intQuery")
    public IntegrationQueryDto getIntQueryByName(@RequestParam("name")String queryName);

    @GetMapping("/intQuery/ciType/{ciTypeId}/search")
    public List<IdNamePairDto> searchIntQuery(@PathVariable("ciTypeId") Integer ciTypeId, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "tailAttrId", required = false) Integer tailAttrId);

}
