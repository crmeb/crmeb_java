package com.zbkj.crmeb.sms.service;

import com.common.MyRecord;
import com.common.PageParamRequest;
import com.zbkj.crmeb.sms.request.SendSmsVo;
import com.zbkj.crmeb.sms.request.SmsApplyTempRequest;
import com.zbkj.crmeb.sms.request.SmsModifySignRequest;

import java.util.HashMap;

/**
 * SmsService 接口
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
public interface SmsService{

    /**
     * 发送短信到短信列表
     */
    Boolean pushCodeToList(String phone, Integer tag,HashMap<String, Object> pram);

    void push(String phone,String tempKey,Integer msgTempId,boolean valid, HashMap<String,Object> mapPram);

    void consume();

    boolean sendCode(SendSmsVo sendSmsVo);

    /**
     * 修改签名
     * @return
     */
    Boolean modifySign(SmsModifySignRequest request);

    /**
     * 短信模板
     * @return
     */
    MyRecord temps(PageParamRequest pageParamRequest);

    /**
     * 申请模板消息
     * @return
     */
    Boolean applyTempMessage(SmsApplyTempRequest request);

    /**
     * 模板申请记录
     * @param type (1=验证码 2=通知 3=推广)
     * @return
     */
    MyRecord applys(Integer type, PageParamRequest pageParamRequest);
}
