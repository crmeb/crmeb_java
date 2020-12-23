package com.zbkj.crmeb.store.utilService;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.UrlUtil;
import com.zbkj.crmeb.bargain.model.StoreBargain;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.request.StoreCombinationRequest;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.front.response.ProductActivityItemResponse;
import com.zbkj.crmeb.seckill.model.StoreSeckill;
import com.zbkj.crmeb.seckill.request.StoreSeckillRequest;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.model.StoreProductCoupon;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import com.zbkj.crmeb.store.request.StoreProductRequest;
import com.zbkj.crmeb.store.service.StoreProductCouponService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品工具类
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
public class ProductUtils {
    private String baseUrl;

    String rightUrl;

    @Autowired
    private StoreProductCouponService storeProductCouponService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StoreSeckillService storeSeckillService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreCombinationService storeCombinationService;


    /**
     * 设置优惠券信息
     * @param storeProductRequest 商品request信息
     * @param storeProduct 商品信息
     */
    public void shipProductCoupons(StoreProductRequest storeProductRequest, StoreProduct storeProduct) {
        if(null != storeProductRequest.getCouponIds() && storeProductRequest.getCouponIds().size() > 0){
            storeProductCouponService.deleteByProductId(storeProduct.getId());
            List<StoreProductCoupon> spcs = new ArrayList<>();
            for (Integer couponId : storeProductRequest.getCouponIds()) {
                StoreProductCoupon spc = new StoreProductCoupon(storeProduct.getId(), couponId, DateUtil.getNowTime());
                spcs.add(spc);
            }
            storeProductCouponService.saveCoupons(spcs);
        }
    }

