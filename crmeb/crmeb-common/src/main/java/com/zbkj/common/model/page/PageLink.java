package com.zbkj.common.model.page;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 页面链接
 * </p>
 *
 * @author dazongzi
 * @since 2023-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_page_link")
@ApiModel(value="PageLink对象", description="页面链接")
public class PageLink implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "分类id")
    private Integer cateId;

    @ApiModelProperty(value = "分组1:基础2:分销3:个人中心")
    private Boolean type;

    @ApiModelProperty(value = "页面名称")
    private String name;

    @ApiModelProperty(value = "页面链接")
    private String url;

    @ApiModelProperty(value = "参数")
    private String param;

    @ApiModelProperty(value = "事例")
    private String example;

    @ApiModelProperty(value = "状态")
    private Boolean status;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    @ApiModelProperty(value = "1是商户的链接")
    private Boolean isMer;


}
