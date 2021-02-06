package com.zbkj.crmeb.finance.service;

import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.finance.request.UserExtractSearchRequest;
import com.zbkj.crmeb.finance.response.BalanceResponse;
import com.zbkj.crmeb.finance.response.UserExtractResponse;
import com.zbkj.crmeb.front.response.UserExtractRecordResponse;

import java.math.BigDecimal;
import java.util.List;

/**
* UserExtractService 接口
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
public interface UserExtractService extends IService<UserExtract> {

    List<UserExtract> getList(UserExtractSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 提现总金额
     */
    BalanceResponse getBalance(String dateLimit);

    /**
     * 提现总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BalanceResponse
     */
    BigDecimal getWithdrawn(String startTime,String endTime);

    /**
     * 审核中总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BalanceResponse
     */
    BigDecimal getWithdrawning(String startTime, String endTime);

    /**
     * 获取待提现总金额
     * @return 待提现总金额
     */
    BigDecimal getWaiteForDrawn(String startTime,String endTime);

    Boolean create(UserExtractRequest request, Integer userId);

//    BigDecimal getToBeWihDraw(Integer userId);

    BigDecimal getFreeze(Integer userId);

    UserExtractResponse getUserExtractByUserId(Integer userId);

    List<UserExtract> getListByUserIds(List<Integer> userIds);

    /**
     * 提现审核
     * @param id    提现申请id
     * @param status 审核状态 -1 未通过 0 审核中 1 已提现
     * @param backMessage   驳回原因
     * @return  审核结果
     */
    Boolean updateStatus(Integer id,Integer status,String backMessage);

    PageInfo<UserExtractRecordResponse> getExtractRecord(Integer userId, PageParamRequest pageParamRequest);

    BigDecimal getExtractTotalMoney(Integer userId);

    /**
     * 提现申请
     * @return
     */
    Boolean extractApply(UserExtractRequest request);
}
