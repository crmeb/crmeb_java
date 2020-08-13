package com.zbkj.crmeb.system.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 设置用户等级表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_user_level")
@ApiModel(value="SystemUserLevelSearchRequest对象", description="设置用户等级表")
public class SystemUserLevelSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "会员名称")
    @NotNull
    private String name;

    @ApiModelProperty(value = "是否显示 1=显示,0=隐藏")
    @NotNull
    private Boolean isShow;

    @ApiModelProperty(value = "是否删除.1=删除,0=未删除")
    @NotNull
    private Boolean isDel;
}
