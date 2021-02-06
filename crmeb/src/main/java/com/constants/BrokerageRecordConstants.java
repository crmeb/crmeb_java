package com.constants;

/**
 * 佣金记录常量类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public class BrokerageRecordConstants {

    /** 佣金记录类型—增加 */
    public static final Integer BROKERAGE_RECORD_TYPE_ADD = 1;

    /** 佣金记录类型—扣减 */
    public static final Integer BROKERAGE_RECORD_TYPE_SUB = 2;

    /** 佣金记录状态—创建 */
    public static final Integer BROKERAGE_RECORD_STATUS_CREATE = 1;

    /** 佣金记录状态—冻结期 */
    public static final Integer BROKERAGE_RECORD_STATUS_FROZEN = 2;

    /** 佣金记录状态—完成 */
    public static final Integer BROKERAGE_RECORD_STATUS_COMPLETE = 3;

    /** 佣金记录状态—失效（订单退款） */
    public static final Integer BROKERAGE_RECORD_STATUS_INVALIDATION = 4;

    /** 佣金记录状态—提现申请 */
    public static final Integer BROKERAGE_RECORD_STATUS_WITHDRAW = 5;

    /** 佣金记录关联类型—订单 */
    public static final String BROKERAGE_RECORD_LINK_TYPE_ORDER = "order";

    /** 佣金记录关联类型—提现 */
    public static final String BROKERAGE_RECORD_LINK_TYPE_WITHDRAW = "withdraw";

    /** 佣金记录关联类型—转余额 */
    public static final String BROKERAGE_RECORD_LINK_TYPE_YUE = "yue";

    /** 佣金记录标题—订单获得佣金 */
    public static final String BROKERAGE_RECORD_TITLE_ORDER = "获得推广佣金";

    /** 佣金记录标题—提现申请 */
    public static final String BROKERAGE_RECORD_TITLE_WITHDRAW_APPLY = "提现申请";

    /** 佣金记录标题—提现拒绝 */
    public static final String BROKERAGE_RECORD_TITLE_WITHDRAW_FAIL = "提现申请拒绝";

    /** 佣金记录标题—提现拒绝 */
    public static final String BROKERAGE_RECORD_TITLE_BROKERAGE_YUE = "佣金转余额";
}
