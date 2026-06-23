package com.zbkj.common.model.wechat.video;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 组件类目表
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_pay_component_cat")
@ApiModel(value="PayComponentCat对象", description="组件类目表")
public class PayComponentCat implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "类目ID")
    private Integer thirdCatId;

    @ApiModelProperty(value = "类目名称")
    private String thirdCatName;

    @ApiModelProperty(value = "类目资质")
    private String qualification;

    @ApiModelProperty(value = "类目资质类型,0:不需要,1:必填,2:选填")
    private Integer qualificationType;

    @ApiModelProperty(value = "商品资质")
    private String productQualification;

    @ApiModelProperty(value = "商品资质类型,0:不需要,1:必填,2:选填")
    private Integer productQualificationType;

    @ApiModelProperty(value = "二级类目ID")
    private Integer secondCatId;

    @ApiModelProperty(value = "二级类目名称")
    private String secondCatName;

    @ApiModelProperty(value = "一级类目ID")
    private Integer firstCatId;

    @ApiModelProperty(value = "一级类目名称")
    private String firstCatName;


}
