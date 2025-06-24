package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.model.acticitystyle.ActivityStyle;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.ActivityStyleSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.ActivityStyleResponse;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.dao.ActivityStyleDao;
import com.zbkj.service.service.ActivityStyleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 活动样式
 *
 * @author dazongzi
 * @description ActivityStyleServiceImpl 接口实现
 * @date 2023-01-05
 */
@Service
public class ActivityStyleServiceImpl extends ServiceImpl<ActivityStyleDao, ActivityStyle> implements ActivityStyleService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityStyleService.class);

    @Resource
    private ActivityStyleDao dao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CrmebConfig crmebConfig;

    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<ActivityStyle>
     * @author dazongzi
     * @since 2023-01-05
     */
    @Override
    public PageInfo<ActivityStyleResponse> getList(ActivityStyleSearchRequest request, PageParamRequest pageParamRequest) {
        Page<ActivityStyle> stylePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        Date currentDate = CrmebDateUtil.nowDateTime();

        //带 ActivityStyle 类的多条件查询
        LambdaQueryWrapper<ActivityStyle> lambdaQueryWrapper = Wrappers.lambdaQuery();
        // type 类型不能为空 bean中已经限制

        lambdaQueryWrapper.eq(ActivityStyle::getType, request.getType());

        if (ObjectUtil.isNotEmpty(request.getId()) && request.getId() > 0) {
            lambdaQueryWrapper.eq(ActivityStyle::getId, request.getId());
        }
        if (ObjectUtil.isNotEmpty(request.getName())) {
            lambdaQueryWrapper.like(ActivityStyle::getName, URLUtil.decode(request.getName()));
        }

        if (ObjectUtil.isNotEmpty(request.getStatus())) {
            lambdaQueryWrapper.eq(ActivityStyle::getStatus, request.getStatus());
        }
        if (ObjectUtil.isNotEmpty(request.getMethod())) {
            lambdaQueryWrapper.eq(ActivityStyle::getMethod, request.getMethod());
        }

        // 时间区间
        if (ObjectUtil.isNotEmpty(request.getStarttime()) && ObjectUtil.isNotEmpty(request.getEndtime())) {
//            lambdaQueryWrapper.ge(ActivityStyle::getStarttime, request.getStarttime()).le(ActivityStyle::getEndtime, request.getEndtime());
            lambdaQueryWrapper.between(ActivityStyle::getCreatetime, request.getStarttime(), request.getEndtime());
        }
        // * -1 已结束 结束时间小于当前时间
        // * 1 进行中 结束时间大于当前时间 && 开始时间小于等于当前时间
        // * 0 未开始 开始时间大于当前时间
        if (ObjectUtil.isNotEmpty(request.getRunningStatus())) {
            switch (request.getRunningStatus()) {
                case -1:
                    lambdaQueryWrapper.lt(ActivityStyle::getEndtime, currentDate);
                    break;
                case 0:
                    lambdaQueryWrapper.gt(ActivityStyle::getStarttime, currentDate);
                    break;
                case 1:
                    lambdaQueryWrapper.ge(ActivityStyle::getEndtime, currentDate).le(ActivityStyle::getStarttime, currentDate);
                    break;
            }
        }

        lambdaQueryWrapper.orderByDesc(ActivityStyle::getCreatetime);
        List<ActivityStyle> activityStyles = dao.selectList(lambdaQueryWrapper);
        // 添加活动状态
        List<ActivityStyleResponse> activityStyleResponses = new ArrayList<>();

        for (ActivityStyle activityStyle : activityStyles) {
            ActivityStyleResponse activityStyleResponse = new ActivityStyleResponse();
            BeanUtils.copyProperties(activityStyle, activityStyleResponse);
            activityStyleResponse.setRunningStatus(activityStyle.getStarttime(), activityStyle.getEndtime(), currentDate);
            activityStyleResponses.add(activityStyleResponse);
        }
        return CommonPage.copyPageInfo(stylePage, activityStyleResponses);
    }

    /**
     * 更新
     *
     * @param id     id
     * @param status 活动样式 状态
     * @return 更新状态结果
     */
    @Override
    public boolean updateStatus(Integer id, boolean status) {
        ActivityStyle activityStyle = new ActivityStyle();
        activityStyle.setId(id);
        activityStyle.setStatus(status);
        activityStyle.setUpdatetime(DateUtil.date());
        return updateById(activityStyle);
    }

    /**
     * 根据服务器时间查询正在进行和即将开始的活动边框配置 并缓存在redis中
     * 缓存时常5分钟
     *
     * @param type 0边框 1背景
     * @return 正在进行和未开始的活动边框配置数据
     */
    @Override
    public List<ActivityStyle> getListBeforeBeginTime(boolean type) {
        if (!type && redisUtil.exists(ProductConstants.PRODUCT_ACTIVITY_STYLE_BORDER)) {
            Object o = redisUtil.get(ProductConstants.PRODUCT_ACTIVITY_STYLE_BORDER);
            String s = JSON.toJSONString(o);
            logger.info("已经存在的缓存活动边框:${}", s);
            return JSON.parseArray(s, ActivityStyle.class);
        }
        if (type && redisUtil.exists(ProductConstants.PRODUCT_ACTIVITY_STYLE_BACKGROUND)) {
            Object o = redisUtil.get(ProductConstants.PRODUCT_ACTIVITY_STYLE_BACKGROUND);
            String s = JSON.toJSONString(o);
            logger.info("已经存在的缓存活动背景:${}", s);
            return JSON.parseArray(s, ActivityStyle.class);
        }
        LambdaQueryWrapper<ActivityStyle> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(ActivityStyle::getType, type);
        lambdaQueryWrapper.eq(ActivityStyle::getStatus, Boolean.TRUE);
        lambdaQueryWrapper.le(ActivityStyle::getStarttime, CrmebDateUtil.nowDateTime(DateConstants.DATE_FORMAT));
        lambdaQueryWrapper.ge(ActivityStyle::getEndtime, CrmebDateUtil.nowDateTime(DateConstants.DATE_FORMAT));
        List<ActivityStyle> activityStyles = dao.selectList(lambdaQueryWrapper);
        if (activityStyles.size() == 0) {
            return new ArrayList<>();
        }
        if (!type) {
            redisUtil.set(ProductConstants.PRODUCT_ACTIVITY_STYLE_BORDER, activityStyles,
                    Long.parseLong(crmebConfig.getActivityStyleCachedTime().toString()), TimeUnit.SECONDS);
            logger.info("新增缓存活动边框:${}", JSON.toJSONString(activityStyles));
        } else {
            redisUtil.set(ProductConstants.PRODUCT_ACTIVITY_STYLE_BACKGROUND, activityStyles,
                    Long.parseLong(crmebConfig.getActivityStyleCachedTime().toString()), TimeUnit.SECONDS);
            logger.info("新增缓存活动背景:${}", JSON.toJSONString(activityStyles));
        }

        return activityStyles;
    }

    /**
     * 填补活动边框
     * method 0=商品参与类型 ，1=指定商品参与，2=指定品牌参与，3=指定商品分类参与, 4=指定商户产品
     *
     * @param productList 待添加商品数据
     * @return 添加边框之后的商品数据
     */
    @Override
    public List<StoreProduct> makeActivityBorderStyle(List<StoreProduct> productList) {
        List<ActivityStyle> listBeforeBeginTime = getListBeforeBeginTime(Boolean.FALSE);
        if (ObjectUtil.isNotNull(listBeforeBeginTime) && listBeforeBeginTime.size() > 0) {
            // 根据现有生效配置 权限最大 0=全部商品参与 过滤重复配置 并且根据更新时间倒序，仅仅第一条生效
            List<ActivityStyle> activityStyleAllBeforeCompare = listBeforeBeginTime.stream().filter(activityStyle -> activityStyle.getMethod() == 0).collect(Collectors.toList());
            // 如果是全部生效其他逻辑都会失效
            if (CollUtil.isNotEmpty(activityStyleAllBeforeCompare)) {
                sortByActivityStyleByUpdateTime(activityStyleAllBeforeCompare);
                // 全部商品生效规则下只有最后更新的生效
                ActivityStyle activityStyleCurrentAll = activityStyleAllBeforeCompare.get(0);
                productList.forEach(product -> product.setActivityStyle(activityStyleCurrentAll.getStyle()));
            } else {
                makeActivityBorderStyleNonGlobal(productList, listBeforeBeginTime);
            }
        }
        return productList;
    }

    /**
     * 填补活动边框非全局
     * method 1=指定商品参与，2=指定品牌参与，3=指定商品分类参与, 4=指定商户产品
     *
     * @param productList 待添加商品数据
     * @return 添加边框之后的商品数据
     */
    private void makeActivityBorderStyleNonGlobal(List<StoreProduct> productList, List<ActivityStyle> listBeforeBeginTime) {
        // 过滤指定商品逻辑 1=指定商品参与
        List<ActivityStyle> activityStyleAllBeforeCompare = listBeforeBeginTime.stream().filter(activityStyle -> activityStyle.getMethod() == 1).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(activityStyleAllBeforeCompare)) {
            sortByActivityStyleByUpdateTime(activityStyleAllBeforeCompare);
            for (ActivityStyle activityStyle : activityStyleAllBeforeCompare) {
                if (activityStyle.getProducts().length() == 0) {
                    break;
                }
                String[] activityBorderStyleProducts = StrUtil.split(activityStyle.getProducts(), ",");
                productList.forEach(product -> {
                    for (String activityBorderStyleProduct : activityBorderStyleProducts) {
                        if (activityBorderStyleProduct.equals(product.getId().toString())) {
                            product.setActivityStyle(activityStyle.getStyle());
                        }
                    }
                });
            }
        }
        // 判断是否还有剩余商品没有指定活动边框
        long productSurplusNum = productList.stream().filter(e -> StrUtil.isBlank(e.getActivityStyle())).count();
        if (productSurplusNum <= 0L) {
            return;
        }
        // 根据品牌过滤 2=指定品牌参与 TODO 预留
