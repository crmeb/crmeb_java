package com.zbkj.crmeb.express.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 快递公司表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_express")
@ApiModel(value="ExpressRequest对象", description="快递公司")
public class ExpressRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "快递公司简称", required = true)
    @NotBlank(message = "快递公司简称必须填写")
    private String code;

    @ApiModelProperty(value = "快递公司全称", required = true)
    @NotBlank(message = "快递公司全称必须填写")
    private String name;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序数字必须填写")
    private Integer sort;

    @ApiModelProperty(value = "是否显示 0=否 1=是", required = true)
    @NotNull(message = "请选择是否弃用")
    private Integer isShow;


}