    /**
     * 解析淘宝产品数据
     * @param url
     * @param tag
     * @throws IOException
     * @throws JSONException
     */
    public StoreProductRequest getTaobaoProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url, tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.tbJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        StoreProductRequest productRequest = new StoreProductRequest();
        StoreProduct product = new StoreProduct();
        product.setStoreName(item.getString("title"));
        product.setStoreInfo(item.getString("title"));
        product.setSliderImage(item.getString("images"));
        product.setImage(item.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        product.setKeyword(item.getString("title"));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(item.getString("desc"));

        productRequest.setSpecType(true);
        JSONArray props = item.getJSONArray("props");
//        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        if (null == props || props.length() < 1) {
            productRequest.setSpecType(false);
            return productRequest;
        }
        List<StoreProductAttr> spaAttes = new ArrayList<>();
        for (int i = 0; i < props.length(); i++) {
            JSONObject pItem = props.getJSONObject(i);
            StoreProductAttr spattr = new StoreProductAttr();
            spattr.setAttrName(pItem.getString("name"));
            JSONArray values = pItem.getJSONArray("values");
            List<String> attrValues = new ArrayList<>();
            for (int j = 0; j < values.length(); j++) {
                JSONObject value = values.getJSONObject(j);
                attrValues.add(value.getString("name"));
            }
            spattr.setAttrValues(JSON.toJSONString(attrValues));
            spaAttes.add(spattr);
        }
        productRequest.setAttr(spaAttes);
        return productRequest;
    }

    /**
     * 解析京东产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public StoreProductRequest getJDProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url,tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.jdJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        StoreProductRequest productRequest = new StoreProductRequest();
        StoreProduct product = new StoreProduct();
        product.setStoreName(item.getString("name"));
        product.setStoreInfo(item.getString("name"));
        product.setSliderImage(item.getString("images"));
        product.setImage(item.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        product.setPrice(BigDecimal.valueOf(item.getDouble("price")));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(item.getString("desc"));

        JSONObject props = item.getJSONObject("skuProps");
        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        List<StoreProductAttr> spaAttes = new ArrayList<>();
        JSONObject saleJson = item.getJSONObject("saleProp");
        int attrValueIsNullCount = 0;
        Iterator<String> saleProps = saleJson.keys();
        while (saleProps.hasNext()) {
            StoreProductAttr spattr = new StoreProductAttr();
            String stepkey = saleProps.next();
            String stepValue = props.getString(stepkey);
            String stepValueValidLength = stepValue.replace("[","").replace("]","").replace("\"","");
            if(stepValueValidLength.length() > 0){
                com.alibaba.fastjson.JSONArray stepValues = JSON.parseArray(stepValue);
                int c = stepValues.get(0).toString().length();
                attrValueIsNullCount += c == 0 ? 1 : 0;
                spattr.setAttrName(saleJson.getString(stepkey));
                spattr.setAttrValues(props.getString(stepkey));
                spaAttes.add(spattr);
                productRequest.setAttr(spaAttes);
            }else{
                attrValueIsNullCount += 1;
            }
        }
        // 判断是否单属性
        productRequest.setSpecType(spaAttes.size() != attrValueIsNullCount);
        return productRequest;
    }

    /**
     * 解析天猫产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public StoreProductRequest getTmallProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url, tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.tmallJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        StoreProductRequest productRequest = new StoreProductRequest();
        StoreProduct product = new StoreProduct();
        product.setStoreName(item.getString("title"));
        product.setStoreInfo(item.getString("subTitle"));
        product.setSliderImage(item.getString("images"));
        product.setImage(item.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        product.setKeyword(item.getString("title"));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(item.getString("descUrl"));

        productRequest.setSpecType(true);
        JSONArray props = item.getJSONArray("props");
//        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        if (null == props || props.length() < 1) {
            // 无规格商品
            productRequest.setSpecType(false);
            return productRequest;
        }
        List<StoreProductAttr> spaAttes = new ArrayList<>();
        for (int i = 0; i < props.length(); i++) {
            JSONObject pItem = props.getJSONObject(i);
            StoreProductAttr spattr = new StoreProductAttr();
            spattr.setAttrName(pItem.getString("name"));
            JSONArray values = pItem.getJSONArray("values");
            List<String> attrValues = new ArrayList<>();
            for (int j = 0; j < values.length(); j++) {
                JSONObject value = values.getJSONObject(j);
                attrValues.add(value.getString("name"));
            }
            spattr.setAttrValues(JSON.toJSONString(attrValues));
            spaAttes.add(spattr);
        }
        productRequest.setAttr(spaAttes);
        return productRequest;
    }

    /**
     * 解析拼多多产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public StoreProductRequest getPddProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url, tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.pddJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        StoreProductRequest productRequest = new StoreProductRequest();
        StoreProduct product = new StoreProduct();
        product.setStoreName(item.getString("goodsName"));
        product.setStoreInfo(item.getString("goodsDesc"));
        product.setSliderImage(item.getString("thumbUrl"));
        product.setImage(item.getString("banner"));
        product.setVideoLink(item.getJSONArray("video").getJSONObject(0).getString("videoUrl"));
        product.setPrice(BigDecimal.valueOf(item.getDouble("maxNormalPrice")));
        product.setOtPrice(BigDecimal.valueOf(item.getDouble("marketPrice")));
        BeanUtils.copyProperties(product, productRequest);

        JSONArray props = item.getJSONArray("skus");
        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        if (props.length() > 0) {
            List<StoreProductAttr> spaAttes = new ArrayList<>();
            HashMap<String,List<String>> tempAttr = new HashMap<>();
            for (int i = 0; i < props.length(); i++) {
                JSONObject pItem = props.getJSONObject(i);
                JSONArray specArray = pItem.getJSONArray("specs");
                for (int j = 0; j < specArray.length(); j++) {
                    JSONObject specItem = specArray.getJSONObject(j);
                    String keyTemp = specItem.getString("spec_key");
                    String valueTemp = specItem.getString("spec_value");
                    if(tempAttr.containsKey(keyTemp)){
                        if(!tempAttr.get(keyTemp).contains(valueTemp)){
                            tempAttr.get(keyTemp).add(valueTemp);
                        }
                    }else{
                        List<String> tempList = new ArrayList<>();
                        tempList.add(valueTemp);
                        tempAttr.put(keyTemp, tempList);
                    }
                }

            }
            Iterator iterator = tempAttr.keySet().iterator();
            while (iterator.hasNext()){
                String key = (String)iterator.next();
                StoreProductAttr spattr = new StoreProductAttr();
                spattr.setAttrName(key);
                spattr.setAttrValues(tempAttr.get(key).toString());
                spaAttes.add(spattr);
            }
            productRequest.setAttr(spaAttes);
        }
        return productRequest;
    }

    /** TODO 苏宁返回的数据不一致，暂放
     * 解析苏宁产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public StoreProductRequest getSuningProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url,tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
        System.out.println("tbJsonData:"+tbJsonData);
//        JSONObject tbJsonData = new JSONObject(JSONExample.snJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");

        StoreProductRequest productRequest = new StoreProductRequest();
        StoreProduct product = new StoreProduct();
        product.setStoreName(data.getString("title"));
        product.setStoreInfo(data.getString("title"));
        product.setSliderImage(data.getString("images"));
        product.setImage(data.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        Long priceS = data.getLong("price");
        product.setPrice(BigDecimal.valueOf(priceS));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(data.getString("desc"));

        List<StoreProductAttr> spaAttes = new ArrayList<>();
        StoreProductAttr spattr = new StoreProductAttr();
        spattr.setAttrName("默认");
        List<String> attrValues = new ArrayList<>();
        attrValues.add("默认");
        spattr.setAttrValues(attrValues.toString());
        productRequest.setSpecType(false);
        productRequest.setAttr(spaAttes);
//        if(null == data.optJSONArray("passSubList")){
//            return productRequest;
//        }
//        JSONArray props = data.getJSONArray("passSubList");
//        if (null == props){
//            return productRequest;
//        }
//        if (props.length() > 0) {
//            List<StoreProductAttr> spaAttes = new ArrayList<>();
//            for (int i = 0; i < props.length(); i++) {
//                JSONObject pItem = props.getJSONObject(i);
//                Iterator it = pItem.keys();
//                while (it.hasNext()){
//                    String key = (String)it.next();
//                    JSONArray skuItems = pItem.getJSONArray(key);
//                    List<String> attrValues = new ArrayList<>();
//                    StoreProductAttr spattr = new StoreProductAttr();
//                    for (int j = 0; j < skuItems.length(); j++) {
//                        JSONObject skuItem = skuItems.getJSONObject(j);
//                        if(null != skuItem.optString("characterValueDisplayName"))
//                            attrValues.add(skuItem.getString("characterValueDisplayName"));
//                    }
//                    spattr.setAttrName(key);
//                    spattr.setAttrValues(attrValues.toString());
//                    spaAttes.add(spattr);
//                }
//                productRequest.setAttr(spaAttes);
//            }
//        }
        return productRequest;
    }

    /**
     * 设置配置数据
     * @param tag
     */
    public void setConfig(String url, int tag){
        String rightEndUrl = "&itemid=";
        switch (tag){ // 导入平台1=淘宝，2=京东，3=苏宁，4=拼多多， 5=天猫
            case 1:
                baseUrl = systemConfigService.getValueByKey("importProductTB");
                rightEndUrl += UrlUtil.getParamsByKey(url, "id");
                break;
            case 2:
                baseUrl = systemConfigService.getValueByKey("importProductJD");
                rightEndUrl += url.substring(url.lastIndexOf("/")+1).replace(".html","");
                break;
            case 3:
                baseUrl = systemConfigService.getValueByKey("importProductSN");
                int start = url.indexOf(".com/") + 5;
                int end = url.indexOf(".html");
                String sp = url.substring(start,end);
                String[] shopProduct = sp.split("/");
                rightEndUrl += shopProduct[1]+"&shopid="+shopProduct[0];
                break;
            case 4:
                rightEndUrl += UrlUtil.getParamsByKey(url, "goods_id");
                baseUrl = systemConfigService.getValueByKey("importProductPDD");
                break;
            case 5:
                rightEndUrl += UrlUtil.getParamsByKey(url, "id");
                baseUrl = systemConfigService.getValueByKey("importProductTM");
                break;
        }
        String token = systemConfigService.getValueByKey("importProductToken");
        if(StringUtils.isBlank(token)){
            throw new CrmebException("请配置复制产品平台的Token -- www.99api.com");
        }
        if(StringUtils.isBlank(baseUrl)){
            throw new CrmebException("请配置复制产品平台的Url-- www.99api.com");
        }
        rightUrl = "?apikey="+systemConfigService.getValueByKey("importProductToken")+rightEndUrl;

    }

    /**
     * 99api产品复制工具方法
     * @param rd
     * @return
     * @throws IOException
     */
    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * 根据url访问99api后返回对应的平台的产品json数据 带有body参数，暂时无用
     * @param url
     * @param body
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject postRequestFromUrl(String url, String body) throws IOException, JSONException {
        URL realUrl = new URL(url);
        URLConnection conn = realUrl.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(body);
        out.flush();
        InputStream instream = conn.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(instream, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            instream.close();
        }
    }

    /**
     * 根据url访问99api后返回对应的平台的产品json数据
     * @param url
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject getRequestFromUrl(String url) throws IOException, JSONException {
        URL realUrl = new URL(url);
        URLConnection conn = realUrl.openConnection();
        InputStream instream = conn.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(instream, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            instream.close();
        }
    }

    /**
     * 计算产品属性之中最大和最小的价格，新增和编辑使用
     * @param storeProductRequest 分析的参数
     * @param storeProduct 当前操作的产品
     */
    public void calcPriceForAttrValues(StoreProductRequest storeProductRequest, StoreProduct storeProduct) {
        // 设置商品成本价和市场价，2020-8-28日更 商品本身价钱取sku列表中最低的价格
        List<StoreProductAttrValueRequest> attrValuesSortAsc = storeProductRequest.getAttrValue().stream()
                .sorted(Comparator.comparing(StoreProductAttrValueRequest::getPrice))
                .collect(Collectors.toList());
        if(attrValuesSortAsc.size() == 0){
            return;
        }
        storeProduct.setPrice(attrValuesSortAsc.get(0).getPrice());
        storeProduct.setOtPrice(attrValuesSortAsc.get(0).getOtPrice());
        storeProduct.setStock(attrValuesSortAsc.stream().mapToInt(e -> e.getStock()).sum());
        List<StoreProductAttrValueRequest> attrValuesSortAsc1 = storeProductRequest.getAttrValue().stream()
                .sorted(Comparator.comparing(StoreProductAttrValueRequest::getCost))
                .collect(Collectors.toList());
        storeProduct.setCost(attrValuesSortAsc1.get(0).getCost());
    }
    /**
     * 计算产品属性之中最大和最小的价格，新增和编辑使用
     * @param storeProductRequest 分析的参数
     * @param storeProduct 当前操作的产品
     */
    public void calcPriceForAttrValuesSeckill(StoreSeckillRequest storeProductRequest, StoreSeckill storeProduct) {
        // 设置商品成本价和市场价，2020-8-28日更 商品本身价钱取sku列表中最低的价格
        List<StoreProductAttrValueRequest> attrValuesSortAsc = storeProductRequest.getAttrValue().stream()
                .sorted(Comparator.comparing(StoreProductAttrValueRequest::getPrice))
                .collect(Collectors.toList());
        if(attrValuesSortAsc.size() == 0){
            return;
        }
        storeProduct.setPrice(attrValuesSortAsc.get(0).getPrice());
        storeProduct.setOtPrice(attrValuesSortAsc.get(0).getOtPrice());
        storeProduct.setStock(attrValuesSortAsc.stream().mapToInt(e -> e.getStock()).sum());

        // 计算限购总数
        int skillLimit = storeProductRequest.getAttrValue().stream().mapToInt(e -> e.getQuota()).sum();
        storeProduct.setQuota(skillLimit);
        storeProduct.setQuotaShow(skillLimit);

    }

    /**
     * 计算产品属性之中最大和最小的价格，新增和编辑使用
     * @param storeProductRequest 分析的参数
     * @param storeProduct 当前操作的产品
     */
    public void calcPriceForAttrValuesCombination(StoreCombinationRequest storeProductRequest, StoreCombination storeProduct) {
        // 设置商品成本价和市场价
        List<StoreProductAttrValueRequest> attrValuesSortAsc = storeProductRequest.getAttrValue().stream()
                .sorted(Comparator.comparing(StoreProductAttrValueRequest::getPrice))
                .collect(Collectors.toList());
        if(attrValuesSortAsc.size() == 0){
            return;
        }
        storeProduct.setPrice(attrValuesSortAsc.get(0).getPrice());
        storeProduct.setOtPrice(attrValuesSortAsc.get(0).getOtPrice());
        storeProduct.setCost(attrValuesSortAsc.get(0).getOtPrice());
        storeProduct.setStock(attrValuesSortAsc.stream().mapToInt(StoreProductAttrValueRequest::getStock).sum());

        // 计算限购总数
        int skillLimit = storeProductRequest.getAttrValue().stream().mapToInt(StoreProductAttrValueRequest::getQuota).sum();
        storeProduct.setQuota(skillLimit);
        storeProduct.setQuotaShow(skillLimit);

    }

    /**
     * 根据商品参加的活动次序查找对应活动明细
     * @param productId 商品id
     * @param activity 活动次序
     * @return 活动结果
     */
    public HashMap<Integer,ProductActivityItemResponse> getActivityByProduct(Integer productId, String activity){
        HashMap<Integer,ProductActivityItemResponse> result = null;
        // 根据参与活动配置次序查找对应活动信息
        if(StringUtils.isBlank(activity)){
            return result;
        }
        result = new HashMap<>();

        List<Integer> activitys = CrmebUtil.stringToArrayInt(activity);
        for (Integer code : activitys) {
            if(code == 0){

            }
            if(code == 1){ // 查找秒杀信息
                List<StoreSeckill> currentSecKills = storeSeckillService.getCurrentSecKillByProductId(productId);
                if(null != currentSecKills && currentSecKills.size() > 0){
                    ProductActivityItemResponse seckillResponse = new ProductActivityItemResponse();
                    seckillResponse.setId(currentSecKills.get(0).getId());
                    seckillResponse.setTime(DateUtil.getSecondTimestamp(currentSecKills.get(0).getStopTime()));
                    seckillResponse.setType(Constants.PRODUCT_TYPE_SECKILL+"");
                    result.put(code,seckillResponse);
                }
            }
            if(code == 2){ // 查找砍价信息
                List<StoreBargain> currentBargains = storeBargainService.getCurrentBargainByProductId(productId);
                if (CollUtil.isNotEmpty(currentBargains)) {
                    ProductActivityItemResponse bargainResponse = new ProductActivityItemResponse();
                    bargainResponse.setId(currentBargains.get(0).getId());
                    bargainResponse.setTime(DateUtil.getSecondTimestamp(currentBargains.get(0).getStopTime()));
                    bargainResponse.setType(Constants.PRODUCT_TYPE_BARGAIN +"");
                    result.put(code, bargainResponse);
                }
            }
            if(code == 3){ // 查找拼团信息
                List<StoreCombination> currentCombinations = storeCombinationService.getCurrentBargainByProductId(productId);
                if (CollUtil.isNotEmpty(currentCombinations)) {
                    ProductActivityItemResponse bargainResponse = new ProductActivityItemResponse();
                    bargainResponse.setId(currentCombinations.get(0).getId());
                    bargainResponse.setTime(DateUtil.getSecondTimestamp(currentCombinations.get(0).getStopTime()));
                    bargainResponse.setType(Constants.PRODUCT_TYPE_PINGTUAN +"");
                    result.put(code, bargainResponse);
                }
            }
        }
        return result;
    }

    /**
     * 获取商品参与的全部活动
     * @param storeProduct  当前商品
     * @return  商品所参与的活动
     */
    public List<ProductActivityItemResponse> getProductAllActivity(StoreProduct storeProduct) {
        HashMap<Integer, ProductActivityItemResponse> currentActivityList
                = getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
        if(StringUtils.isBlank(storeProduct.getActivity())) return new ArrayList<>();
        List<ProductActivityItemResponse> activityH5 = new ArrayList<>();
        List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());
        for (Integer code : activityList) {
            if(null != currentActivityList.get(code)){
                activityH5.add(currentActivityList.get(code));
            }
        }
        return activityH5;
    }

    /**
     * 获取当前商品参与的第一个活动
     * @param storeProduct  当前商品信息
     * @return 当前参与的商品活动
     */
    public ProductActivityItemResponse getProductCurrentActivity(StoreProduct storeProduct) {
        HashMap<Integer, ProductActivityItemResponse> currentActivityList
                = getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
        if(StringUtils.isBlank(storeProduct.getActivity())) return null;
        List<ProductActivityItemResponse> activityH5 = new ArrayList<>();
        List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());

        return currentActivityList.get(activityList.get(0));
    }

    /**
     * 设置商品参与的活动
     * @param storeProductRequest   商品参数
     * @param storeProduct  设置后的商品
     */
    public void setProductActivity(StoreProductRequest storeProductRequest, StoreProduct storeProduct) {
        List<Integer> _activity = new ArrayList<>();
        if(null != storeProductRequest.getActivity() &&  storeProductRequest.getActivity().size() >0){
            storeProductRequest.getActivity().stream().map(e->{
                switch (e) {
                    case Constants.PRODUCT_TYPE_NORMAL_STR:
                        _activity.add(Constants.PRODUCT_TYPE_NORMAL);
                        break;
                    case Constants.PRODUCT_TYPE_SECKILL_STR:
                        _activity.add(Constants.PRODUCT_TYPE_SECKILL);
                        break;
                    case Constants.PRODUCT_TYPE_BARGAIN_STR:
                        _activity.add(Constants.PRODUCT_TYPE_BARGAIN);
                        break;
                    case Constants.PRODUCT_TYPE_PINGTUAN_STR:
                        _activity.add(Constants.PRODUCT_TYPE_PINGTUAN);
                        break;
                }
                return e;
            }).collect(Collectors.toList());
            storeProduct.setActivity(_activity.toString()
                    .replace("[","").replace("]","")
                    .replaceAll("\\\\s*",""));
        }
    }


    /**
     * 解析json字符串 返回对应对象的数据
     * @param storeProductRequest
     * @return
     */
