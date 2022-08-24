package com.zbkj.common.vo;

import lombok.Data;

/**
 * 上传类目资质Item
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
public class ShopAuditCategoryRequestItemVo {
    // 营业执照或组织机构代码证，图片url
    private String license;
    // Response ItemData
    private ShopAuditCategoryRequestItemDataVo category_info;
}
