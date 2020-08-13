package com.zbkj.crmeb.finance.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户提现表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_extract")
@ApiModel(value="UserExtract对象", description="用户提现表")
public class UserExtract implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer uid;

    @ApiModelProperty(value = "名称")
    private String realName;

    @ApiModelProperty(value = "bank = 银行卡 alipay = 支付宝wx=微信")
    private String extractType;

    @ApiModelProperty(value = "银行卡")
    private String bankCode;

    @ApiModelProperty(value = "开户地址")
    private String bankAddress;

    @ApiModelProperty(value = "支付宝账号")
    private String alipayCode;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal extractPrice;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "金额")
    private BigDecimal balance;

    @ApiModelProperty(value = "无效原因")
    private String failMsg;

    @ApiModelProperty(value = "-1 未通过 0 审核中 1 已提现")
    private Integer status;

    @ApiModelProperty(value = "微信号")
    private String wechat;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "失败时间")
    private Date failTime;


}