//    private List<StoreProductAttrValueRequest> getStoreProductAttrValueRequests(StoreProductRequest storeProductRequest) {
//        List<StoreProductAttrValueRequest> storeProductAttrValuesRequest = new ArrayList<>();
//        com.alibaba.fastjson.JSONArray attrJSONArray = com.alibaba.fastjson.JSONArray.parseArray(storeProductRequest.getAttrValue());
//        for (int i = 0; i < attrJSONArray.size(); i++) {
//            com.alibaba.fastjson.JSONObject jsonObject = attrJSONArray.getJSONObject(i);
//            StoreProductAttrValueRequest attrValueRequest =
//                    com.alibaba.fastjson.JSONObject.parseObject(String.valueOf(jsonObject), StoreProductAttrValueRequest.class);
//            storeProductAttrValuesRequest.add(attrValueRequest);
//        }
//        return storeProductAttrValuesRequest;
//    }

    /**
     * 产品保存和更新时设置attr和attrValues属性
     * @param storeProductRequest 产品属性
     * @return 设置后的数据对象
     */
    public HashMap<String, Object> setAttrValueByRequest(StoreProductRequest storeProductRequest) {
        HashMap<String, Object> attrValues = new HashMap<>();
        attrValues.put("attr", storeProductRequest.getAttr());
        attrValues.put("attrValue", storeProductRequest.getAttrValue());
        return attrValues;
    }
    //    public static void main(String[] args) throws IOException, JSONException {
