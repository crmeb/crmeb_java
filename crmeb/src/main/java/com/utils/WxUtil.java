package com.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.exception.CrmebException;
import org.apache.commons.codec.binary.Base64;

/**
 *  微信工具类
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
public class WxUtil {

    private static final String WATERMARK = "watermark";
    private static final String APPID = "appid";
    /**
     * 解密数据
     * @return
     * @throws Exception
     */
    public static String decrypt(String appId, String encryptedData, String sessionKey, String iv){
        String result = "";
        try {
            AESUtil aes = new AESUtil();
            byte[] resultByte = aes.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey), Base64.decodeBase64(iv));
            if(null != resultByte && resultByte.length > 0){
                result = new String(WxPKCS7Encoder.decode(resultByte));
                JSONObject jsonObject = JSONObject.parseObject(result);
                String decryptAppid = jsonObject.getJSONObject(WATERMARK).getString(APPID);
                if(!appId.equals(decryptAppid)){
                    result = "";
                }
            }
        } catch (Exception e) {
            result = "";
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 检测结构请求返回的数据
     * @param result 接口返回的结果
     * @return JSONObject
     */
    public static JSONObject checkResult(JSONObject result){
        if(ObjectUtil.isNull(result)){
            throw new CrmebException("微信平台接口异常，没任何数据返回！");
        }
        if(result.containsKey("errcode") && result.getString("errcode").equals("0")){
            return result;
        }
        if(result.containsKey("errmsg")){
            throw new CrmebException("微信接口调用失败：" + result.getString("errcode") + result.getString("errmsg"));
        }
        return result;
    }

    public static void main(String[] args) throws Exception{
//        String appId = "wx4f4bc4dec97d474b";
        String appId = "wxcda5a2b1b98b6a95";
//        String encryptedData = "CiyLU1Aw2KjvrjMdj8YKliAjtP4gsMZMQmRzooG2xrDcvSnxIMXFufNstNGTyaGS9uT5geRa0W4oTOb1WT7fJlAC+oNPdbB+3hVbJSRgv+4lGOETKUQz6OYStslQ142dNCuabNPGBzlooOmB231qMM85d2/fV6ChevvXvQP8Hkue1poOFtnEtpyxVLW1zAo6/1Xx1COxFvrc2d7UL/lmHInNlxuacJXwu0fjpXfz/YqYzBIBzD6WUfTIF9GRHpOn/Hz7saL8xz+W//FRAUid1OksQaQx4CMs8LOddcQhULW4ucetDf96JcR3g0gfRK4PC7E/r7Z6xNrXd2UIeorGj5Ef7b1pJAYB6Y5anaHqZ9J6nKEBvB4DnNLIVWSgARns/8wR2SiRS7MNACwTyrGvt9ts8p12PKFdlqYTopNHR1Vf7XjfhQlVsAJdNiKdYmYVoKlaRv85IfVunYzO0IKXsyl7JCUjCpoG20f0a04COwfneQAGGwd5oa+T8yO5hzuyDb/XcxxmK01EpqOyuxINew==";
        String encryptedData = "dDhlMskxNhvKiicMe6Bufq+4nwcxFj+q4+RCis3BvWwvWNGZO+mk/taMBSu3eE54UCh1mw69PtzKi7Ug7K/4SQfpheHhVXrUdsmD05zrgj4G9/9FBgde/q6Jp92jf3+r2aKHtN5M2EIftev5r7aK3SHNynOTX7n19Ue8vRszbHcJ5Hmsscuy90WoEF6GTL7Pjt9CoN4OW1iiVLNZRtqgtg==";
//        String sessionKey = "tiihtNczf5v6AKRyjwEUhQ==";
        String sessionKey = "tiihtNczf5v6AKRyjwEUhQ==";
//        String iv = "r7BXXKkLb8qrSNn05n0qiA==";
        String iv = "1dK4U6ZoPIVz/lYR2cNtYQ==";
        System.out.println(decrypt(appId, encryptedData, sessionKey, iv));
    }

}
