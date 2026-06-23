package com.zbkj.common.model.theme;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 主题下载记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_theme_download")
@ApiModel(value = "ThemeDownload对象", description = "主题下载记录表")
public class ThemeDownload implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "模版名称")
    private String title;

    @ApiModelProperty(value = "模版ID")
    private Integer tid;

    @ApiModelProperty(value = "下载时间")
    private Integer downloadTime;

    @ApiModelProperty(value = "下载地址")
    private String downloadUrl;
}
