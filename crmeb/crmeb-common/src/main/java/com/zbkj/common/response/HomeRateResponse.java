package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 主页统计数据对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="HomeRateResponse对象", description="主页统计数据对象")
public class HomeRateResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "今日销售额")
    private Object sales;

    @ApiModelProperty(value = "昨日销售额")
    private Object yesterdaySales;

    @ApiModelProperty(value = "今日访问量")
    private Object pageviews;

    @ApiModelProperty(value = "昨日访问量")
    private Object yesterdayPageviews;

    @ApiModelProperty(value = "今日订单量")
    private Object orderNum;

    @ApiModelProperty(value = "昨日订单量")
    private Object yesterdayOrderNum;

    @ApiModelProperty(value = "今日新增用户")
    private Object newUserNum;

    @ApiModelProperty(value = "昨日新增用户")
    private Object yesterdayNewUserNum;


}
