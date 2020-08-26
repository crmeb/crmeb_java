package com.zbkj.crmeb.finance.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.finance.request.UserRechargeSearchRequest;
import com.zbkj.crmeb.front.request.UserRechargeRequest;
import com.zbkj.crmeb.store.model.StoreOrder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @description UserRechargeService 接口
* @date 2020-05-11
*/
public interface UserRechargeService extends IService<UserRecharge> {

    List<UserRecharge> getList(UserRechargeSearchRequest request, PageParamRequest pageParamRequest);

    HashMap<String, BigDecimal> getBalanceList();

    UserRecharge getInfoByEntity(UserRecharge userRecharge);

    UserRecharge create(UserRechargeRequest request);

    Boolean complete(UserRecharge userRecharge);

    BigDecimal getSumBigDecimal(Integer uid);
}