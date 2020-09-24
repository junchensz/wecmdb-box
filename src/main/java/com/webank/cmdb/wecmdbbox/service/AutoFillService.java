package com.webank.cmdb.wecmdbbox.service;

import com.webank.cmdb.wecmdbbox.common.AutoFillType;
import com.webank.cmdb.wecmdbbox.common.EnumCodeAttr;
import com.webank.cmdb.wecmdbbox.dto.cmdb.*;
import com.webank.cmdb.wecmdbbox.remote.CmdbApiV2Service;
import com.webank.cmdb.wecmdbbox.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.webank.cmdb.wecmdbbox.utils.SpecialSymbolUtils.getAfterSpecialSymbol;

@Service
public class AutoFillService {
    private static final String TARGET_NAME = "targetName";
    private static final String TARGET_DEFAULT_VALUE = "";
    public static final String SYMBOL_COMMA = ",";

    @Autowired
    private CmdbApiV2Service cmdbApiV2Service;

    public String queryValueByRule(String rootGuid,Object autoFillRuleValue,StringBuilder sb){
        List<AutoFillItem> autoRuleItems = parserRule(autoFillRuleValue);
        boolean isPreviousExpressionValue = true;
        for (AutoFillItem item : autoRuleItems) {
            if (AutoFillType.Rule.getCode().equals(item.getType())) {
                try {
                    List<AutoFillIntegrationQueryDto> routines = JsonUtils.toList(item.getValue(), AutoFillIntegrationQueryDto.class);
                    routines.forEach(routine -> {
                        routine.getFilters().forEach(filter -> {
                            if ("autoFill".equals(filter.getType())){
                                filter.setValue(queryValueByRule(rootGuid,filter.getValue(),new StringBuilder()));
                            }
                        });
                    });
                    QueryResponse response = queryIntegrateWithRoutines(rootGuid, routines);
                    List<String> targetValues = getValueFromResponse(response, routines);
                    if(isPreviousExpressionValue && targetValues.isEmpty()) {
                        return null;
                    }
                    for (int i = 0; i < targetValues.size(); i++) {
                        String value = targetValues.get(i);
                        if (checkExpression(value)) {
                            queryValueByRule(rootGuid, value, sb);
                        } else {
                            sb.append(value);
                        }
                        if (i < targetValues.size() - 1) {
                            sb.append(getAfterSpecialSymbol(SYMBOL_COMMA));
                        }
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException(String.format("Failed to convert auto fill rule [%s]. ", item), e);
                }
            } else {
                sb.append(item.getValue());
                isPreviousExpressionValue=false;
            }
        }
        return sb.toString();
    }

    private boolean checkExpression(String targetValue) {
        if(targetValue.contains("isReferedFromParent")) {
            return true;
        }
        return false;
    }
    private List<String> getValueFromResponse(QueryResponse response, List<AutoFillIntegrationQueryDto> routines) {
        List<Map<String, Object>> contents = response.getContents();
        List<String> targetValues = new ArrayList<>();
        contents.forEach(content -> {
            Object targetValue = content.get(TARGET_NAME) != null ? content.get(TARGET_NAME) : TARGET_DEFAULT_VALUE;
            if (targetValue != null) {
                if (targetValue instanceof CatCodeDto) {
                    targetValues.add(getValueFromEnumCode(routines, targetValue));
                } else {
                    targetValues.add(targetValue.toString());
                }
            }
        });
        return targetValues;
    }

    private String getValueFromEnumCode(List<AutoFillIntegrationQueryDto> routines, Object targetValue) {
        String value = null;
        CatCodeDto code = (CatCodeDto) targetValue;
        if (!routines.isEmpty()) {
            String codeAttr = routines.get(routines.size() - 1).getEnumCodeAttr();
            if (codeAttr != null) {
                switch (EnumCodeAttr.fromCode(codeAttr)) {
                    case Id:
                        value = code.getCodeId().toString();
                        break;
                    case Code:
                        value = code.getCode();
                        break;
                    case Value:
                        value = code.getValue();
                        break;
                    case GroupCodeId:
                        value = code.getGroupCodeId().toString();
                        break;
                    default:
                        value = code.getValue();
                        break;
                }
            } else {
                value = code.getValue();
            }
        }
        return value;
    }

    private List<AutoFillItem> parserRule(Object autoFillRuleValue) {
        String autoFillRule = (String) autoFillRuleValue;
        List<AutoFillItem> autoRuleItems = new ArrayList<>();
        try {
            autoRuleItems = JsonUtils.toList(autoFillRule, AutoFillItem.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to parse autoFillRule [%s]. ", autoFillRule));
        }
        return autoRuleItems;
    }

    private QueryResponse queryIntegrateWithRoutines(String guid, List<AutoFillIntegrationQueryDto> routines) {
        AdhocIntegrationQueryDto adhocDto = buildRootDto(routines.get(0), guid);
        travelFillQueryDto(routines, adhocDto.getCriteria(), adhocDto.getQueryRequest(),1);

        return cmdbApiV2Service.queryAdhocInt(adhocDto);
    }

    private AdhocIntegrationQueryDto buildRootDto(IntegrationQueryDto routineDto, String guid) {
        AdhocIntegrationQueryDto adhocDto = new AdhocIntegrationQueryDto();

        QueryRequest queryRequest = new QueryRequest();
        String aliasName = "root$guid";

        Filter filter = new Filter(aliasName, "eq", guid);
        queryRequest.setFilters(Arrays.asList(filter));

        IntegrationQueryDto rootDto = new IntegrationQueryDto();
        rootDto.setName("root");
        rootDto.setCiTypeId(routineDto.getCiTypeId());
        rootDto.setAttrs(Arrays.asList(getAttrIdByPropertyNameAndCiTypeId(routineDto.getCiTypeId(),"guid")));
        rootDto.setAttrKeyNames(Arrays.asList("root$guid"));

        adhocDto.setCriteria(rootDto);
        adhocDto.setQueryRequest(queryRequest);

        return adhocDto;
    }

    private Integer getAttrIdByPropertyNameAndCiTypeId(Integer ciTypeId, String guid) {
        QueryRequest queryRequest = QueryRequest.defaultQueryObject().addEqualsFilter("ciTypeId",ciTypeId)
                .addEqualsFilter("propertyName",guid);
        QueryResponse<CiTypeAttrDto> ciTypeAttrDtoQueryResponse = cmdbApiV2Service.retrieveCiTypeAttrs(queryRequest);
        List<CiTypeAttrDto> attrDtos = ciTypeAttrDtoQueryResponse.getContents();
        if(attrDtos.size() ==0 ){
            System.out.println(String.format("Can not find out ci type (%d) guid attribution:",ciTypeId));
            throw new IllegalStateException(String.format("Can not find out ci type (%d) guid attribution:",ciTypeId));
        }else{
            return attrDtos.get(0).getCiTypeAttrId();
        }
    }

    private IntegrationQueryDto travelFillQueryDto(List<AutoFillIntegrationQueryDto> routines, IntegrationQueryDto parentDto, QueryRequest queryRequest, final int position) {
        if (position >= routines.size()) {
            return null;
        }

        IntegrationQueryDto item = routines.get(position);
        Relationship parentRs = new Relationship();
        parentRs.setAttrId(item.getParentRs().getAttrId());
        parentRs.setIsReferedFromParent(item.getParentRs().getIsReferedFromParent());

        IntegrationQueryDto dto = new IntegrationQueryDto();
        dto.setName("index" + position);
        dto.setCiTypeId(item.getCiTypeId());
        dto.setParentRs(parentRs);
        List<String> fileds = new ArrayList();
        List<Integer> attrs = new ArrayList();
        if (position < routines.size() - 1) {
            attrs.add(getAttrIdByPropertyNameAndCiTypeId(item.getCiTypeId(), "guid"));
            fileds.add(item.getCiTypeId() + "$guid_" + position);
        }
        if (item.getFilters().size() > 0) {
            List<Filter> filters = new ArrayList<Filter>(queryRequest.getFilters());
            item.getFilters().stream().forEach(filter -> {
                attrs.add(getAttrIdByPropertyNameAndCiTypeId(item.getCiTypeId(), filter.getName()));
                String fixedFilterName = null;
                if("guid".equals(filter.getName())){
                    fixedFilterName = item.getCiTypeId() + "$guid_" + position;
                }else{
                    fixedFilterName = item.getCiTypeId() + "$" + filter.getName();
                }
                filter.setName(fixedFilterName);
                filters.add(filter);
                fileds.add(filter.getName());
            });
            queryRequest.setFilters(filters);
        }
        dto.setAttrs(attrs);
        dto.setAttrKeyNames(fileds);
        IntegrationQueryDto childDto = travelFillQueryDto(routines, dto, queryRequest, position+1);

        if (childDto == null) {
            addTargetName(parentDto, dto);
        } else {
            parentDto.setChildren(Arrays.asList(childDto));
        }
        return parentDto;
    }

    private void addTargetName(IntegrationQueryDto parentDto, IntegrationQueryDto dto) {
        List<Integer> attrs = new ArrayList<>();
        attrs.addAll(dto.getAttrs());
        attrs.addAll(parentDto.getAttrs());
        attrs.add(dto.getParentRs().getAttrId());

        List<String> attrKeyNames = new ArrayList<>();
        attrKeyNames.addAll(dto.getAttrKeyNames());
        attrKeyNames.addAll(parentDto.getAttrKeyNames());
        attrKeyNames.add(TARGET_NAME);

        parentDto.setAttrs(attrs);
        parentDto.setAttrKeyNames(attrKeyNames);
    }
}
