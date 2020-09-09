package com.webank.cmdb.wecmdbbox.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public class CatCodeDto{
    private String code;
    private String value;
    private Integer seqNo;
    private Integer codeId;
    private String codeDescription;
    // groupCodeId can be integer (for update) or map (query result form cmdb)
    private Object groupCodeId;
    private String groupName;
    private Integer catId;
    private String status;

    private List<CiTypeDto> ciTypes = new ArrayList<>();
    private CategoryDto cat;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public Integer getCodeId() {
        return codeId;
    }

    public void setCodeId(Integer codeId) {
        this.codeId = codeId;
    }

    public String getCodeDescription() {
        return codeDescription;
    }

    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }

    public Object getGroupCodeId() {
        return groupCodeId;
    }

    public void setGroupCodeId(Object groupCodeId) {
        this.groupCodeId = groupCodeId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CiTypeDto> getCiTypes() {
        return ciTypes;
    }

    public void setCiTypes(List<CiTypeDto> ciTypes) {
        this.ciTypes = ciTypes;
    }

    public CategoryDto getCat() {
        return cat;
    }

    public void setCat(CategoryDto cat) {
        this.cat = cat;
    }
}
