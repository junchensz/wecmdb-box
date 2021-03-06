package com.webank.cmdb.wecmdbbox.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public class IntegrationQueryDto {
    private String name;
    private Integer ciTypeId;
    private List<Integer> attrs = new LinkedList<>();
    // to display attribute names in cmdb gui (can be duplicated)
    private List<String> attrAliases = new ArrayList<>();
    @JsonIgnore
    // to identify agg attribute ( don't need pass value from cmdb gui), only
    // internal usage.
    private List<String> aggKeyNames = new ArrayList<>();
    // cmdb gui will use attrKeyNames to save and display attribute name
    private List<String> attrKeyNames = new ArrayList<>();

    // relation with parent node, it is not needed in root node.
    private Relationship parentRs;
    private List<IntegrationQueryDto> children = new LinkedList<>();
    private List<Filter> filters = new ArrayList<Filter>();
    public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public IntegrationQueryDto() {
    }

    public IntegrationQueryDto(String name) {
        this.name = name;
    }

    public IntegrationQueryDto(String name, Integer ciTypeId, List<Integer> attrs, Relationship rs) {
        this(name, ciTypeId, attrs, null, rs);
    }

    public IntegrationQueryDto(String name, Integer ciTypeId, List<Integer> attrs, List<String> attrAliases, Relationship rs) {
        this.name = name;
        this.ciTypeId = ciTypeId;
        this.attrs = attrs;
        this.attrAliases = attrAliases;
        this.parentRs = rs;
    }

    public Integer getCiTypeId() {
        return ciTypeId;
    }

    public void setCiTypeId(Integer ciTypeId) {
        this.ciTypeId = ciTypeId;
    }

    public List<Integer> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<Integer> attrs) {
        this.attrs = attrs;
    }

    public Relationship getParentRs() {
        return parentRs;
    }

    public void setParentRs(Relationship parentRs) {
        this.parentRs = parentRs;
    }

    public List<IntegrationQueryDto> getChildren() {
        return children;
    }

    public void setChildren(List<IntegrationQueryDto> children) {
        this.children = children;
    }

    public void addChild(IntegrationQueryDto child) {
        this.children.add(child);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAttrAliases() {
        return attrAliases;
    }

    public void setAttrAliases(List<String> attrAliases) {
        this.attrAliases = attrAliases;
    }

    public List<String> getAttrKeyNames() {
        return attrKeyNames;
    }

    public void setAttrKeyNames(List<String> attrKeyNames) {
        this.attrKeyNames = attrKeyNames;
    }

    public List<String> getAggKeyNames() {
        return aggKeyNames;
    }

    public void setAggKeyNames(List<String> aggKeyNames) {
        this.aggKeyNames = aggKeyNames;
    }
    
	@Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ciTypeId", ciTypeId)
                .add("attrs", attrs)
                .add("attrAliases", attrAliases)
                .add("parentRs", parentRs)
                .toString();
    }
}
