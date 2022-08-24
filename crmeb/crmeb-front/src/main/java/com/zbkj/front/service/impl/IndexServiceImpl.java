package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.IndexInfoResponse;
import com.zbkj.common.response.IndexProductResponse;
import com.zbkj.common.response.ProductActivityItemResponse;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.constants.SysGroupDataConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.model.record.UserVisitRecord;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.model.system.SystemConfig;
import com.zbkj.common.model.user.User;
import com.zbkj.front.service.IndexService;
import com.zbkj.service.delete.ProductUtils;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* IndexServiceImpl 接口实现
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private ProductUtils productUtils;

    @Autowired
    private UserVisitRecordService userVisitRecordService;

    /**
     * 首页数据
     * banner、金刚区、广告位
     */
    @Override
    public IndexInfoResponse getIndexInfo() {
        IndexInfoResponse indexInfoResponse = new IndexInfoResponse();
        indexInfoResponse.setBanner(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_BANNER)); //首页banner滚动图
        indexInfoResponse.setMenus(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_MENU)); //导航模块
        indexInfoResponse.setRoll(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_NEWS_BANNER)); //首页滚动新闻

        indexInfoResponse.setLogoUrl(systemConfigService.getValueByKey(Constants.CONFIG_KEY_SITE_LOGO));// 企业logo地址
        indexInfoResponse.setYzfUrl(systemConfigService.getValueByKey(Constants.CONFIG_KEY_YZF_H5_URL));// 云智服H5 url
        indexInfoResponse.setConsumerHotline(systemConfigService.getValueByKey(Constants.CONFIG_KEY_CONSUMER_HOTLINE));// 客服电话
        indexInfoResponse.setTelephoneServiceSwitch(systemConfigService.getValueByKey(Constants.CONFIG_KEY_TELEPHONE_SERVICE_SWITCH));// 客服电话服务
        indexInfoResponse.setCategoryPageConfig(systemConfigService.getValueByKey(Constants.CONFIG_CATEGORY_CONFIG));// 商品分类页配置
        indexInfoResponse.setIsShowCategory(systemConfigService.getValueByKey(Constants.CONFIG_IS_SHOW_CATEGORY));// 是否隐藏一级分类
        indexInfoResponse.setExplosiveMoney(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_EX_BANNER));//首页超值爆款
        indexInfoResponse.setHomePageSaleListStyle(systemConfigService.getValueByKey(Constants.CONFIG_IS_PRODUCT_LIST_STYLE));// 首页商品列表模板配置
        indexInfoResponse.setSubscribe(false);
        User user = userService.getInfo();
        if(ObjectUtil.isNotNull(user) && user.getSubscribe()) {
            indexInfoResponse.setSubscribe(user.getSubscribe());
        }

        // 保存用户访问记录
        UserVisitRecord visitRecord = new UserVisitRecord();
        visitRecord.setDate(DateUtil.date().toString("yyyy-MM-dd"));
        visitRecord.setUid(userService.getUserId());
        visitRecord.setVisitType(1);
        userVisitRecordService.save(visitRecord);
        return indexInfoResponse;
    }

    /**
     * 热门搜索
     * @return List<HashMap<String, String>>
     */
    @Override
    public List<HashMap<String, Object>> hotKeywords() {
        return systemGroupDataService.getListMapByGid(SysGroupDataConstants.GROUP_DATA_ID_INDEX_KEYWORDS);
    }

    /**
     * 微信分享配置
     * @return Object
     */
    @Override
    public HashMap<String, String> getShareConfig() {
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> info = systemConfigService.info(Constants.CONFIG_FORM_ID_PUBLIC);
        if(info == null) {
            throw new CrmebException("请配置公众号分享信息！");
        }
        map.put("img", info.get(SysConfigConstants.CONFIG_KEY_ADMIN_WECHAT_SHARE_IMAGE));
        map.put("title", info.get(SysConfigConstants.CONFIG_KEY_ADMIN_WECHAT_SHARE_TITLE));
        map.put("synopsis", info.get(SysConfigConstants.CONFIG_KEY_ADMIN_WECHAT_SHARE_SYNOSIS));
        return map;
    }

    /**
     * 获取首页商品列表
     * @param type 类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public CommonPage<IndexProductResponse> findIndexProductList(Integer type, PageParamRequest pageParamRequest) {
        if (type < Constants.INDEX_RECOMMEND_BANNER || type > Constants.INDEX_BENEFIT_BANNER) {
            return CommonPage.restPage(new ArrayList<>());
        }
        List<StoreProduct> storeProductList = storeProductService.getIndexProduct(type, pageParamRequest);
        if(CollUtil.isEmpty(storeProductList)) {
            return CommonPage.restPage(new ArrayList<>());
        }
        CommonPage<StoreProduct> storeProductCommonPage = CommonPage.restPage(storeProductList);

        List<IndexProductResponse> productResponseArrayList = new ArrayList<>();
        for (StoreProduct storeProduct : storeProductList) {
            IndexProductResponse productResponse = new IndexProductResponse();
            List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());
            // 活动类型默认：直接跳过
            if (activityList.get(0).equals(Constants.PRODUCT_TYPE_NORMAL)) {
                BeanUtils.copyProperties(storeProduct, productResponse);
                productResponseArrayList.add(productResponse);
                continue;
            }
            // 根据参与活动添加对应商品活动标示
            HashMap<Integer, ProductActivityItemResponse> activityByProduct =
                    productUtils.getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
            if (CollUtil.isNotEmpty(activityByProduct)) {
                for (Integer activity : activityList) {
                    if (activity.equals(Constants.PRODUCT_TYPE_NORMAL)) {
                        break;
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_SECKILL)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_SECKILL);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_BARGAIN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_BARGAIN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_PINGTUAN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_PINGTUAN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                }
            }
            BeanUtils.copyProperties(storeProduct, productResponse);
            productResponseArrayList.add(productResponse);
        }
        CommonPage<IndexProductResponse> productResponseCommonPage = CommonPage.restPage(productResponseArrayList);
        BeanUtils.copyProperties(storeProductCommonPage, productResponseCommonPage, "list");
        return productResponseCommonPage;
    }

    /**
     * 获取颜色配置
     * @return SystemConfig
     */
    @Override
    public SystemConfig getColorConfig() {
        return systemConfigService.getColorConfig();
    }

    /**
     * 获取版本信息
     * @return MyRecord
     */
    @Override
    public MyRecord getVersion() {
        MyRecord record = new MyRecord();
        // app版本号
        record.set("appVersion", systemConfigService.getValueByKey(Constants.CONFIG_APP_VERSION));
        record.set("androidAddress", systemConfigService.getValueByKey(Constants.CONFIG_APP_ANDROID_ADDRESS));
        record.set("iosAddress", systemConfigService.getValueByKey(Constants.CONFIG_APP_IOS_ADDRESS));
        record.set("openUpgrade", systemConfigService.getValueByKey(Constants.CONFIG_APP_OPEN_UPGRADE));
        return record;
    }

    /**
     * 获取全局本地图片域名
     * @return String
     */
    @Override
    public String getImageDomain() {
        String localUploadUrl = systemConfigService.getValueByKey("localUploadUrl");
        return StrUtil.isBlank(localUploadUrl) ? "" : localUploadUrl;
    }
}

