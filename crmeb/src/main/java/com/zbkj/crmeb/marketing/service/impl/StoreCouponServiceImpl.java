package com.zbkj.crmeb.marketing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.marketing.dao.StoreCouponDao;
import com.zbkj.crmeb.marketing.model.StoreCoupon;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.request.StoreCouponRequest;
import com.zbkj.crmeb.marketing.request.StoreCouponSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreCouponFrontResponse;
import com.zbkj.crmeb.marketing.response.StoreCouponInfoResponse;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StoreCouponServiceImpl 接口实现
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
public class StoreCouponServiceImpl extends ServiceImpl<StoreCouponDao, StoreCoupon> implements StoreCouponService {

    @Resource
    private StoreCouponDao dao;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private CategoryService categoryService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-18
    * @return List<StoreCoupon>
    */
    @Override
    public List<StoreCoupon> getList(StoreCouponSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreCoupon 类的多条件查询
        LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(null != request.getIsDel()){
            lambdaQueryWrapper.eq(StoreCoupon::getIsDel, request.getIsDel());
        }

        if(null != request.getType()){
            lambdaQueryWrapper.eq(StoreCoupon::getType, request.getType());
        }

        if(null != request.getStatus()){
            lambdaQueryWrapper.eq(StoreCoupon::getStatus, request.getStatus());
        }

        if(StringUtils.isNotBlank(request.getName())){
            lambdaQueryWrapper.like(StoreCoupon::getName, request.getName());
        }

        if(StringUtils.isNotBlank(request.getPrimaryKey())){
            lambdaQueryWrapper.apply(CrmebUtil.getFindInSetSql("primary_key", request.getPrimaryKey()) + " and use_type = " + request.getUseType());
        }

        lambdaQueryWrapper.orderByDesc(StoreCoupon::getSort).orderByDesc(StoreCoupon::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 保存优惠券表
     * @param request StoreCouponRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @Override
    public boolean create(StoreCouponRequest request) {
        StoreCoupon storeCoupon = new StoreCoupon();
        BeanUtils.copyProperties(request, storeCoupon);
        if(storeCoupon.getIsLimited() && (storeCoupon.getTotal() == null || storeCoupon.getTotal() == 0)){
            throw new CrmebException("请输入数量！");
        }

        if(request.getUseType() > 1 && (StringUtils.isBlank(request.getPrimaryKey()))){
            throw new CrmebException("请选择商品/分类！");
        }

        storeCoupon.setLastTotal(storeCoupon.getTotal());
        if(!request.getIsForever()){
            storeCoupon.setReceiveStartTime(DateUtil.nowDateTime()); //开始时间设置为当前时间
        }else{
            if(storeCoupon.getReceiveStartTime() == null || storeCoupon.getReceiveEndTime() == null){
                throw new CrmebException("请选择领取时间范围！");
            }

            int compareDate = DateUtil.compareDate(DateUtil.dateToStr(storeCoupon.getReceiveStartTime(), Constants.DATE_FORMAT), DateUtil.dateToStr(storeCoupon.getReceiveEndTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
            if(compareDate > -1){
                throw new CrmebException("请选择正确的领取时间范围！");
            }
        }

        //非固定时间, 领取后多少天
        if(!request.getIsFixedTime()){
            if(storeCoupon.getDay() == null || storeCoupon.getDay() == 0){
                throw new CrmebException("请输入天数！");
            }
            storeCoupon.setUseStartTime(null);
            storeCoupon.setUseEndTime(null);
        }
        return save(storeCoupon);
    }

    /**
     * 获取详情
     * @param id Integer id
     * @author Mr.Zhang
     * @since 2020-05-18
     * @return StoreCoupon
     */
    @Override
    public StoreCoupon getInfoException(Integer id) {
        //获取优惠券信息
        StoreCoupon storeCoupon = getById(id);
        checkException(storeCoupon);

        return storeCoupon;
    }

    /**
     * 检测当前优惠券是否正常
     * @param storeCoupon StoreCoupon 优惠券对象`
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @Override
    public void checkException(StoreCoupon storeCoupon) {
        if(storeCoupon == null || storeCoupon.getIsDel() || !storeCoupon.getStatus()){
            throw new CrmebException("优惠券信息不存在或者已失效！");
        }

        //看是否过期
        if(!(storeCoupon.getReceiveEndTime() == null || storeCoupon.getReceiveEndTime().equals(""))){
            //非永久可领取
            String date = DateUtil.nowDateTimeStr();
            int result = DateUtil.compareDate(date, DateUtil.dateToStr(storeCoupon.getReceiveEndTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
            if(result == 1){
                //过期了
                throw new CrmebException("已超过优惠券领取最后期限！");
            }
        }

        //看是否有剩余数量
        if(storeCoupon.getIsLimited()){
            //考虑到并发溢出的问题用大于等于
            if(storeCoupon.getLastTotal() < 1){
                throw new CrmebException("此优惠券已经被领完了！");
            }
        }
    }

    /**
     * 用户是否有可以领取的优惠券
     * @param idList List<Integer> id集合
     * @author Mr.Zhang
     * @since 2020-05-18
     * @return List<StoreCoupon>
     */
    @Override
    public List<StoreCoupon> getReceiveListInId(List<Integer> idList) {
        //带 StoreCoupon 类的多条件查询
        LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //状态
        lambdaQueryWrapper.in(StoreCoupon::getId, idList)
                .eq(StoreCoupon::getIsDel, false)
                .eq(StoreCoupon::getStatus, true);

        //数量不限量或者大于0
        lambdaQueryWrapper.and(i -> i.or().eq(StoreCoupon::getIsLimited, 0).or().lt(StoreCoupon::getLastTotal, 0));

        //可领取时间
        lambdaQueryWrapper.lt(StoreCoupon::getReceiveEndTime, DateUtil.nowDate());

        return dao.selectList(lambdaQueryWrapper);
    }

//    /**
//     * 根据商品id获取可用优惠券列表
//     * @param productId 获取可用优惠券的商品id
//     * @return 商品对应的可用优惠券集合
//     */
//    @Override
//    public List<StoreCoupon> getListByProductCanUse(Integer productId) {
//
//        List<Integer> categoryIdList = storeProductService.getSecondaryCategoryByProductId(productId);
//
//        LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(StoreCoupon::getIsDel, false);
//        lambdaQueryWrapper.eq(StoreCoupon::getType, 1);
//        lambdaQueryWrapper.eq(StoreCoupon::getIsLimited, true);
//        lambdaQueryWrapper.ge(StoreCoupon::getLastTotal, 0);
//        //有商品id  通用券可以领取，商品券可以领取，分类券可以领取
//        lambdaQueryWrapper.apply(getPrimaryKeySql(productId));
//        return dao.selectList(lambdaQueryWrapper);
//    }

    /**
     * 优惠券详情
     * @param id Integer 获取可用优惠券的商品id
     * @return StoreCouponInfoResponse
     */
    @Override
    public StoreCouponInfoResponse info(Integer id) {
        try{
            StoreCoupon storeCoupon = getById(id);
            if(storeCoupon == null || storeCoupon.getIsDel() || !storeCoupon.getStatus()){
                throw new CrmebException("优惠券信息不存在或者已失效！");
            }

            List<StoreProduct> productList = null;
            List<Category> categoryList = null;
            if(StringUtils.isNotBlank(storeCoupon.getPrimaryKey()) && storeCoupon.getUseType() > 1){
                List<Integer> primaryIdList = CrmebUtil.stringToArray(storeCoupon.getPrimaryKey());
                if(storeCoupon.getUseType() == 2){
                    productList = storeProductService.getList(new StoreProductSearchRequest(), new PageParamRequest(), primaryIdList);
                }
                if(storeCoupon.getUseType() == 3){
                    categoryList = categoryService.getByIds(primaryIdList);
                }
            }

            StoreCouponRequest coupon = new StoreCouponRequest();
            BeanUtils.copyProperties(storeCoupon, coupon);
            coupon.setIsForever(false);
            if(null != coupon.getReceiveEndTime()){
                coupon.setIsForever(true);
            }

            return new StoreCouponInfoResponse(coupon, productList, categoryList);
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }
    }

    @Override
    public List<StoreCouponFrontResponse> getListByUser(Integer productId, PageParamRequest pageParamRequest, Integer userId) {
        List<StoreCoupon> list = getListByReceive(productId, pageParamRequest);
        if(list.size() < 1){
            return null;
        }
        //获取用户当前已领取未使用的优惠券
        HashMap<Integer, StoreCouponUser> couponUserMap = null;

        if(userId > 0){
            couponUserMap = storeCouponUserService.getMapByUserId(userId);
        }
        List<StoreCouponFrontResponse> storeCouponFrontResponseArrayList = new ArrayList<>();
        for (StoreCoupon storeCoupon : list) {
            StoreCouponFrontResponse response = new StoreCouponFrontResponse();
            BeanUtils.copyProperties(storeCoupon, response);
            if(null != couponUserMap && couponUserMap.containsKey(storeCoupon.getId())){
                response.setIsUse(true);
            }

            if(response.getReceiveEndTime() == null){
                response.setReceiveStartTime(null);
            }

            // 更改使用时间格式，去掉时分秒
            response.setUseStartTimeStr(DateUtil.dateToStr(response.getUseStartTime(), Constants.DATE_FORMAT_DATE));
            response.setUseEndTimeStr(DateUtil.dateToStr(response.getUseEndTime(), Constants.DATE_FORMAT_DATE));
            storeCouponFrontResponseArrayList.add(response);
        }

        return storeCouponFrontResponseArrayList;
    }


    /**
     * 根据优惠券id获取
     * @param ids 优惠券id集合
     * @return
     */
    @Override
    public List<StoreCoupon> getByIds(List<Integer> ids) {
        LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreCoupon::getId, ids);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 扣减数量
     * @param id 优惠券id
     * @param num 数量
     * @param isLimited 是否限量
     */
    @Override
    public Boolean deduction(Integer id, Integer num, Boolean isLimited) {
        UpdateWrapper<StoreCoupon> updateWrapper = new UpdateWrapper<>();
        if (isLimited) {
            updateWrapper.setSql(StrUtil.format("last_total = last_total - {}", num));
            updateWrapper.last(StrUtil.format(" and (last_total - {} >= 0)", num));
        } else {
            updateWrapper.setSql(StrUtil.format("last_total = last_total + {}", num));
        }
        updateWrapper.eq("id", id);
        return update(updateWrapper);
    }

    /**
     * 获取用户注册赠送新人券
     * @return
     */
    @Override
    public List<StoreCoupon> findRegisterList() {
        String dateStr = DateUtil.nowDate(Constants.DATE_FORMAT);
        LambdaQueryWrapper<StoreCoupon> lqw = new LambdaQueryWrapper<>();
//        lqw.gt(StoreCoupon::getLastTotal, 0);
        lqw.eq(StoreCoupon::getType, 2);
        lqw.eq(StoreCoupon::getStatus, true);
        lqw.eq(StoreCoupon::getIsDel, false);
        lqw.le(StoreCoupon::getReceiveStartTime, dateStr);
        List<StoreCoupon> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CollUtil.newArrayList();
        }
        List<StoreCoupon> couponList = list.stream().filter(coupon -> {
            // 是否限量
            if (coupon.getIsLimited() && coupon.getLastTotal() <= 0) {
                return false;
            }
            // 是否有领取结束时间
            if (ObjectUtil.isNotNull(coupon.getReceiveEndTime())) {
                int compareDate = DateUtil.compareDate(dateStr, DateUtil.dateToStr(coupon.getReceiveEndTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
                if (compareDate > 0) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        return couponList;
    }

    /**
     * 用户可领取的优惠券
     * @author Mr.Zhang
     * @since 2020-05-18
     * @return List<StoreCoupon>
     */
    private List<StoreCoupon> getListByReceive(Integer productId, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Date date = DateUtil.nowDateTime();
        //带 StoreCoupon 类的多条件查询
        LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCoupon::getIsDel, false)
                .eq(StoreCoupon::getStatus, 1)
                //剩余数量大于0 或者不设置上限
                .and(i -> i.gt(StoreCoupon::getLastTotal, 0).or().eq(StoreCoupon::getIsLimited, false))
                //领取时间范围, 结束时间为null则是不限时
                .and(i -> i.isNull(StoreCoupon::getReceiveEndTime).or( p -> p.lt(StoreCoupon::getReceiveStartTime, date).gt(StoreCoupon::getReceiveEndTime, date)));
        lambdaQueryWrapper.eq(StoreCoupon::getType, 1);
        if(productId > 0){
            //有商品id  通用券可以领取，商品券可以领取，分类券可以领取
            getPrimaryKeySql(lambdaQueryWrapper, productId.toString());
        }

        lambdaQueryWrapper.orderByDesc(StoreCoupon::getSort).orderByDesc(StoreCoupon::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    private void getPrimaryKeySql(LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper, String productIdStr){
        if(StringUtils.isBlank(productIdStr)){
            return;
        }

        List<Integer> categoryIdList = storeProductService.getSecondaryCategoryByProductId(productIdStr);
        lambdaQueryWrapper.and(i -> i.and(
                //通用券  商品券  品类券
                t -> t.eq(StoreCoupon::getUseType, 1)
                .or(p -> p.eq(StoreCoupon::getUseType , 2).apply(CrmebUtil.getFindInSetSql("primary_key", productIdStr)))
                .or(c -> c.eq(StoreCoupon::getUseType , 3).apply(CrmebUtil.getFindInSetSql("primary_key", (ArrayList<Integer>) categoryIdList)))
        ));
    }
}

