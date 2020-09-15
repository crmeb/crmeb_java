package com.zbkj.crmeb.wechat.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProgramCategoryVo对象", description="小程序行业信息")
public class ProgramCategoryVo {
    @ApiModelProperty(value = "行业id")
    private Integer id;

    @ApiModelProperty(value = "行业名称")
    private String name;
}
