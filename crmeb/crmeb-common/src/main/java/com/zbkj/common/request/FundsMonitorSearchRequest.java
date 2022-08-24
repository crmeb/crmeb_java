package com.zbkj.common.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 资金监控
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
@ApiModel(value="FundsMonitorSearchRequest对象", description="资金监控")
public class FundsMonitorSearchRequest implements Serializable {

    @ApiModelProperty(value = "搜索关键字")
    private String keywords;

    @ApiModelProperty(value = "类型")
    private String category;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "添加时间")
    private String dateLimit;

    @ApiModelProperty(value = "最大佣金")
    private BigDecimal max;

    @ApiModelProperty(value = "最小佣金")
    private BigDecimal min;

    @ApiModelProperty(value = "排序 asc/desc")
    private String sort;

    @JsonIgnore
    @ApiModelProperty(value = "关联id")
    private String linkId;

    @JsonIgnore
    @ApiModelProperty(value = "操作类型")
    private Integer pm;

    @ApiModelProperty(value = "用户id list")
    private List<Integer> userIdList;

    @ApiModelProperty(value = "用户id list")
    private Integer uid;
}
