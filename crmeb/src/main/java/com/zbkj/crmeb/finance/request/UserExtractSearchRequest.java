package com.zbkj.crmeb.finance.request;

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
public class UserExtractSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "搜索关键字")
    private String keywords;

    @ApiModelProperty(value = "bank = 银行卡 alipay = 支付宝wx=微信")
    private String extractType;

    @ApiModelProperty(value = "-1 未通过 0 审核中 1 已提现")
    private Integer status;

    @ApiModelProperty(value = "添加时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

}
