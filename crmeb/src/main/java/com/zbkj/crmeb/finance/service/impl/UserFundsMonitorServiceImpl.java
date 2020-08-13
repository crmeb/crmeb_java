package com.zbkj.crmeb.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.DateUtil;
import com.zbkj.crmeb.finance.dao.UserFundsMonitorDao;
import com.zbkj.crmeb.finance.dao.UserRechargeDao;
import com.zbkj.crmeb.finance.model.UserFundsMonitor;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.finance.request.FundsMonitorUserSearchRequest;
import com.zbkj.crmeb.finance.request.UserRechargeSearchRequest;
import com.zbkj.crmeb.finance.service.UserFundsMonitorService;
import com.zbkj.crmeb.finance.service.UserRechargeService;
import com.zbkj.crmeb.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @Description UserRechargeServiceImpl 接口实现
* @since  2020-05-11
*/
@Service
public class UserFundsMonitorServiceImpl extends ServiceImpl<UserFundsMonitorDao, UserFundsMonitor> implements UserFundsMonitorService {

    @Resource
    private UserFundsMonitorDao dao;


    /**
     * 佣金列表
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return List<User>
     */
    @Override
    public List<UserFundsMonitor> getFundsMonitor(FundsMonitorUserSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        HashMap<String, Object> map = new HashMap<>();
        String keywords = null;
        if(!StringUtils.isBlank(request.getKeywords())){
            keywords = "'%"+request.getKeywords()+"%'";
        }
        map.put("keywords", keywords);
        map.put("max", request.getMax());
        map.put("min", request.getMin());
        String sort =  "desc";
        if(!StringUtils.isBlank(request.getSort())){
            sort = request.getSort();
        }
        map.put("sort", sort);
        return dao.getFundsMonitor(map);
    }

}

