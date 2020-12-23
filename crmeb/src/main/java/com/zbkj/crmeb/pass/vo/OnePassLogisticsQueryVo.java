package com.zbkj.crmeb.pass.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 一号通物流查询结果对象
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
@ApiModel(value="OnePassLogisticsQueryVo对象", description = "一号通物流查询结果对象")
public class OnePassLogisticsQueryVo {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "是否签收")
    private String ischeck;

    @ApiModelProperty(value = "物流状态：0在途，1揽收，2疑难，3签收，4退签，5派件，6退回，7转单，10待清关，11清关中，12已清关，13清关异常，14收件人拒签")
    private String status;

    @ApiModelProperty(value = "物流公司")
    private String com;

    @ApiModelProperty(value = "快递单号")
    private String num;

    @ApiModelProperty(value = "物流详情")
    private List<OnePassLogisticsTrackVo> content;

}
