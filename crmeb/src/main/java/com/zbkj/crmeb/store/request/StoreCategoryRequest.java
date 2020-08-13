package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author stivepeim
 * @title: StoreCategoryRequest
 * @projectName crmeb
 * @description: TODO
 * @date 2020/5/2815:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_category")
@ApiModel(value="StoreCategory对象", description="商品分类表")
public class StoreCategoryRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品分类表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "父id")
    private Integer pid;

    @ApiModelProperty(value = "分类名称")
    private String cateName;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "图标")
    private String pic;

    @ApiModelProperty(value = "是否推荐")
    private Boolean isShow;

    @ApiModelProperty(value = "添加时间")
    private Integer addTime;
}
