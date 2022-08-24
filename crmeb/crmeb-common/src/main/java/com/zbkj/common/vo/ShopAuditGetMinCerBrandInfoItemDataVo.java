package com.zbkj.common.vo;

import java.util.List;

/**
 * 获取小程序提交过的入驻资质信息 ItemData
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
public class ShopAuditGetMinCerBrandInfoItemDataVo {
    // 品牌名
    private String brand_wording;
    // 商标注册证
    private List<String> sale_authorization;
    // 商标授权书
    private List<String> trademark_registration_certificate;

}
