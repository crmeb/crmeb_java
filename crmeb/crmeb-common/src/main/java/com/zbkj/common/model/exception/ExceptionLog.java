package com.zbkj.common.model.exception;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * <p>
 * 异常信息表
 * </p>
 *
 * @author HZW
 * @since 2023-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_exception_log")
@ApiModel(value = "ExceptionLog对象", description = "异常信息表")
public class ExceptionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("异常日志ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("异常的url")
    @TableField("exp_url")
    private String expUrl;

    @ApiModelProperty("异常的参数")
    @TableField("exp_params")
    private String expParams;

    @ApiModelProperty("异常的类型")
    @TableField("exp_type")
    private String expType;

    @ApiModelProperty("异常的类名")
    @TableField("exp_controller")
    private String expController;

    @ApiModelProperty("异常的方法名")
    @TableField("exp_method")
    private String expMethod;

    @ApiModelProperty("异常详细信息")
    @TableField("exp_detail")
    private String expDetail;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;


}
