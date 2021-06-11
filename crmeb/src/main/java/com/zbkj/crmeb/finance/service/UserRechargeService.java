package com.zbkj.crmeb.finance.service;

import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.finance.request.UserRechargeRefundRequest;
import com.zbkj.crmeb.finance.request.UserRechargeSearchRequest;
import com.zbkj.crmeb.finance.response.UserRechargeResponse;
import com.zbkj.crmeb.front.request.UserRechargeRequest;

import java.math.BigDecimal;
import java.util.HashMap;

/**
* UserRechargeService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface UserRechargeService extends IService<UserRecharge> {

    PageInfo<UserRechargeResponse> getList(UserRechargeSearchRequest request, PageParamRequest pageParamRequest);

    HashMap<String, BigDecimal> getBalanceList();

    UserRecharge getInfoByEntity(UserRecharge userRecharge);

    UserRecharge create(UserRechargeRequest request);

    Boolean complete(UserRecharge userRecharge);

    /**
     * 充值退款
     * @param request 退款参数
     * @return Boolean
     */
    Boolean refund(UserRechargeRefundRequest request);

    /**
     * 获取用户累计充值金额
     * @param uid 用户uid
     * @return BigDecimal
     */
    BigDecimal getTotalRechargePrice(Integer uid);
}
