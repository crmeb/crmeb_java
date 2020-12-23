package com.zbkj.crmeb.statistics.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户购买统计
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
@ApiModel(value="HomeUserBuyResponse对象", description="用户购买统计")
public class HomeUserBuyResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "未消费用户")
    private Object no;

    @ApiModelProperty(value = "消费一次用户")
    private Object once;

    @ApiModelProperty(value = "留存用户")
    private Object history;

    @ApiModelProperty(value = "回流客户")
    private Object back;
}
