package com.zbkj.common.vo;

import lombok.Data;

/**
 * 第三级类目
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
public class ThirdCatVo {

    /** 类目ID */
    private Integer thirdCatId;

    /** 类目名称 */
    private String thirdCatName;

    /** 类目资质 */
    private String qualification;

    /** 类目资质类型,0:不需要,1:必填,2:选填 */
    private Integer qualificationType;

    /** 商品资质 */
    private String productQualification;

    /** 商品资质类型,0:不需要,1:必填,2:选填 */
    private Integer productQualificationType;

}
