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
 * 通知模板表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_notice")
@ApiModel(value="SystemNotice对象", description="通知模板表")
public class SystemNotice implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "通知模板id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "通知标题")
    private String title;

    @ApiModelProperty(value = "通知类型")
    private String type;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "链接")
    private String url;

    @ApiModelProperty(value = "通知数据")
    private String tableTitle;

    @ApiModelProperty(value = "通知模板")
    private String template;

    @ApiModelProperty(value = "通知管理员id")
    private String pushAdmin;

    @ApiModelProperty(value = "状态")
    private Boolean status;


}
