package com.zbkj.crmeb.front.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.constants.SmsConstants;
import com.exception.CrmebException;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 用户工具类
 * +----------------------------------------------------------------------
 *  * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  * +----------------------------------------------------------------------
 *  * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  * +----------------------------------------------------------------------
 *  * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  * +----------------------------------------------------------------------
 *  * | Author: CRMEB Team <admin@crmeb.com>
 *  * +----------------------------------------------------------------------
 */
@Slf4j
@Service
public class userUtil {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;
    /**
     * 检测手机验证码
     * @param phone 手机号
     * @param code 验证码
     */
    public void checkValidateCode(String phone, String code) {
        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + phone);
        if(validateCode == null){
            throw new CrmebException("验证码已过期");
        }

        if(!validateCode.toString().equals(code)){
            throw new CrmebException("验证码错误");
        }

        //删除验证码
        redisUtil.remove(SmsConstants.SMS_VALIDATE_PHONE + phone);
    }

    /**
     * 绑定分销关系
     * @param user User 用户user类
     * @param spreadUid Integer 推广人id
     * @return Boolean
     * 1.判断分销功能是否启用
     * 2.判断分销模式
     * 3.根据不同的分销模式校验
     * 4.指定分销，只有分销员才可以分销，需要spreadUid是推广员才可以绑定
     * 5.满额分销，同上
     * 6.人人分销，可以直接绑定
     */
    public Boolean bindSpread(User user, Integer spreadUid) {
        log.info("绑定推广关系:user:"+ JSON.toJSONString(user));
        log.info("绑定推广关系:spreadUid:"+ spreadUid);
        Boolean checkBingSpread = userService.checkBingSpread(user, spreadUid, "old");
        log.info("确定绑定关系:"+checkBingSpread);
        if (!checkBingSpread) return false;

        Integer oldSprUid = user.getSpreadUid();

        user.setSpreadUid(spreadUid);
        user.setSpreadTime(DateUtil.nowDateTime());

        Boolean execute = transactionTemplate.execute(e -> {
            userService.updateById(user);
            userService.updateSpreadCountByUid(spreadUid, "add");
            if (oldSprUid > 0) {
                userService.updateSpreadCountByUid(oldSprUid, "sub");
            }
            log.info(StrUtil.format("绑定推广人:user={},spreadUid = {}",JSON.toJSONString(user),spreadUid));
            return Boolean.TRUE;
        });
        if (!execute) {
            log.info(StrUtil.format("绑定推广人时出错，userUid = {}, spreadUid = {}", user.getUid(), spreadUid));
        }
        return execute;
    }
}