//        activityStyleAllBeforeCompare = listBeforeBeginTime.stream().filter(activityStyle -> activityStyle.getMethod() == 2).collect(Collectors.toList());
//        if (CollUtil.isNotEmpty(activityStyleAllBeforeCompare)) {
//            sortByActivityStyleByUpdateTime(activityStyleAllBeforeCompare);
//            for (ActivityStyle activityStyle : activityStyleAllBeforeCompare) {
//                if (activityStyle.getProducts().length() == 0) {
//                    break;
//                }
//                String[] activityBorderStyleBands = StrUtil.split(activityStyle.getProducts(), ",");
//                productList.forEach(product -> {
//                    if (StrUtil.isBlank(product.getActivityStyle())) {
//                        for (String activityBorderStyleBand : activityBorderStyleBands) {
//                            if (ObjectUtil.isNotEmpty(product.getBrandId()) && product.getBrandId() > 0 && activityBorderStyleBand.equals(product.getBrandId().toString())) {
//                                product.setActivityStyle(activityStyle.getStyle());
//                            }
//                        }
//                    }
//                });
//            }
//        }
        productSurplusNum = productList.stream().filter(e -> StrUtil.isBlank(e.getActivityStyle())).count();
        if (productSurplusNum <= 0L) {
            return;
        }
        // 根据分类过滤 3=指定商品分类参与
        activityStyleAllBeforeCompare = listBeforeBeginTime.stream().filter(activityStyle -> activityStyle.getMethod() == 3).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(activityStyleAllBeforeCompare)) {
            sortByActivityStyleByUpdateTime(activityStyleAllBeforeCompare);
            for (ActivityStyle activityStyle : activityStyleAllBeforeCompare) {
                if (activityStyle.getProducts().length() == 0) {
                    break;
                }
                String[] platCategoryIds = StrUtil.split(activityStyle.getProducts(), ",");
                productList.forEach(product -> {
                    if (StrUtil.isBlank(product.getActivityStyle())) {
                        for (String pcid : platCategoryIds) {
                            if (ObjectUtil.isNotEmpty(product.getCateId())) {
                                List<Integer> proCateIdList = CrmebUtil.stringToArray(product.getCateId());
                                if (proCateIdList.contains(Integer.valueOf(pcid))) {
                                    product.setActivityStyle(activityStyle.getStyle());
                                }
                            }
                        }
                    }
                });
            }
        }
        productSurplusNum = productList.stream().filter(e -> StrUtil.isBlank(e.getActivityStyle())).count();
        if (productSurplusNum <= 0L) {
            return;
        }
        // 根据山谷过滤 4=包含的商户所有商品参与
        activityStyleAllBeforeCompare = listBeforeBeginTime.stream().filter(activityStyle -> activityStyle.getMethod() == 4).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(activityStyleAllBeforeCompare)) {
            sortByActivityStyleByUpdateTime(activityStyleAllBeforeCompare);
            for (ActivityStyle activityStyle : activityStyleAllBeforeCompare) {
                if (activityStyle.getProducts().length() == 0) {
                    break;
                }
                String[] merIds = StrUtil.split(activityStyle.getProducts(), ",");
                productList.forEach(product -> {
                    if (StrUtil.isBlank(product.getActivityStyle())) {
                        for (String merid : merIds) {
                            if (merid.equals(product.getMerId().toString())) {
                                product.setActivityStyle(activityStyle.getStyle());
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 填补活动背景 用于商品详情
     *
     * @param product 待添加活动背景的商品
     * @return 已经添加活动背景的商品
     */
    @Override
    public String makeActivityBackgroundStyle(StoreProduct product) {
        List<ActivityStyle> listBeforeBeginTime = getListBeforeBeginTime(Boolean.TRUE);
        // 根据现有生效配置 权限最大 0=全部商品参与 过滤重复配置 并且根据更新时间倒序，仅仅第一条生效
        List<ActivityStyle> activityStyleAllBeforeCompare = listBeforeBeginTime.stream().filter(activityStyle -> activityStyle.getMethod() == 0).collect(Collectors.toList());
        sortByActivityStyleByUpdateTime(activityStyleAllBeforeCompare);
        // 如果是全部生效其他逻辑都会失效
        if (activityStyleAllBeforeCompare.size() > 0) {
            // 全部商品生效规则下只有最后更新的生效
            ActivityStyle activityStyleCurrentAll = activityStyleAllBeforeCompare.get(0);
//            product.setActivityStyle(activityStyleCurrentAll.getStyle());
            return activityStyleCurrentAll.getStyle();
        } else {
            // 过滤指定商品逻辑 1=指定商品参与
            activityStyleAllBeforeCompare = listBeforeBeginTime.stream().filter(activityStyle -> activityStyle.getMethod() == 1).collect(Collectors.toList());
            sortByActivityStyleByUpdateTime(activityStyleAllBeforeCompare);
            for (ActivityStyle activityStyle : activityStyleAllBeforeCompare) {
                if (activityStyle.getProducts().length() == 0) {
                    break;
                }
                String[] activityBorderStyleProducts = StrUtil.split(activityStyle.getProducts(), ",");
                for (String activityBorderStyleProduct : activityBorderStyleProducts) {
                    if (activityBorderStyleProduct.equals(product.getId().toString())) {
//                        product.setActivityStyle(activityStyle.getStyle());
                        return activityStyle.getStyle();
                    }
                }
            }
//            // 根据品牌过滤 2=指定品牌参与 TODO 预留
//            activityStyleAllBeforeCompare = listBeforeBeginTime.stream().filter(activityStyle -> activityStyle.getMethod() == 2).collect(Collectors.toList());
//            sortByActivityStyleByUpdateTime(activityStyleAllBeforeCompare);
//            for (ActivityStyle activityStyle : activityStyleAllBeforeCompare) {
//                if (activityStyle.getProducts().length() == 0) {
//                    break;
//                }
//                String[] activityBorderStyleBands = StrUtil.split(activityStyle.getProducts(), ",");
//                for (String activityBorderStyleBand : activityBorderStyleBands) {
//                    if (ObjectUtil.isNotEmpty(product.getBrandId()) && activityBorderStyleBand.equals(product.getBrandId().toString())) {
////                        product.setActivityStyle(activityStyle.getStyle());
//                        return activityStyle.getStyle();
//                    }
//                }
//            }
            // 根据分类过滤 3=指定商品分类参与
            activityStyleAllBeforeCompare = listBeforeBeginTime.stream().filter(activityStyle -> activityStyle.getMethod() == 3).collect(Collectors.toList());
            sortByActivityStyleByUpdateTime(activityStyleAllBeforeCompare);
            for (ActivityStyle activityStyle : activityStyleAllBeforeCompare) {
                if (activityStyle.getProducts().length() == 0) {
                    break;
                }
                String[] platCategoryIds = StrUtil.split(activityStyle.getProducts(), ",");
                for (String pcid : platCategoryIds) {
                    if (pcid.equals(product.getCateId().toString())) {
//                        product.setActivityStyle(activityStyle.getStyle());
                        return activityStyle.getStyle();
                    }
                }
            }
            // 根据山谷过滤 4=包含的商户所有商品参与
            activityStyleAllBeforeCompare = listBeforeBeginTime.stream().filter(activityStyle -> activityStyle.getMethod() == 4).collect(Collectors.toList());
            sortByActivityStyleByUpdateTime(activityStyleAllBeforeCompare);
            for (ActivityStyle activityStyle : activityStyleAllBeforeCompare) {
                if (activityStyle.getProducts().length() == 0) {
                    break;
                }
                String[] merIds = StrUtil.split(activityStyle.getProducts(), ",");
                for (String merid : merIds) {
                    if (merid.equals(product.getMerId().toString())) {
//                        product.setActivityStyle(activityStyle.getStyle());
                        return activityStyle.getStyle();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据更新时间倒序排序活动数据
     *
     * @param toSortActivityStyleByUpdateTime 待排序活动对象集合
     */
    private void sortByActivityStyleByUpdateTime(List<ActivityStyle> toSortActivityStyleByUpdateTime) {
        Collections.sort(toSortActivityStyleByUpdateTime, new Comparator<ActivityStyle>() {
            @Override
            public int compare(ActivityStyle o1, ActivityStyle o2) {
                return CrmebDateUtil.compareDate(
                        CrmebDateUtil.dateToStr(o2.getUpdatetime(), DateConstants.DATE_FORMAT),
                        CrmebDateUtil.dateToStr(o1.getUpdatetime(), DateConstants.DATE_FORMAT), DateConstants.DATE_FORMAT);
            }
        });
    }
}

