package com.webank.cmdb.wecmdbbox.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CiDataTreeDto {
    private Integer ciTypeId;
    private String rootGuid;
    private String guid;
    private Object data;
    private List<CiDataTreeDto> children = new LinkedList<>();

    public Integer getCiTypeId() {
        return ciTypeId;
    }

    public void setCiTypeId(Integer ciTypeId) {
        this.ciTypeId = ciTypeId;
    }

    public String getRootGuid() {
        return rootGuid;
    }

    public void setRootGuid(String rootGuid) {
        this.rootGuid = rootGuid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<CiDataTreeDto> getChildren() {
        return children;
    }

    public void setChildren(List<CiDataTreeDto> children) {
        this.children = children;
    }
}
