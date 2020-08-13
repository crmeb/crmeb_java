package com.zbkj.crmeb.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.finance.model.UserFundsMonitor;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.finance.request.FundsMonitorUserSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description UserRechargeService 接口
* @date 2020-05-11
*/
public interface UserFundsMonitorService extends IService<UserFundsMonitor> {

    List<UserFundsMonitor> getFundsMonitor(FundsMonitorUserSearchRequest request, PageParamRequest pageParamRequest);
}