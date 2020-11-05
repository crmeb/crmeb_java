package com.zbkj.crmeb.wechat.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 小程序行业信息
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PProgramTempVo对象", description="小程序行业信息")
public class ProgramTempVo {
    @ApiModelProperty(value = "模板id")
    private Integer tid;

    @ApiModelProperty(value = "模板名称")
    private String title;

    @ApiModelProperty(value = "模版类型，2 为一次性订阅，3 为长期订阅")
    private Integer type;

    @ApiModelProperty(value = "行业id")
    private Integer categoryId;
}
