package com.zbkj.common.vo;

import lombok.Data;

import java.util.List;

/**
 *  自定义交易组件商品详情Vo
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
public class ShopSpuInfoVo {

    /** 商品详情图文 */
    private String desc;

    /** 商品详情图片 */
    private List<String> imgs;

}
