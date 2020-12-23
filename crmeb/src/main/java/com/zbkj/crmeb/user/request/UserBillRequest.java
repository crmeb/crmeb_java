package com.zbkj.crmeb.user.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户账单表
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
@TableName("eb_user_bill")
@ApiModel(value="UserBill对象", description="用户账单表")
public class UserBillRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "关联id")
    private String linkId;

    @ApiModelProperty(value = "0 = 支出 1 = 获得")
    private Boolean pm;

    @ApiModelProperty(value = "账单标题")
    private String title;

    @ApiModelProperty(value = "明细种类")
    private String category;

    @ApiModelProperty(value = "明细类型")
    private String type;

    @ApiModelProperty(value = "明细数字")
    private BigDecimal number;

    @ApiModelProperty(value = "剩余")
    private BigDecimal balance;

    @ApiModelProperty(value = "备注")
    private String mark;


    @ApiModelProperty(value = "0 = 带确定 1 = 有效 -1 = 无效")
    private Boolean status;

}
