package com.zbkj.crmeb.express.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.RedisUtil;
import com.utils.RestTemplateUtil;
import com.zbkj.crmeb.express.service.LogisticService;
import com.zbkj.crmeb.express.vo.LogisticsResultVo;
import com.zbkj.crmeb.system.service.SystemConfigService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
* ExpressServiceImpl 接口实现
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
@Data
@Service
public class LogisticsServiceImpl implements LogisticService {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private RedisUtil redisUtil;

    private String redisKey = Constants.LOGISTICS_KEY;
    private Long redisCacheSeconds = 1800L;

    private String expressNo;


    /** 快递
     * @param expressNo String 物流单号
     * @param type String 快递公司字母简写：不知道可不填 95%能自动识别，填写查询速度会更快 https://market.aliyun.com/products/56928004/cmapi021863.html#sku=yuncode15863000015
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return Express
     */
    @Override
    public LogisticsResultVo info(String expressNo, String type) {
        setExpressNo(expressNo);
        JSONObject result = getCache();
        if(null == result || result.equals("")){
            String appCode = systemConfigService.getValueByKey(Constants.CONFIG_KEY_LOGISTICS_APP_CODE);
            String url = Constants.LOGISTICS_API_URL + "?no=" + expressNo;
            if(StringUtils.isNotBlank(type)){
                url += "&type=" + type;
            }

            HashMap<String, String> header = new HashMap<>();
            header.put("Authorization", "APPCODE " + appCode);

            JSONObject data = restTemplateUtil.getData(url, header);
            checkResult(data);

            //把数据解析成对象返回到前端
            result = data.getJSONObject("result");
            saveCache(result);
        }
        return JSONObject.toJavaObject(result, LogisticsResultVo.class);
    }

    /** 获取快递缓存
     * @author Mr.Zhang
     * @since 2020-07-06
     * @return JSONObject
     */
    private JSONObject getCache() {
        Object data = redisUtil.get(getRedisKey() + getExpressNo());
        if(null != data){
         return JSONObject.parseObject(data.toString());
        }else return null;
    }

    /** 获取快递缓存
     * @param data JSONObject 需要保存的数据
     * @author Mr.Zhang
     * @since 2020-07-06
     */
    private void saveCache(JSONObject data) {
        redisUtil.set(getRedisKey() + getExpressNo(), data.toJSONString(), getRedisCacheSeconds(), TimeUnit.SECONDS);
    }

    /** 获取快递缓存
     * @param data JSONObject 检测返回的结果
     * @author Mr.Zhang
     * @since 2020-07-06
     */
    private void checkResult(JSONObject data) {
        if (!data.getString("status").equals("0")){
            throw new CrmebException(data.getString("msg"));
        }
    }
}

