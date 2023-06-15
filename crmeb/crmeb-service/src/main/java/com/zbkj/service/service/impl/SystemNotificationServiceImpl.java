package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.NotifyConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.sms.SmsTemplate;
import com.zbkj.common.model.system.SystemNotification;
import com.zbkj.common.model.wechat.TemplateMessage;
import com.zbkj.common.request.NotificationInfoRequest;
import com.zbkj.common.request.NotificationSearchRequest;
import com.zbkj.common.request.NotificationUpdateRequest;
import com.zbkj.common.response.NotificationInfoResponse;
import com.zbkj.service.dao.SystemNotificationDao;
import com.zbkj.service.service.SmsTemplateService;
import com.zbkj.service.service.SystemNotificationService;
import com.zbkj.service.service.TemplateMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SystemNotificationServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class SystemNotificationServiceImpl extends ServiceImpl<SystemNotificationDao, SystemNotification> implements SystemNotificationService {

    @Resource
    private SystemNotificationDao dao;

    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private SmsTemplateService smsTemplateService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 系统通知列表
     * @param request 查询对象
     * @return List
     */
    @Override
    public List<SystemNotification> getList(NotificationSearchRequest request) {
        LambdaQueryWrapper<SystemNotification> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(request.getSendType())) {
            lqw.eq(SystemNotification::getSendType, request.getSendType());
        }
        return dao.selectList(lqw);
    }

    /**
     * 公众号模板开关
     * @param id 通知id
     * @return Boolean
     */
    @Override
    public Boolean wechatSwitch(Integer id) {
        SystemNotification systemNotification = getByIdException(id);
        if (systemNotification.getIsWechat().equals(0)) {
            throw new CrmebException("通知没有配置公众号模板");
        }
        LambdaUpdateWrapper<SystemNotification> luw = Wrappers.lambdaUpdate();
        luw.set(SystemNotification::getIsWechat, systemNotification.getIsWechat().equals(1) ? 2 : 1);
        luw.eq(SystemNotification::getId, id);
        return update(luw);
    }

    /**
     * 小程序订阅模板开关
     * @param id 通知id
     * @return Boolean
     */
    @Override
    public Boolean routineSwitch(Integer id) {
        SystemNotification systemNotification = getByIdException(id);
        if (systemNotification.getIsRoutine().equals(0)) {
            throw new CrmebException("通知没有配置小程序订阅模板");
        }
        LambdaUpdateWrapper<SystemNotification> luw = Wrappers.lambdaUpdate();
        luw.set(SystemNotification::getIsRoutine, systemNotification.getIsRoutine().equals(1) ? 2 : 1);
        luw.eq(SystemNotification::getId, id);
        return update(luw);
    }

    /**
     * 发送短信开关
     * @param id 通知id
     * @return Boolean
     */
    @Override
    public Boolean smsSwitch(Integer id) {
        SystemNotification systemNotification = getByIdException(id);
        if (systemNotification.getIsSms().equals(0)) {
            throw new CrmebException("通知没有配置短信");
        }
        LambdaUpdateWrapper<SystemNotification> luw = Wrappers.lambdaUpdate();
        luw.set(SystemNotification::getIsSms, systemNotification.getIsSms().equals(1) ? 2 : 1);
        luw.eq(SystemNotification::getId, id);
        return update(luw);
    }

    /**
     * 通知详情
     * @param request 详情请求参数
     * @return NotificationInfoResponse
     */
    @Override
    public NotificationInfoResponse getDetail(NotificationInfoRequest request) {
        SystemNotification notification = getByIdException(request.getId());
        NotificationInfoResponse response = new NotificationInfoResponse();
        if ("wechat".equals(request.getDetailType())) {
            if (notification.getIsWechat().equals(0)) {
                throw new CrmebException("请先配置公众号模板消息");
            }
            TemplateMessage templateMessage = templateMessageService.info(notification.getWechatId());
            BeanUtils.copyProperties(templateMessage, response);
            response.setStatus(notification.getIsWechat());
        }
        if ("routine".equals(request.getDetailType())) {
            if (notification.getIsRoutine().equals(0)) {
                throw new CrmebException("请先配置小程序订阅消息");
            }
            TemplateMessage templateMessage = templateMessageService.info(notification.getRoutineId());
            BeanUtils.copyProperties(templateMessage, response);
            response.setStatus(notification.getIsRoutine());
        }
        if ("sms".equals(request.getDetailType())) {
            if (notification.getIsSms().equals(0)) {
                throw new CrmebException("请先配置短信模板");
            }
            SmsTemplate smsTemplate = smsTemplateService.getDetail(notification.getSmsId());
            BeanUtils.copyProperties(smsTemplate, response);
            response.setStatus(notification.getIsSms());
        }
        return response;
    }

    /**
     * 根据标识查询信息
     * @param mark 标识
     * @return SystemNotification
     */
    @Override
    public SystemNotification getByMark(String mark) {
        LambdaQueryWrapper<SystemNotification> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemNotification::getMark, mark);
        return dao.selectOne(lqw);
    }

    /**
     * 获取微信相关列表
     * @param type routine-小程序，public-公众号
     */
    @Override
    public List<SystemNotification> getListByWechat(String type) {
        LambdaQueryWrapper<SystemNotification> lqw = Wrappers.lambdaQuery();
        if ("routine".equals(type)) {
            lqw.ne(SystemNotification::getIsRoutine, 0);
        }
        if ("public".equals(type)) {
            lqw.ne(SystemNotification::getIsWechat, 0);
        }
        return dao.selectList(lqw);
    }

    /**
     * 修改通知
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean modify(NotificationUpdateRequest request) {
        if (!"sms".equals(request.getDetailType()) && StrUtil.isEmpty(request.getTempId())) {
            throw new CrmebException("模板id不能为空");
        }
        SystemNotification notification = getByIdException(request.getId());
        if ("wechat".equals(request.getDetailType())) {
            if (notification.getIsWechat().equals(0)) {
                throw new CrmebException("请先为通知配置公众号模板");
            }
            TemplateMessage templateMessage = templateMessageService.info(notification.getWechatId());
            if (templateMessage.getTempId().equals(request.getTempId()) && notification.getIsWechat().equals(request.getStatus())) {
                return true;
            }
            return transactionTemplate.execute(e -> {
                if (!templateMessage.getTempId().equals(request.getTempId())) {
                    templateMessage.setTempId(request.getTempId());
                    templateMessageService.updateById(templateMessage);
                }
                if (!notification.getIsWechat().equals(request.getStatus())) {
                    notification.setIsWechat(request.getStatus());
                    updateById(notification);
                }
                return Boolean.TRUE;
            });
        }
        if ("routine".equals(request.getDetailType())) {
            if (notification.getIsRoutine().equals(0)) {
                throw new CrmebException("请先为通知配置小程序订阅模板");
            }
            TemplateMessage templateMessage = templateMessageService.info(notification.getRoutineId());
            if (templateMessage.getTempId().equals(request.getTempId()) && notification.getIsRoutine().equals(request.getStatus())) {
                return true;
            }
            return transactionTemplate.execute(e -> {
                if (!templateMessage.getTempId().equals(request.getTempId())) {
                    templateMessage.setTempId(request.getTempId());
                    templateMessageService.updateById(templateMessage);
                }
                if (!notification.getIsRoutine().equals(request.getStatus())) {
                    notification.setIsRoutine(request.getStatus());
                    updateById(notification);
                }
                return Boolean.TRUE;
            });
        }
        if ("sms".equals(request.getDetailType()) && !notification.getIsSms().equals(request.getStatus())) {
            notification.setIsSms(request.getStatus());
            return updateById(notification);
        }
        return true;
    }

    /**
     * 获取小程序订阅模板编号(小程序端调用)
     * @param type 场景类型
     * 支付之前：beforePay
     * 		支付成功通知
     * 支付成功：afterPay
     * 		发货、配送、收货
     * 申请退款：refundApply
     * 		退款成功、拒绝退款
     * 充值之前：beforeRecharge
     * 		充值成功通知
     * 创建砍价：createBargain
     * 		砍价成功、失败通知
     * 参与拼团：pink
     * 		拼团成功、失败通知
     * 取消拼团：cancelPink
     * 		退款成功、拒绝退款
     * @return List
     */
    @Override
    public List<TemplateMessage> getMiniTempList(String type) {
        LambdaQueryWrapper<SystemNotification> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemNotification::getIsRoutine, 1);
        switch (type) {
            case "beforePay":// 支付之前
                lqw.eq(SystemNotification::getMark, NotifyConstants.PAY_SUCCESS_MARK);
                break;
            case "afterPay":// 支付成功
                lqw.in(SystemNotification::getMark, NotifyConstants.DELIVER_GOODS_MARK, NotifyConstants.FULFILLMENT_ORDER_MARK, NotifyConstants.RECEIPT_GOODS_MARK);
                break;
            case "refundApply":// 申请退款
                lqw.eq(SystemNotification::getMark, "-1");
                break;
            case "beforeRecharge":// 充值之前
                lqw.eq(SystemNotification::getMark, "-1");
                break;
            case "createBargain":// 创建砍价
                lqw.in(SystemNotification::getMark, NotifyConstants.BARGAINING_SUCCESS_MARK);
                break;
            case "pink":// 参与拼团
                lqw.in(SystemNotification::getMark, NotifyConstants.GROUP_SUCCESS_MARK);
                break;
            case "cancelPink":// cancelPink
                lqw.eq(SystemNotification::getMark, "-1");
                break;
        }
        List<SystemNotification> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CollUtil.newArrayList();
        }
        List<Integer> tidList = list.stream().map(SystemNotification::getRoutineId).collect(Collectors.toList());
        return templateMessageService.getByIdList(tidList);
    }

    private SystemNotification getByIdException(Integer id) {
        SystemNotification notification = getById(id);
        if (ObjectUtil.isNull(notification)) {
            throw new CrmebException("系统通知不存在");
        }
        return notification;
    }
}

