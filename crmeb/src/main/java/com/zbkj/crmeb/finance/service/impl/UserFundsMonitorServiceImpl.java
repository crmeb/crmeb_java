package com.zbkj.crmeb.finance.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.dao.UserFundsMonitorDao;
import com.zbkj.crmeb.finance.model.UserFundsMonitor;
import com.zbkj.crmeb.finance.request.FundsMonitorUserSearchRequest;
import com.zbkj.crmeb.finance.service.UserFundsMonitorService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBrokerageRecord;
import com.zbkj.crmeb.user.service.UserBrokerageRecordService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
* UserRechargeServiceImpl 接口实现
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
@Service
public class UserFundsMonitorServiceImpl extends ServiceImpl<UserFundsMonitorDao, UserFundsMonitor> implements UserFundsMonitorService {

    @Resource
    private UserFundsMonitorDao dao;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private UserService userService;

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
            keywords = "%"+request.getKeywords()+"%";
        }
        map.put("keywords", keywords);
        map.put("max", request.getMax());
        map.put("min", request.getMin());
        String sort =  "desc";
        if(!StringUtils.isBlank(request.getSort())){
            sort = request.getSort();
        }
        map.put("sort", sort);
        List<UserFundsMonitor> monitorList = dao.getFundsMonitor(map);
        monitorList.forEach(e -> {
            if (e.getSpreadUid() > 0) {
                User spreadUser = userService.getById(e.getSpreadUid());
                e.setSpreadName(spreadUser.getNickname());
            }
        });
        return monitorList;
    }

    /**
     * 佣金详细记录
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @return
     */
    @Override
    public PageInfo<UserBrokerageRecord> getFundsMonitorDetail(Integer uid, String dateLimit, PageParamRequest pageParamRequest) {
        return userBrokerageRecordService.getFundsMonitorDetail(uid, dateLimit, pageParamRequest);
    }


}

