package com.zbkj.crmeb.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.dao.UserFundsMonitorDao;
import com.zbkj.crmeb.finance.model.UserFundsMonitor;
import com.zbkj.crmeb.finance.request.FundsMonitorUserSearchRequest;
import com.zbkj.crmeb.finance.service.UserFundsMonitorService;
import com.zbkj.crmeb.user.model.UserBrokerageRecord;
import com.zbkj.crmeb.user.service.UserBrokerageRecordService;
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
//        if (CollUtil.isEmpty(monitorList)) {
//            return monitorList;
//        }
//        List<Integer> spreadUidList = monitorList.stream().map(UserFundsMonitor::getSpreadUid).distinct().collect(Collectors.toList());
//        HashMap<Integer, User> mapListInUid = userService.getMapListInUid(spreadUidList);
//        for (UserFundsMonitor temp: monitorList) {
//            if (ObjectUtil.isNotNull(temp.getSpreadUid())) {
//                User user = mapListInUid.get(temp.getSpreadUid());
//                if (ObjectUtil.isNotNull(user)) {
//                    temp.setSpreadName(Optional.ofNullable(user.getNickname()).orElse(""));
//                }
//            }
//        }
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

