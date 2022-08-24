package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 上传品牌信息 request item
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
public class ShopAuditBrandRequestItemVo {

    /** 营业执照或组织机构代码证，图片url/media_id */
    @NotBlank(message = "营业执照或组织机构代码证不能为空")
    private String license;

    /** 品牌信息 */
    @TableField(value = "brand_info")
    private ShopAuditBrandRequestItemDataVo brandInfo;
}
