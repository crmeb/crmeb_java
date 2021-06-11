package com.zbkj.crmeb.user.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户积分记录响应对象
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
@ApiModel(value="UserIntegralRecordResponse对象", description="用户积分记录响应对象")
public class UserIntegralRecordResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "记录id")
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "关联id-orderNo,(sign,system默认为0）")
    private String linkId;

    @ApiModelProperty(value = "关联类型（order,sign,system）")
    private String linkType;

    @ApiModelProperty(value = "类型：1-增加，2-扣减")
    private Integer type;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "积分")
    private Integer integral;

    @ApiModelProperty(value = "剩余")
    private Integer balance;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "状态：1-订单创建，2-冻结期，3-完成，4-失效（订单退款）")
    private Integer status;

    @ApiModelProperty(value = "冻结期时间（天）")
    private Integer frozenTime;

    @ApiModelProperty(value = "解冻时间")
    private Long thawTime;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;
}
