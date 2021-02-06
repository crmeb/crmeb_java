package com.zbkj.crmeb.combination.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.UserConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.combination.dao.StorePinkDao;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.request.StorePinkSearchRequest;
import com.zbkj.crmeb.combination.response.StorePinkAdminListResponse;
import com.zbkj.crmeb.combination.response.StorePinkDetailResponse;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.front.request.OrderRefundApplyRequest;
import com.zbkj.crmeb.front.service.OrderService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StorePinkService 实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class StorePinkServiceImpl extends ServiceImpl<StorePinkDao, StorePink> implements StorePinkService {

    @Resource
    private StorePinkDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private UserTokenService userTokenService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author HZW
    * @since 2020-11-13
    * @return List<StorePink>
    */
    @Override
    public PageInfo<StorePinkAdminListResponse> getList(StorePinkSearchRequest request, PageParamRequest pageParamRequest) {
        Page<StorePink> pinkPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(StorePink::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            lqw.between(StorePink::getAddTime, DateUtil.dateStr2Timestamp(dateLimit.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN), DateUtil.dateStr2Timestamp(dateLimit.getEndTime(), Constants.DATE_TIME_TYPE_END));
        }
        lqw.eq(StorePink::getKId, 0);
        lqw.orderByDesc(StorePink::getId);
        List<StorePink> storePinks = dao.selectList(lqw);
        if (CollUtil.isEmpty(storePinks)) {
            return CommonPage.copyPageInfo(pinkPage, CollUtil.newArrayList());
        }
        List<StorePinkAdminListResponse> list = storePinks.stream().map(pink -> {
            StorePinkAdminListResponse storePinkResponse = new StorePinkAdminListResponse();
            BeanUtils.copyProperties(pink, storePinkResponse);
            Integer countPeople = getCountByKidAndCid(pink.getCid(), pink.getId());
            storePinkResponse.setCountPeople(countPeople);
            storePinkResponse.setAddTime(DateUtil.timestamp2DateStr(pink.getAddTime(), Constants.DATE_FORMAT));
            storePinkResponse.setStopTime(DateUtil.timestamp2DateStr(pink.getStopTime(), Constants.DATE_FORMAT));
            StoreCombination combination = storeCombinationService.getById(pink.getCid());
            storePinkResponse.setTitle(combination.getTitle());
            return storePinkResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pinkPage, list);
    }

    /**
     * 获取拼团列表cid
     * @param cid 拼团商品id
     * @return
     */
    @Override
    public List<StorePink> getListByCid(Integer cid) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StorePink::getCid, cid);
        lqw.orderByDesc(StorePink::getId);
        return dao.selectList(lqw);
    }

    /**
     * 拼团实体查询
     * @param storePink
     * @return
     */
    @Override
    public List<StorePink> getByEntity(StorePink storePink) {
        LambdaQueryWrapper<StorePink> lqw = Wrappers.lambdaQuery();
        lqw.setEntity(storePink);
        return dao.selectList(lqw);
    }

    /**
     * PC拼团详情列表
     * @param pinkId 团长pinkId
     * @return
     */
    @Override
    public List<StorePinkDetailResponse> getAdminList(Integer pinkId) {
        LambdaQueryWrapper<StorePink> lqw = Wrappers.lambdaQuery();
        lqw.eq(StorePink::getId, pinkId).or().eq(StorePink::getKId, pinkId);
        lqw.orderByDesc(StorePink::getId);
        List<StorePink> pinkList = dao.selectList(lqw);
        // 将拼团状态提换为订单状态
        List<StorePinkDetailResponse> responseList = pinkList.stream().map(pink -> {
            StorePinkDetailResponse response = new StorePinkDetailResponse();
            BeanUtils.copyProperties(pink, response);
            StoreOrder storeOrder = storeOrderService.getByOderId(pink.getOrderId());
            if (ObjectUtil.isNotNull(storeOrder)) {
                response.setOrderStatus(storeOrder.getStatus());
                response.setRefundStatus(storeOrder.getRefundStatus());
            }
            return response;
        }).collect(Collectors.toList());
        return responseList;
    }

    @Override
    public List<StorePink> getListByCidAndKid(Integer cid, Integer kid) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StorePink::getCid, cid);
        lqw.eq(StorePink::getKId, kid);
        lqw.eq(StorePink::getIsRefund, false);
        lqw.orderByDesc(StorePink::getId);
        return dao.selectList(lqw);
    }

    @Override
    public Integer getCountByKid(Integer pinkId) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.select(StorePink::getId);
        lqw.eq(StorePink::getIsRefund, false);
        lqw.and(i -> i.eq(StorePink::getKId, pinkId).or().eq(StorePink::getId, pinkId));
        return dao.selectCount(lqw);
    }

    /**
     * 检查状态，更新数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void detectionStatus() {
        // 查找所有结束时间小等于当前的进行中拼团团长
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StorePink::getStatus, 1);
        lqw.eq(StorePink::getKId, 0);
        lqw.le(StorePink::getStopTime, System.currentTimeMillis());
        List<StorePink> headList = dao.selectList(lqw);
        if (CollUtil.isEmpty(headList)) {
            return ;
        }
        /**
         * 1.判断是否拼团成功
         * 2.成功的修改状态
         * 3.失败的拼团改为失败，订单申请退款
         */
        List<StorePink> pinkSuccessList = CollUtil.newArrayList();
        List<StorePink> pinkFailList = CollUtil.newArrayList();
        List<OrderRefundApplyRequest> applyList = CollUtil.newArrayList();
        for (StorePink headPink : headList) {
            // 查询团员
            List<StorePink> memberList = getListByCidAndKid(headPink.getCid(), headPink.getId());
            memberList.add(headPink);
            if (headPink.getPeople().equals(memberList.size())) {
                memberList.forEach(i -> i.setStatus(2));
                pinkSuccessList.addAll(memberList);
                continue;
            }
            // 计算虚拟比例，判断是否拼团成功
            StoreCombination storeCombination = storeCombinationService.getById(headPink.getCid());
            Integer virtual = storeCombination.getVirtualRation();// 虚拟成团比例
            if (headPink.getPeople() <= memberList.size() + virtual) {
                // 可以虚拟成团
                memberList.forEach(i -> i.setStatus(2).setIs_virtual(true));
                pinkSuccessList.addAll(memberList);
                continue;
            }
            // 失败
            headPink.setStatus(3);
            // 订单申请退款
            OrderRefundApplyRequest refundRequest = new OrderRefundApplyRequest();
            refundRequest.setId(headPink.getOrderIdKey());
            refundRequest.setText("拼团订单取消，申请退款");
            refundRequest.setExplain("用户取消拼团订单，申请退款");
            pinkFailList.add(headPink);
            applyList.add(refundRequest);

            // 团员处理
            if (CollUtil.isNotEmpty(memberList)) {
                memberList.forEach(i -> i.setStatus(3));
                List<OrderRefundApplyRequest> tempApplyList = memberList.stream().map(i -> {
                    OrderRefundApplyRequest tempRefundRequest = new OrderRefundApplyRequest();
                    tempRefundRequest.setId(headPink.getOrderIdKey());
                    tempRefundRequest.setText("拼团订单取消，申请退款");
                    tempRefundRequest.setExplain("用户取消拼团订单，申请退款");
                    return tempRefundRequest;
                }).collect(Collectors.toList());
                pinkFailList.addAll(memberList);
                applyList.addAll(tempApplyList);
            }
        }
        if (CollUtil.isNotEmpty(pinkFailList) && pinkFailList.size() > 0) {
            boolean failUpdate = updateBatchById(pinkFailList, 100);
            if (!failUpdate) throw new CrmebException("批量更新拼团状态，拼团未成功部分，失败");
        }
        if (applyList.size() > 0) {
            boolean task = orderService.refundApplyTask(applyList);
            if (!task) throw new CrmebException("拼团未成功,订单申请退款失败");
        }
        if (CollUtil.isNotEmpty(pinkSuccessList) && pinkSuccessList.size() > 0) {
            boolean successUpdate = updateBatchById(pinkSuccessList, 100);
            if (!successUpdate) throw new CrmebException("批量更新拼团状态，拼团成功部分，失败");
            pinkSuccessList.forEach(i -> {
                StoreOrder storeOrder = storeOrderService.getByOderId(i.getOrderId());
                StoreCombination storeCombination = storeCombinationService.getById(i.getCid());
                User tempUser = userService.getById(i.getUid());
                // 发送微信模板消息
                MyRecord record = new MyRecord();
                record.set("orderNo", storeOrder.getOrderId());
                record.set("proName", storeCombination.getTitle());
                pushMessageOrder(record, tempUser);
//                map.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "恭喜您拼团成功！我们将尽快为您发货。");
//                map.put("keyword1", storeOrder.getOrderId());
//                map.put("keyword2", storeCombination.getTitle());
//                map.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢你的使用！");
//
//                templateMessageService.push(Constants.WE_CHAT_TEMP_KEY_COMBINATION_SUCCESS, map, storeOrder.getUid(), Constants.PAY_TYPE_WE_CHAT_FROM_PUBLIC);
            });
        }
    }

    /**
     * 发送消息通知
     * @param record 参数
     * @param user 拼团用户
     */
    private void pushMessageOrder(MyRecord record, User user) {
        if (user.getUserType().equals(UserConstants.USER_TYPE_H5)) {
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号
        if (user.getUserType().equals(UserConstants.USER_TYPE_WECHAT)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "恭喜您拼团成功！我们将尽快为您发货。");
            temMap.put("keyword1", record.getStr("orderNo"));
            temMap.put("keyword2", record.getStr("proName"));
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢你的使用！");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_COMBINATION_SUCCESS, temMap, userToken.getToken());
            return;
        }
        // 小程序发送订阅消息
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }
        // 组装数据
        temMap.put("character_string1",  record.getStr("orderNo"));
        temMap.put("thing2", record.getStr("proName"));
        temMap.put("thing5", "恭喜您拼团成功！我们将尽快为您发货。");
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_COMBINATION_SUCCESS, temMap, userToken.getToken());


    }

    /**
     * 拼团成功
     * @param kid
     * @return
     */
    @Override
    public boolean pinkSuccess(Integer kid) {
        if (ObjectUtil.isNull(kid)) {
            return false;
        }
        StorePink teamPink = getById(kid);
        List<StorePink> memberList = getListByCidAndKid(teamPink.getCid(), kid);
        long timeMillis = System.currentTimeMillis();
        memberList.add(teamPink);
        memberList.forEach(i -> {
            i.setStatus(2);
            i.setStopTime(timeMillis);
        });
        return updateBatchById(memberList);
    }

    /**
     * 根据订单编号获取
     * @param orderId
     * @return
     */
    @Override
    public StorePink getByOrderId(String orderId) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StorePink::getOrderId, orderId);
        return dao.selectOne(lqw);
    }

    private Integer getCountByKidAndCid(Integer cid, Integer kid) {
        LambdaQueryWrapper<StorePink> lqw = new LambdaQueryWrapper<>();
        lqw.select(StorePink::getId);
        lqw.eq(StorePink::getCid, cid);
        lqw.and(i -> i.eq(StorePink::getKId, kid).or().eq(StorePink::getId, kid));
        lqw.eq(StorePink::getIsRefund, false);
        return dao.selectCount(lqw);
    }
}

