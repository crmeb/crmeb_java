package com.zbkj.crmeb.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.SmsConstants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;
import com.utils.RedisUtil;
import com.utils.RestTemplateUtil;
import com.zbkj.crmeb.sms.SmsResultVo;
import com.zbkj.crmeb.sms.model.SmsRecord;
import com.zbkj.crmeb.sms.dao.SmsRecordDao;
import com.zbkj.crmeb.sms.request.SmsRecordRequest;
import com.zbkj.crmeb.sms.service.SmsRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @Description SmsRecordServiceImpl 接口实现
* @since 2020-04-16
*/
@Service
public class SmsRecordServiceImpl extends ServiceImpl<SmsRecordDao, SmsRecord> implements SmsRecordService {

    @Resource
    private SmsRecordDao dao;
    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private RedisUtil redisUtil;

    private static final Logger logger = LoggerFactory.getLogger(SmsRecordServiceImpl.class);
    /**
     * 短信发送记录
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @return
     */
    @Override
    public List<SmsRecord> getList(SmsRecordRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SmsRecord> queryWrapper = new LambdaQueryWrapper<>();
        if(null != request.getResultCode()){
            queryWrapper.eq(SmsRecord::getResultcode, request.getResultCode());
        }
        queryWrapper.orderByDesc(SmsRecord::getId);
        return dao.selectList(queryWrapper);
    }

    /**
     * 短信发送记录保存
     * @param smsRecord 待保存短信记录
     * @return 保存结果
     */
    @Override
    public boolean save(SmsRecord smsRecord) {
        return dao.insert(smsRecord) > 0;
    }

    /**
     *  更新短信记录实际发送状态嘛
     * @param recordIds 待更新短信发送id
     */
    @Override
    public void updateSmsStatus(List<Integer> recordIds) {
        HashMap<String, List<Integer>> pram = new HashMap<>();
        pram.put("record_id", recordIds);
        String result = restTemplateUtil.postJsonData(
                SmsConstants.SMS_API_URL + SmsConstants.SMS_API_SEND_STATUS,
                JSONObject.parseObject(JSONObject.toJSONString(pram)));

        JSONObject joResult;
        try{
            joResult = checkResult(result);
        }catch (Exception e1){
            joResult = JSONObject.parseObject(result);
        }

        int resultCode = joResult.getInteger("status");
        if(resultCode == Constants.HTTPSTATUS_CODE_SUCCESS){
            List<SmsRecord> smsRecords = new ArrayList<>();
            for (Object data : joResult.getJSONArray("data")) {
                JSONObject resultJo = JSONObject.parseObject(data.toString());
                SmsRecord smsRecord = new SmsRecord().setRecordId(resultJo.getInteger("id"))
                        .setResultcode(resultJo.getInteger("resultcode"));
                smsRecords.add(smsRecord);
            }
            updateSendSmsStatus(smsRecords);
        }
    }

    private void updateSendSmsStatus(List<SmsRecord> smsRecords){
        for (SmsRecord smsRecord : smsRecords) {
            LambdaUpdateWrapper<SmsRecord> lup = new LambdaUpdateWrapper<>();
            lup.eq(SmsRecord::getRecordId, smsRecord.getRecordId())
            .set(SmsRecord::getResultcode,smsRecord.getResultcode());
            update(lup);
        }
    }

    // 短信发送状态同步队列消费者
    @Async
    public void consumeSmsStatus() {
        Long size = redisUtil.getListSize(SmsConstants.SMS_SEND_RESULT_KEY);
        logger.info("SmsServiceImpl.consumeSmsStatus | size:" + size);
        if(size > 0){
            for (int i = 0; i < size; i++) {
                //如果10秒钟拿不到一个数据，那么退出循环
                Object data = redisUtil.getRightPop(SmsConstants.SMS_SEND_RESULT_KEY, 10L);
                if(null == data){
                    continue;
                }
                try{
                    List<Integer> recordIds = CrmebUtil.stringToArray(data.toString());
                    updateSmsStatus(recordIds);

                }catch (Exception e){
                    redisUtil.lPush(SmsConstants.SMS_SEND_RESULT_KEY, data);
                }
            }
        }
    }


    /**
     * 检测结构请求返回的数据
     * @param result 接口返回的结果
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return JSONObject
     */
    private JSONObject checkResult(String result){
        if(StringUtils.isBlank(result)){
            throw new CrmebException("短信平台接口异常，没任何数据返回！");
        }

        JSONObject jsonObject = JSONObject.parseObject(result);
        if(SmsConstants.SMS_ERROR_CODE.equals(jsonObject.getInteger("status"))){
            throw new CrmebException("短信平台接口" + jsonObject.getString("msg"));
        }

        return jsonObject;
    }

    private JSONObject checkResult(JSONObject result){
        if(null == result){
            throw new CrmebException("短信平台接口异常，没任何数据返回！");
        }
        if(SmsConstants.SMS_ERROR_CODE.equals(result.getInteger("status"))){
            throw new CrmebException("短信平台接口" + result.getString("msg"));
        }

        return result;
    }
}

