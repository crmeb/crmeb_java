package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 *  商品审核信息Vo对象
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
public class ShopSpuAuditVo {

    /** 上一次提交时间, yyyy-MM-dd HH:mm:ss */
    @TableField(value = "submit_time")
    private String submitTime;

    /** 上一次审核时间, yyyy-MM-dd HH:mm:ss */
    @TableField(value = "audit_time")
    private String auditTime;

    /** 拒绝理由，只有edit_status为3时出现 */
    @TableField(value = "reject_reason")
    private String rejectReason;

    /** 审核单id */
    @TableField(value = "audit_id")
    private String auditId;

}
