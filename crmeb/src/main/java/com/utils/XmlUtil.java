package com.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  XML 工具类
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
public class XmlUtil {
    public static Map<String,String> xmlToMap(HttpServletRequest request)
    {
        Map<String,String> map = new HashMap<>();
        SAXReader reader = new SAXReader();

        InputStream in = null;
        try {
            in = request.getInputStream();
            Document doc = reader.read(in);
            Element root = doc.getRootElement();
            List<Element> list = root.elements();
            for (Element element : list) {
                map.put(element.getName(), element.getText());
            }
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        } finally{
            try {
                assert in != null;
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 将发送消息封装成对应的xml格式
     */
    public static HashMap<String,Object> xmlToMap(String strxml)throws Exception{
        strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

        HashMap<String,Object> map = new HashMap<>();
        SAXReader reader = new SAXReader();
        InputStream inputStream = new ByteArrayInputStream(strxml.getBytes(StandardCharsets.UTF_8));

        if(StringUtils.isBlank(strxml)) {
            return null;
        }

        Document document = reader.read(inputStream);
        Element root = document.getRootElement();
        List<Element> list = root.elements();

        for (Element e : list){
            map.put(e.getName(),e.getText());
        }
        inputStream.close();

        return map;
    }

    /**
     * 将发送消息封装成对应的xml格式
     */
    public static String objectToXml(Object object) {
        XStream xstream  = new XStream(new Xpp3Driver(new NoNameCoder())); //不需要转义
        xstream.alias("xml", object.getClass());
        return xstream.toXML(object);
    }


}
