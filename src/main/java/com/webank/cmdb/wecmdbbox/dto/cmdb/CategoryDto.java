package com.webank.cmdb.wecmdbbox.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.LinkedList;
import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public class CategoryDto {
    private Integer catId;
    private String catName;
    private String description;
    private Integer groupTypeId;
    private Integer catTypeId;
    private List<CatCodeDto> codes = new LinkedList<>();
    private CatTypeDto catType;

    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getGroupTypeId() {
        return groupTypeId;
    }

    public void setGroupTypeId(Integer groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    public Integer getCatTypeId() {
        return catTypeId;
    }

    public void setCatTypeId(Integer catTypeId) {
        this.catTypeId = catTypeId;
    }

    public List<CatCodeDto> getCodes() {
        return codes;
    }

    public void setCodes(List<CatCodeDto> codes) {
        this.codes = codes;
    }

    public CatTypeDto getCatType() {
        return catType;
    }

    public void setCatType(CatTypeDto catType) {
        this.catType = catType;
    }
}
