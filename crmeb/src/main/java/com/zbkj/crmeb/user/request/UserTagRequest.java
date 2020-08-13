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
 * @since 2020-06-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserTagRequest对象", description="会员标签")
public class UserTagRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "标签名称")
    @NotBlank(message = "请填写标签名称")
    private String name;


}
