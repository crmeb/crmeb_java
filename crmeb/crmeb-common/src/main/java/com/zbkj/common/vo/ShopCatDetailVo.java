package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 *  商品类型详情Vo对象
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
public class ShopCatDetailVo {

    /** 类目ID */
    @TableField(value = "third_cat_id")
    private Integer thirdCatId;

    /** 类目名称 */
    @TableField(value = "third_cat_name")
    private String thirdCatName;

    /** 类目资质 */
    private String qualification;

    /** 类目资质类型,0:不需要,1:必填,2:选填 */
    @TableField(value = "qualification_type")
    private Integer qualificationType;

    /** 商品资质 */
    @TableField(value = "product_qualification")
    private String productQualification;

    /** 商品资质类型,0:不需要,1:必填,2:选填 */
    @TableField(value = "product_qualification_type")
    private Integer productQualificationType;

    /** 二级类目ID */
    @TableField(value = "second_cat_id")
    private Integer secondCatId;

    /** 二级类目名称 */
    @TableField(value = "second_cat_name")
    private String secondCatName;

    /** 一级类目ID */
    @TableField(value = "first_cat_id")
    private Integer firstCatId;

    /** 一级类目名称 */
    @TableField(value = "first_cat_name")
    private String firstCatName;
}
