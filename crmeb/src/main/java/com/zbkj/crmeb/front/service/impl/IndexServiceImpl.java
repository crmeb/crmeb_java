package com.zbkj.crmeb.front.service.impl;

import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.front.response.IndexInfoItemResponse;
import com.zbkj.crmeb.front.response.IndexInfoResponse;
import com.zbkj.crmeb.front.response.IndexProductBannerResponse;
import com.zbkj.crmeb.front.service.IndexService;
import com.zbkj.crmeb.front.service.ProductService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
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
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
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
    private ProductService productService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private UserService userService;

    /**
     * 首页产品的轮播图和产品信息
     * @param type integer 类型
     * @author Mr.Zhang
     * @since 2020-06-02
     * @return HashMap<String, Object>
     */
    @Override
    public IndexProductBannerResponse getProductBanner(int type, PageParamRequest pageParamRequest) {

        IndexProductBannerResponse indexProductBannerResponse = new IndexProductBannerResponse();
        IndexStoreProductSearchRequest request = new IndexStoreProductSearchRequest();
        int gid;
        String key;
        switch (type){
            case Constants.INDEX_RECOMMEND_BANNER: //精品推荐
                gid = Constants.GROUP_DATA_ID_INDEX_RECOMMEND_BANNER;
                key = Constants.INDEX_BAST_LIMIT;
                request.setIsBest(true);
                break;
            case Constants.INDEX_HOT_BANNER: //热门榜单
                gid = Constants.GROUP_DATA_ID_INDEX_HOT_BANNER;
                key = Constants.INDEX_HOT_LIMIT;
                request.setIsHot(true);
                break;
            case Constants.INDEX_NEW_BANNER: //首发新品
                gid = Constants.GROUP_DATA_ID_INDEX_NEW_BANNER;
                key = Constants.INDEX_FIRST_LIMIT;
                request.setIsNew(true);
                break;
            case Constants.INDEX_BENEFIT_BANNER: //促销单品
                gid = Constants.GROUP_DATA_ID_INDEX_BENEFIT_BANNER;
                key = Constants.INDEX_SALES_LIMIT;
                request.setIsBenefit(true);
                break;
            default:
                return null;
        }

        if(StringUtils.isNotBlank(key)){
            String num = systemConfigService.getValueByKey(Constants.INDEX_BAST_LIMIT);
            if(pageParamRequest.getLimit() == 0){
                //首页limit传0，则读取默认数据， 否则后台设置的首页配置不起作用
                pageParamRequest.setLimit(Integer.parseInt(num));
            }
        }

        indexProductBannerResponse.setBanner(systemGroupDataService.getListMapByGid(gid));
        indexProductBannerResponse.setList(productService.getIndexProduct(request, pageParamRequest).getList());
        return indexProductBannerResponse;
    }

    /**
     * 首页数据
     * @author Mr.Zhang
     * @since 2020-06-02
     * @return HashMap<String, Object>
     */
    @Override
    public IndexInfoResponse getIndexInfo() {
        IndexInfoResponse indexInfoResponse =  new IndexInfoResponse();
        indexInfoResponse.setBanner(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_BANNER)); //首页banner滚动图
        indexInfoResponse.setMenus(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_MENU)); //导航模块
        indexInfoResponse.setRoll(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_NEWS_BANNER)); //首页滚动新闻
        indexInfoResponse.setActivity(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_ACTIVITY_BANNER)); //首页活动区域图片
        indexInfoResponse.setInfo(null); //活动
        indexInfoResponse.setActivity(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_ACTIVITY_BANNER)); //首页活动区域图片

        //首页配置
        HashMap<String, String> info = systemConfigService.info(Constants.CONFIG_FORM_ID_INDEX);
        IndexInfoItemResponse indexInfoItemResponse = CrmebUtil.mapStringToObj(info, IndexInfoItemResponse.class);


        int limit = Constants.INDEX_LIMIT_DEFAULT;

        IndexStoreProductSearchRequest request = new IndexStoreProductSearchRequest();
        request.setIsBest(true);
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(limit);

        if(!StringUtils.isBlank(indexInfoItemResponse.getBastNumber())){
            pageParamRequest.setLimit(Integer.parseInt(indexInfoItemResponse.getBastNumber()));
        }
        indexInfoItemResponse.setBastList(productService.getIndexProduct(request, pageParamRequest).getList()); //精品推荐个数

        request.setIsBest(false);
        request.setIsNew(true);
        if(!StringUtils.isBlank(indexInfoItemResponse.getFirstNumber())){
            pageParamRequest.setLimit(Integer.parseInt(indexInfoItemResponse.getFirstNumber()));
        }
        indexInfoItemResponse.setFirstList(productService.getIndexProduct(request, pageParamRequest).getList()); //首发新品个数

        //首页展示的二级分类  排序默认降序
        indexInfoItemResponse.setFastList(null);


        indexInfoResponse.setInfo(indexInfoItemResponse); //首页配置

        indexInfoItemResponse.setBastBanner(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_BEST_BANNER)); //首页精品推荐图片

        request.setIsNew(false);
        request.setIsBenefit(true);
        if(!StringUtils.isBlank(indexInfoItemResponse.getPromotionNumber())){
            pageParamRequest.setLimit(Integer.parseInt(indexInfoItemResponse.getPromotionNumber()));
        }
        indexInfoResponse.setBenefit(new ArrayList<>()); //首页促销单品

        if(productService.getIndexProduct(request, pageParamRequest) != null){
            indexInfoResponse.setBenefit(productService.getIndexProduct(request, pageParamRequest).getList()); //首页促销单品
        }

        request.setIsBenefit(false);
        request.setIsHot(true);
        pageParamRequest.setLimit(limit);
        indexInfoResponse.setLikeInfo(productService.getIndexProduct(request, pageParamRequest).getList()); //热门榜单

        indexInfoResponse.setLovely(null);//首发新品广告图
        List<HashMap<String, Object>> lovelyList = systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_NEW_BANNER);
        if(lovelyList != null && lovelyList.size() > 0){
            indexInfoResponse.setLovely(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_NEW_BANNER).get(0));//首发新品广告图
        }
        indexInfoResponse.setExplosiveMoney(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_EX_BANNER));//首页超值爆款

        indexInfoResponse.setLogoUrl(systemConfigService.getValueByKey(Constants.CONFIG_KEY_SITE_LOGO));

        User user = userService.getInfo();
        if(null != user){
            indexInfoResponse.setSubscribe(user.getSubscribe());
        }
        return indexInfoResponse;
    }


    /**
     * 热门搜索
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return List<HashMap<String, String>>
     */
    @Override
    public List<HashMap<String, Object>> hotKeywords() {

        return systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_KEYWORDS);
    }

    /**
     * 微信分享配置
     * @author Mr.Zhang
     * @since 2020-05-25
     * @return Object
     */
    @Override
    public HashMap<String, String> getShareConfig() {
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> info = systemConfigService.info(Constants.CONFIG_FORM_ID_PUBLIC);
        if(info == null){
            throw new CrmebException("请配置公众号分享信息！");
        }

        map.put("img", info.get("wechat_share_img"));
        map.put("title", info.get("wechat_share_title"));
        map.put("synopsis", info.get("wechat_share_synopsis"));
        return map;
    }

    /**
     * 获取公共配置
     *
     * @return 公共配置
     */
    @Override
    public HashMap<String, String> getCommConfig() {
        HashMap<String,String> result = new HashMap<>();
        result.put("yzfUrl", systemConfigService.getValueByKey(Constants.CONFIG_KEY_YZF_H5_URL));
        return result;
    }
}

