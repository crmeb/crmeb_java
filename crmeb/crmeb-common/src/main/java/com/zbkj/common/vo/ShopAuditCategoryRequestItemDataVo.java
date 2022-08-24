package com.zbkj.common.vo;

import lombok.Data;

/**
 * 上传类目资质 itemData
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
public class ShopAuditCategoryRequestItemDataVo {
        private Integer level1; // 一级类目
        private Integer level2; // 二级类目
        private Integer level3; // 三级类目
        private String certificate; // 资质材料
}
