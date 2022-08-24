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
@TableName("eb_system_store_staff")
@ApiModel(value="SystemStoreStaff对象", description="门店店员表")
public class SystemStoreStaffRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "管理员 id")
    @Min(value = 1, message = "请选择管理员")
    private Integer uid;

    @ApiModelProperty(value = "管理员头像")
    private String avatar;

    @ApiModelProperty(value = "提货点id")
    @Min(value = 1, message = "请选择提货点")
    private Integer storeId;

    @ApiModelProperty(value = "核销员 名称 [昵称]")
    @NotBlank(message = "核销员名称不能为空")
    @Length(max = 64,message = "核销员名称不能超过64个字符")
    private String staffName;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "核销开关")
    private boolean verifyStatus = false;

    @ApiModelProperty(value = "状态")
    private boolean status = false;


}
