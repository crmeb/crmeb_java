package com.zbkj.common.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 门店店员表
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_store_staff")
@ApiModel(value="SystemStoreStaff对象", description="门店店员表")
public class SystemStoreStaffRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "移动端用户 id")
    @NotNull(message = "请选择移动端用户")
    @Min(value = 1, message = "请选择移动端用户")
    private Integer uid;

    @ApiModelProperty(value = "管理员头像")
    private String avatar;

    @ApiModelProperty(value = "提货点id，开启订单核销时必填")
    private Integer storeId;

    @ApiModelProperty(value = "移动端管理员/核销员名称 [昵称]")
    @NotBlank(message = "管理员名称不能为空")
    @Length(max = 64,message = "管理员名称不能超过64个字符")
    private String staffName;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "核销开关：0=关闭，1=开启")
    private Integer verifyStatus;

    @ApiModelProperty(value = "商家管理状态：0=禁用，1=启用")
    private Integer status;


}
