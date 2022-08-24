package com.zbkj.common.request;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.common.constants.RegularConstants;
import com.zbkj.common.utils.ValidateFormUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 后台管理员表
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
@TableName("eb_system_admin")
@ApiModel(value="SystemAdminAddRequest对象", description="后台管理员表")
public class SystemAdminAddRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "后台管理员账号", required = true)
    @NotNull(message = "后台管理员账号不能为空")
    @Length(max = 32, message = "账号长度不能超过32个字符")
    private String account;

    @ApiModelProperty(value = "后台管理员密码", required = true)
    @NotNull(message = "管理员密码不能为空")
    @Length(max = 32, message = "密码长度不能超过32个字符")
    private String pwd;

    @ApiModelProperty(value = "后台管理员姓名", required = true)
    @NotNull(message = "管理姓名不能为空")
    @Length(max = 16, message = "姓名长度不能超过16个字符")
    private String realName;

    @ApiModelProperty(value = "后台管理员角色(menus_id)", required = true)
    @NotNull(message = "后台管理员角色不能为空")
    @Length(max = 128, message = "角色组合长度不能超过128个字符")
    private String roles;

    @ApiModelProperty(value = "后台管理员状态 1有效0无效", required = true)
    @NotNull(message = "status 字段不能为空")
    @Range(min = 0, max = 1, message = "未知的状态")
    private Boolean status;

    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = RegularConstants.PHONE_TWO, message = "请填写正确的手机号")
    private String phone;
}
