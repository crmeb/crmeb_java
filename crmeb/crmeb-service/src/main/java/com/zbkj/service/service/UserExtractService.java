package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.UserExtractRecordResponse;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.UserExtract;
import com.zbkj.common.request.UserExtractRequest;
import com.zbkj.common.request.UserExtractSearchRequest;
import com.zbkj.common.response.BalanceResponse;
import com.zbkj.common.response.UserExtractResponse;

import java.math.BigDecimal;
import java.util.List;

/**
* UserExtractService 接口
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

    UserExtractResponse getUserExtractByUserId(Integer userId);

    /**
     * 提现审核
     * @param id    提现申请id
     * @param status 审核状态 -1 未通过 0 审核中 1 已提现
     * @param backMessage   驳回原因
     * @return  审核结果
     */
    Boolean updateStatus(Integer id,Integer status,String backMessage);

    /**
     * 获取提现记录列表
     * @param userId 用户uid
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserExtractRecordResponse> getExtractRecord(Integer userId, PageParamRequest pageParamRequest);

    BigDecimal getExtractTotalMoney(Integer userId);

    /**
     * 提现申请
     * @return Boolean
     */
    Boolean extractApply(UserExtractRequest request);

    /**
     * 修改提现申请
     * @param id 申请id
     * @param userExtractRequest 具体参数
     */
    Boolean updateExtract(Integer id, UserExtractRequest userExtractRequest);

    /**
     * 提现申请待审核数量
     * @return Integer
     */
    Integer getNotAuditNum();
}
