package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 生成订单Vo对象
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
public class ShopOrderAddressInfoAddVo {

    /** 收件人姓名 */
    @TableField(value = "receiver_name")
    private String receiverName;

    /** 详细收货地址信息 */
    @TableField(value = "detailed_address")
    private String detailedAddress;

    /** 收件人手机号码 */
    @TableField(value = "tel_number")
    private String telNumber;

    /** 国家 */
    private String country;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 乡镇 */
    private String town;
}
