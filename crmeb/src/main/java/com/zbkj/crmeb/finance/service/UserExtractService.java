package com.zbkj.crmeb.finance.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.finance.request.UserExtractSearchRequest;
import com.zbkj.crmeb.finance.response.BalanceResponse;
import com.zbkj.crmeb.finance.response.UserExtractResponse;

import java.math.BigDecimal;
import java.util.List;

/**
* @author Mr.Zhang
* @description UserExtractService 接口
* @date 2020-05-11
*/
public interface UserExtractService extends IService<UserExtract> {

    List<UserExtract> getList(UserExtractSearchRequest request, PageParamRequest pageParamRequest);

    BalanceResponse getBalance(Integer userId);

    BigDecimal getWithdrawn(Integer userId);

    BigDecimal getUnDrawn(Integer userId);

    Boolean create(UserExtractRequest request, Integer userId);

//    BigDecimal getToBeWihDraw(Integer userId);

    BigDecimal getFreeze(Integer userId);

    UserExtractResponse getUserExtractByUserId(Integer userId);

    List<UserExtract> getListByUserIds(List<Integer> userIds);
}