package com.zbkj.crmeb.wechat.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.HashMap;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="TemplateMessageIndustryVo对象", description="微信模板所属行业")
public class TemplateMessageIndustryVo {
    @ApiModelProperty(value = "主营行业")
    private TemplateMessageIndustryItemVo primaryIndustry;

    @ApiModelProperty(value = "副营行业")
    private TemplateMessageIndustryItemVo secondaryIndustry;

}
