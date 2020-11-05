package com.zbkj.crmeb.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.finance.dao.UserRechargeDao;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.finance.request.UserRechargeSearchRequest;
import com.zbkj.crmeb.finance.response.UserRechargeResponse;
import com.zbkj.crmeb.finance.service.UserRechargeService;
import com.zbkj.crmeb.front.request.UserRechargeRequest;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataUnit;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
public class UserRechargeServiceImpl extends ServiceImpl<UserRechargeDao, UserRecharge> implements UserRechargeService {

    @Resource
    private UserRechargeDao dao;

    @Autowired
    private UserService userService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-11
    * @return List<UserRecharge>
    */
    @Override
    public PageInfo<UserRechargeResponse> getList(UserRechargeSearchRequest request, PageParamRequest pageParamRequest) {
        Page<UserRecharge> userRechargesList = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
        //带 UserExtract 类的多条件查询
        LambdaQueryWrapper<UserRecharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i.
                    or().like(UserRecharge::getUid, request.getKeywords()+""). //充值用户UID
                    or().like(UserRecharge::getOrderId, request.getKeywords()) //订单号
            );
        }
        //是否充值
        if(request.getPaid() != null){
            lambdaQueryWrapper.eq(UserRecharge::getPaid, request.getPaid());
        }

        //时间范围
        if(dateLimit.getStartTime() != null && dateLimit.getEndTime() != null){
            //判断时间
            int compareDateResult = DateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), Constants.DATE_FORMAT);
            if(compareDateResult == -1){
                throw new CrmebException("开始时间不能大于结束时间！");
            }

            lambdaQueryWrapper.between(UserRecharge::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        List<UserRechargeResponse> responses = new ArrayList<>();
        List<UserRecharge> userRecharges = dao.selectList(lambdaQueryWrapper);
        List<Integer> userIds = userRecharges.stream().map(UserRecharge::getUid).collect(Collectors.toList());
        HashMap<Integer, User> userHashMap = new HashMap<>();

        if(userIds.size() > 0)
        userHashMap = userService.getMapListInUid(userIds);

        HashMap<Integer, User> finalUserHashMap = userHashMap;
        userRecharges.stream().map(e-> {
            User user = finalUserHashMap.get(e.getUid());
            UserRechargeResponse r = new UserRechargeResponse();
            BeanUtils.copyProperties(e,r);
            if(null != user){
                r.setAvatar(user.getAvatar());
                r.setNickname(user.getNickname());
            }
            responses.add(r);
            return e;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(userRechargesList,responses);
    }

    /**
     * 充值总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return HashMap<String, BigDecimal>
     */
    @Override
    public HashMap<String, BigDecimal> getBalanceList() {
        HashMap<String, BigDecimal> map = new HashMap<>();

        BigDecimal routine = dao.getSumByType("routine");
        if(null == routine) routine = BigDecimal.ZERO;
        map.put("routine", routine); //小程序充值

        BigDecimal weChat = dao.getSumByType("weixin");
        if(null == weChat) weChat = BigDecimal.ZERO;
        map.put("weChat", weChat); //公众号充值

        BigDecimal total = dao.getSumByType("");
        if(null == total) total = BigDecimal.ZERO;
        map.put("total", total); //总金额

        BigDecimal refund = dao.getSumByRefund();
        if(null == refund) refund = BigDecimal.ZERO;
        map.put("refund", refund);

        map.put("other", total.subtract(routine).subtract(weChat)); //其他金额

        return map;
    }

    /**
     * 根据对象查询订单
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return UserRecharge
     */
    @Override
    public UserRecharge getInfoByEntity(UserRecharge userRecharge) {
        LambdaQueryWrapper<UserRecharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.setEntity(userRecharge);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 创建订单对象
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return UserRecharge
     */
    @Override
    public UserRecharge create(UserRechargeRequest request) {
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setUid(request.getUserId());
        userRecharge.setOrderId(CrmebUtil.getOrderNo(Constants.PAY_TYPE_WE_CHAT));
        userRecharge.setPrice(request.getPrice());
        userRecharge.setGivePrice(request.getGivePrice());
        userRecharge.setRechargeType(request.getFromType());
        save(userRecharge);
        return userRecharge;
    }

    /**
     * 更新充值订单
     * @param userRecharge UserRecharge 充值订单数据
     * @author Mr.Zhang
     * @since 2020-07-01
     */
    @Override
    public Boolean complete(UserRecharge userRecharge) {
        try{
            userRecharge.setPaid(true);
            userRecharge.setPayTime(DateUtil.nowDateTime());
            return updateById(userRecharge);
        }catch (Exception e){
            throw new CrmebException("回调失败！, 更新状态失败！");
        }
    }

    /**
     * 充值总金额
     * @param userId Integer 用户uid
     * @author Mr.Zhang
     * @since 2020-05-29
     * @return UserBill
     */
    @Override
    public BigDecimal getSumBigDecimal(Integer userId) {
        QueryWrapper<UserRecharge> queryWrapper = new QueryWrapper<>();


        queryWrapper.select("sum(price) as price").
                eq("uid", userId).
                eq("paid", 1);
        UserRecharge userRecharge = dao.selectOne(queryWrapper);
        if(null == userRecharge || null == userRecharge.getPrice()){
            return BigDecimal.ZERO;
        }
        return userRecharge.getPrice();
    }
}

