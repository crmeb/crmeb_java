package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类表
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class CategoryTreeVo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "父级ID")
    private Integer pid;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "类型，类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类， 6 配置分类， 7 秒杀配置")
    private Integer type;

    @ApiModelProperty(value = "地址")
    private String url;

    @ApiModelProperty(value = "扩展字段")
    private String extra;

    @ApiModelProperty(value = "状态, 0正常，1失效")
    private Boolean status;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @JsonInclude(JsonInclude.Include.NON_EMPTY) //属性为 空（""）[] 或者为 NULL 都不序列化
    private List<CategoryTreeVo> child = new ArrayList<>();
}
