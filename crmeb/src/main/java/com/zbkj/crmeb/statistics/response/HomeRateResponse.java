package com.zbkj.crmeb.statistics.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 主页用户新增统计
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
@ApiModel(value="HomeRateResponse对象", description="主页用户新增统计")
public class HomeRateResponse implements Serializable {

    private static final long serialVersionUID=1L;

    public HomeRateResponse() {
    }

    public HomeRateResponse(Object count, Object dayRate, Object weekRate, Object total) {
        this.count = count;
        this.dayRate = dayRate;
        this.weekRate = weekRate;
        this.total = total;
    }

    @ApiModelProperty(value = "昨日新增量")
    private Object count;

    @ApiModelProperty(value = "日同比率")
    private Object dayRate;

    @ApiModelProperty(value = "周同比率")
    private Object weekRate;

    @ApiModelProperty(value = "总数")
    private Object total;


}
