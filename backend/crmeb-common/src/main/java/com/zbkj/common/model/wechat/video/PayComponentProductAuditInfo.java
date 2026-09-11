package com.zbkj.common.model.wechat.video;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 组件商品审核信息表
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_pay_component_product_audit_info")
@ApiModel(value="PayComponentProductAuditInfo对象", description="组件商品审核信息表")
public class PayComponentProductAuditInfo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "交易组件平台内部商品ID")
    private Integer componentProductId;

    @ApiModelProperty(value = "上一次提交时间, yyyy-MM-dd HH:mm:ss")
    private String submitTime;

    @ApiModelProperty(value = "上一次审核时间, yyyy-MM-dd HH:mm:ss")
    private String auditTime;

    @ApiModelProperty(value = "拒绝理由，只有edit_status为3时出现")
    private String rejectReason;

    @ApiModelProperty(value = "审核单id")
    private String auditId;


}
