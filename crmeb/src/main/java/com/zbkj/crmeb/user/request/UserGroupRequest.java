package com.zbkj.crmeb.user.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 用户分组表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_group")
@ApiModel(value="UserGroup对象", description="用户分组表")
public class UserGroupRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户分组名称")
    @NotBlank(message = "请填写分组名称")
    private String groupName;

}
