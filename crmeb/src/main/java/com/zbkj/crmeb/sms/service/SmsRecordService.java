package com.zbkj.crmeb.sms.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.sms.model.SmsRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.sms.request.SmsRecordRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description SmsRecordService 接口
* @since 2020-04-16
*/
public interface SmsRecordService extends IService<SmsRecord> {

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-16
    * @return List<SmsRecord>
    */
    List<SmsRecord> getList(SmsRecordRequest request, PageParamRequest pageParamRequest);

    /**
     * 保存短信记录
     * @param smsRecord 待保存短信记录
     * @return 保存结果
     */
    boolean save(SmsRecord smsRecord);


    // 短信状态同步
    void consumeSmsStatus();

    // 更新短信实际发送状态嘛
    void updateSmsStatus(List<Integer> recordIds);

}
