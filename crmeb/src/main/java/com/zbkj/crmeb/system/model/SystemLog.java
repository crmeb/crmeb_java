package com.zbkj.crmeb.system.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 管理员操作记录表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_log")
@ApiModel(value="SystemLog对象", description="管理员操作记录表")
public class SystemLog implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "管理员操作记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "管理员id")
    private Integer adminId;

    @ApiModelProperty(value = "管理员姓名")
    private String adminName;

    @ApiModelProperty(value = "链接")
    private String path;

    @ApiModelProperty(value = "行为")
    private String page;

    @ApiModelProperty(value = "访问类型")
    private String method;

    @ApiModelProperty(value = "登录IP")
    private String ip;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "操作时间")
    private Integer addTime;

    @ApiModelProperty(value = "商户id")
    private Integer merchantId;


}
