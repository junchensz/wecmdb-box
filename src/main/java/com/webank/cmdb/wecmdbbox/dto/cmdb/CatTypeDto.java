package com.webank.cmdb.wecmdbbox.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.LinkedList;
import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public class CatTypeDto {
    private Integer catTypeId;
    private Integer ciTypeId;
    private String catTypeName;
    private String description;
    private List<CategoryDto> cats = new LinkedList<>();

    public Integer getCatTypeId() {
        return catTypeId;
    }

    public void setCatTypeId(Integer catTypeId) {
        this.catTypeId = catTypeId;
    }

    public Integer getCiTypeId() {
        return ciTypeId;
    }

    public void setCiTypeId(Integer ciTypeId) {
        this.ciTypeId = ciTypeId;
    }

    public String getCatTypeName() {
        return catTypeName;
    }

    public void setCatTypeName(String catTypeName) {
        this.catTypeName = catTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CategoryDto> getCats() {
        return cats;
    }

    public void setCats(List<CategoryDto> cats) {
        this.cats = cats;
    }
}
