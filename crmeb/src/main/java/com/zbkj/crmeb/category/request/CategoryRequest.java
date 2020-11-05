package com.zbkj.crmeb.category.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 分类表 Request
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_category")
@ApiModel(value="Category对象", description="分类表")
public class CategoryRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "父级ID")
    @NotNull(message = "请选择父级分类")
    @Min(value = 0, message = "请选择父级分类")
    private Integer pid;

    @ApiModelProperty(value = "分类名称")
    @NotBlank(message = "分类名称必须填写")
    private String name;

    @ApiModelProperty(value = "类型，类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类， 6 配置分类， 7 秒杀配置")
    @NotNull(message = "类型必须选择")
    @Range(min = 1, max = 6, message = "类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类， 6 配置分类， 7 秒杀配置之间") //取值范围
    private Integer type;

    @ApiModelProperty(value = "地址")
//    @NotBlank(message = "地址名称必须填写")
    private String url;

    @ApiModelProperty(value = "扩展字段")
    private String extra;

    @ApiModelProperty(value = "状态, 0正常，1失效")
    @NotNull(message = "状态必须选择")
    private Boolean status;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序数字为空")  //不可为空
    @Min(value = 0, message = "排序数字必须大于等于0") //数字最小值为0
    private Integer sort;

}
