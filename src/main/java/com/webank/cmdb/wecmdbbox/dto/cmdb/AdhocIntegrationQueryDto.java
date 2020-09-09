package com.webank.cmdb.wecmdbbox.dto.cmdb;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Query will not be persisted
 * 
 * @author graychen
 *
 */

@Data
@AllArgsConstructor
public class AdhocIntegrationQueryDto {
    private IntegrationQueryDto criteria;
    private QueryRequest queryRequest;
}