//        // 请求示例 url 默认请求参数已经做URL编码
//        // 淘宝API
//        String tbUrl = "https://api03.6bqb.com/taobao/detail?apikey=A5E94A9B7EBEBE9BB305680C0EE23885&itemid=16793826526";
//        // 京东
//        String jdUrl = "https://api03.6bqb.com/jd/detail?apikey=A5E94A9B7EBEBE9BB305680C0EE23885&itemid=10000017776";
//        // 苏宁
//        String snUrl = "https://api03.6bqb.com/suning/detail?apikey=A5E94A9B7EBEBE9BB305680C0EE23885&itemid=10750373914&shopid=0070088010";
//        // 拼多多
//        String pddUrl = "https://api03.6bqb.com/pdd/detail?apikey=A5E94A9B7EBEBE9BB305680C0EE23885&itemid=5914165983";
//        CopyProduct cp = new CopyProduct();
////        JSONObject tbJson = cp.getRequestFromUrl(tbUrl);
////        System.out.println("淘宝产品"+tbJson.toString());
////        JSONObject jdJson = cp.getRequestFromUrl(jdUrl);
////        System.out.println("京东产品"+jdJson.toString());
////        JSONObject snJson = cp.getRequestFromUrl(snUrl);
////        System.out.println("苏宁产品"+snJson.toString());
//        JSONObject pddJson = cp.getRequestFromUrl(pddUrl);
//
//        System.out.println("拼多多产品"+pddJson.toString());
//
//        cp.getTaobaoProductInfo("",0);
//    }

    /**
     * 一号通复制商品转公共商品参数
     * @param jsonObject 一号通复制商品
     *
     */
    public static StoreProductRequest onePassCopyTransition(com.alibaba.fastjson.JSONObject jsonObject) {
        if (null == jsonObject) return null;

        StoreProductRequest productRequest = new StoreProductRequest();
        StoreProduct product = new StoreProduct();

        product.setStoreName(jsonObject.getString("store_name"));
        product.setStoreInfo(jsonObject.getString("store_info"));
        product.setSliderImage(jsonObject.getString("slider_image"));
        product.setImage(jsonObject.getString("image").replace("[", "").replace("\"", ""));
        product.setKeyword(jsonObject.getString("store_name"));
        product.setCost(jsonObject.getBigDecimal("cost"));
        product.setPrice(jsonObject.getBigDecimal("price"));
        product.setOtPrice(jsonObject.getBigDecimal("ot_price"));
        product.setUnitName(jsonObject.getString("unit_name"));
        BeanUtils.copyProperties(product, productRequest);

        productRequest.setContent(jsonObject.getString("description"));
        productRequest.setSpecType(true);

        com.alibaba.fastjson.JSONArray props = jsonObject.getJSONArray("items");
        if (null == props || props.size() < 1) {
            // 无规格商品
            productRequest.setSpecType(false);
            return productRequest;
        }

        List<StoreProductAttr> spaAttes = new ArrayList<>();
        for (int i = 0; i < props.size(); i++) {
            com.alibaba.fastjson.JSONObject pItem = props.getJSONObject(i);
            StoreProductAttr spattr = new StoreProductAttr();
            spattr.setAttrName(pItem.getString("value"));
            com.alibaba.fastjson.JSONArray values = pItem.getJSONArray("detail");
            List<String> attrValues = new ArrayList<>();
            for (int j = 0; j < values.size(); j++) {
                String value = values.getString(j);
                attrValues.add(value);
            }
            spattr.setAttrValues(JSON.toJSONString(attrValues));
            spaAttes.add(spattr);
        }
        productRequest.setAttr(spaAttes);
        return productRequest;
    }

}