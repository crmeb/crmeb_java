package com.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.constants.PayConstants;
import com.exception.CrmebException;
import com.zbkj.crmeb.payment.vo.wechat.CreateOrderRequestVo;
import com.zbkj.crmeb.payment.vo.wechat.WxRefundVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

/**
 * 微信支付工具类
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
public class WxPayUtil {

    /**
     * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
     *
     * @param xmlStr API返回的XML格式数据
     * @return Map类型数据
     * @throws Exception
     */
    public static HashMap<String, Object> processResponseXml(String xmlStr) throws Exception {
        String RETURN_CODE = "return_code";
        String return_code;
        HashMap<String, Object> respData = XmlUtil.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = (String) respData.get(RETURN_CODE);
        } else {
            throw new CrmebException(String.format("No `return_code` in XML: %s", xmlStr));
        }

        if (return_code.equals(Constants.FAIL)) {
            return respData;
        } else if (return_code.equals(Constants.SUCCESS)) {
            return respData;
        } else {
            throw new CrmebException(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
        }
    }

    /**
     * 获取随机字符串，长度要求在32位以内。
     */
    public static String getNonceStr() {
        return DigestUtils.md5Hex(CrmebUtil.getUuid() + CrmebUtil.randomCount(111111, 666666));
    }

    /**
     * 获取sign
     * @param vo      微信公共下单对象
     * @param signKey 微信签名key
     * @return String
     */
    public static String getSign(CreateOrderRequestVo vo, String signKey) {
        // 对象转map
        Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(vo), Map.class);
        // map排序
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(PayConstants.FIELD_SIGN)) {
                continue;
            }
            if (ObjectUtil.isNotNull(map.get(k))) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(map.get(k)).append("&");
        }
        sb.append("key=").append(signKey);
        String sign = SecureUtil.md5(sb.toString()).toUpperCase();
        System.out.println("sign ========== " + sign);
        return sign;
    }

    /**
     * 获取sign
     * @param wxRefundVo  微信退款对象
     * @param signKey 微信签名key
     * @return String
     */
    public static String getSign(WxRefundVo wxRefundVo, String signKey) {
        // 对象转map
        Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(wxRefundVo), Map.class);
        // map排序
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(PayConstants.FIELD_SIGN)) {
                continue;
            }
            if (ObjectUtil.isNotNull(map.get(k))) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(map.get(k)).append("&");
        }
        sb.append("key=").append(signKey);
        String sign = SecureUtil.md5(sb.toString()).toUpperCase();
        System.out.println("sign ========== " + sign);
        return sign;
    }

    /**
     * 获取sign
     * @param map      待签名数据
     * @param signKey 微信签名key
     * @return String
     */
    public static String getSign(Map<String, String> map, String signKey) {
        // map排序
        Set<String> keySet = map.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(PayConstants.FIELD_SIGN)) {
                continue;
            }
            if (StrUtil.isNotBlank(map.get(k)) && map.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(map.get(k).trim()).append("&");
        }
        sb.append("key=").append(signKey);
        String sign = SecureUtil.md5(sb.toString()).toUpperCase();
        System.out.println("sign ========== " + sign);
        return sign;
    }

    /**
     * 获取当前时间戳，单位秒
     * @return  Long
     */
    public static Long getCurrentTimestamp() {
        return System.currentTimeMillis()/1000;
    }

    /**
     * 获取当前时间戳，单位毫秒
     * @return  Long
     */
    public static Long getCurrentTimestampMs() {
        return System.currentTimeMillis();
    }

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            DocumentBuilder documentBuilder = WXPayXmlUtil.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            System.out.println(StrUtil.format("Invalid XML, can not convert to map. Error message: {}. XML content: {}", ex.getMessage(), strXML));
            throw ex;
        }

    }
}
