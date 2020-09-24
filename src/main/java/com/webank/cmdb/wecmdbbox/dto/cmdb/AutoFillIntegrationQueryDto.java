package com.webank.cmdb.wecmdbbox.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class AutoFillIntegrationQueryDto extends IntegrationQueryDto {
    private String enumCodeAttr;

    public String getEnumCodeAttr() {
        return enumCodeAttr;
    }

    public void setEnumCodeAttr(String enumCodeAttr) {
        this.enumCodeAttr = enumCodeAttr;
    }
}
