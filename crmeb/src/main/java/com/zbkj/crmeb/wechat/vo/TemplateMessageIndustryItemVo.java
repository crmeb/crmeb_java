package com.zbkj.crmeb.wechat.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="TemplateMessageIndustryItemVo对象", description="微信模板所属行业详情")
public class TemplateMessageIndustryItemVo {
    @ApiModelProperty(value = "主行业")
    private String firstClass;

    @ApiModelProperty(value = "副行业")
    private String secondClass;

}
