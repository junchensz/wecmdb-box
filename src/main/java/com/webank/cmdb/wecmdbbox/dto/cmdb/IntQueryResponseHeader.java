package com.webank.cmdb.wecmdbbox.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.LinkedList;
import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public class IntQueryResponseHeader {
    private String ciTypeName;
    private List<AttrUnit> attrUnits = new LinkedList<>();

    public IntQueryResponseHeader(){}

    public IntQueryResponseHeader(String ciTypeName) {
        this.ciTypeName = ciTypeName;
    }

    public String getCiTypeName() {
        return ciTypeName;
    }

    public void setCiTypeName(String ciTypeName) {
        this.ciTypeName = ciTypeName;
    }

    public static class AttrUnit {
        private String attrKey;
        private CiTypeAttrDto attr;

        public AttrUnit(){

        }

        public AttrUnit(String attrKey, CiTypeAttrDto attr) {
            this.attrKey = attrKey;
            this.attr = attr;
        }

        public String getAttrKey() {
            return attrKey;
        }

        public void setAttrKey(String attrKey) {
            this.attrKey = attrKey;
        }

        public CiTypeAttrDto getAttr() {
            return attr;
        }

        public void setAttr(CiTypeAttrDto attr) {
            this.attr = attr;
        }

    }

    public List<AttrUnit> getAttrUnits() {
        return attrUnits;
    }

    public void setAttrUnits(List<AttrUnit> attrUnits) {
        this.attrUnits = attrUnits;
    }
}
