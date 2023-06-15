package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SearchAndPageRequest;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.CouponConstants;
import com.zbkj.common.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.model.category.Category;
import com.zbkj.common.model.coupon.StoreCoupon;
import com.zbkj.common.model.coupon.StoreCouponUser;
import com.zbkj.common.request.StoreCouponRequest;
import com.zbkj.common.request.StoreCouponSearchRequest;
import com.zbkj.common.response.StoreCouponFrontResponse;
import com.zbkj.common.response.StoreCouponInfoResponse;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.service.dao.StoreCouponDao;
import com.zbkj.service.service.*;
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
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
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

    @Autowired
    private UserService userService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<StoreCoupon>
    */
    @Override
    public List<StoreCoupon> getList(StoreCouponSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreCoupon 类的多条件查询
        LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCoupon::getIsDel, false);

        if (null != request.getType()) {
            lambdaQueryWrapper.eq(StoreCoupon::getType, request.getType());
        }

        if (null != request.getStatus()) {
            lambdaQueryWrapper.eq(StoreCoupon::getStatus, request.getStatus());
        }

        if (StrUtil.isNotBlank(request.getName())) {
            lambdaQueryWrapper.like(StoreCoupon::getName, request.getName());
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
        if (storeCoupon.getIsLimited() && (storeCoupon.getTotal() == null || storeCoupon.getTotal() == 0)) {
            throw new CrmebException("请输入数量！");
        }

        if (request.getUseType() > 1 && (StrUtil.isBlank(request.getPrimaryKey()))) {
            throw new CrmebException("请选择商品/分类！");
        }

        storeCoupon.setLastTotal(storeCoupon.getTotal());
        if (!request.getIsForever()) {
            storeCoupon.setReceiveStartTime(DateUtil.nowDateTime()); //开始时间设置为当前时间
        }else{
            if (storeCoupon.getReceiveStartTime() == null || storeCoupon.getReceiveEndTime() == null) {
                throw new CrmebException("请选择领取时间范围！");
            }

            int compareDate = DateUtil.compareDate(DateUtil.dateToStr(storeCoupon.getReceiveStartTime(), Constants.DATE_FORMAT), DateUtil.dateToStr(storeCoupon.getReceiveEndTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
            if (compareDate > -1) {
                throw new CrmebException("请选择正确的领取时间范围！");
            }
        }

        //非固定时间, 领取后多少天
        if (!request.getIsFixedTime()) {
            if (storeCoupon.getDay() == null || storeCoupon.getDay() == 0) {
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
    private void checkException(StoreCoupon storeCoupon) {
        if (storeCoupon == null || storeCoupon.getIsDel() || !storeCoupon.getStatus()) {
            throw new CrmebException("优惠券信息不存在或者已失效！");
        }

        //看是否过期
        if (!(storeCoupon.getReceiveEndTime() == null || "".equals(storeCoupon.getReceiveEndTime()))) {
            //非永久可领取
            String date = DateUtil.nowDateTimeStr();
            int result = DateUtil.compareDate(date, DateUtil.dateToStr(storeCoupon.getReceiveEndTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
            if (result == 1) {
                //过期了
                throw new CrmebException("已超过优惠券领取最后期限！");
            }
        }

        //看是否有剩余数量
        if (storeCoupon.getIsLimited()) {
            //考虑到并发溢出的问题用大于等于
            if (storeCoupon.getLastTotal() < 1) {
                throw new CrmebException("此优惠券已经被领完了！");
            }
        }
    }

    /**
     * 优惠券详情
     * @param id Integer 获取可用优惠券的商品id
     * @return StoreCouponInfoResponse
     */
    @Override
    public StoreCouponInfoResponse info(Integer id) {
        StoreCoupon storeCoupon = getById(id);
        if (ObjectUtil.isNull(storeCoupon) || storeCoupon.getIsDel() || !storeCoupon.getStatus()) {
            throw new CrmebException("优惠券信息不存在或者已失效！");
        }

        List<StoreProduct> productList = null;
        List<Category> categoryList = null;
        if (StrUtil.isNotBlank(storeCoupon.getPrimaryKey()) && storeCoupon.getUseType() > 1) {
            List<Integer> primaryIdList = CrmebUtil.stringToArray(storeCoupon.getPrimaryKey());
            if (storeCoupon.getUseType() == 2) {
                productList = storeProductService.getListInIds(primaryIdList);
            }
            if (storeCoupon.getUseType() == 3) {
                categoryList = categoryService.getByIds(primaryIdList);
            }
        }

        StoreCouponRequest coupon = new StoreCouponRequest();
        BeanUtils.copyProperties(storeCoupon, coupon);
        coupon.setIsForever(false);
        if (ObjectUtil.isNotNull(coupon.getReceiveEndTime())) {
            coupon.setIsForever(true);
        }

        return new StoreCouponInfoResponse(coupon, productList, categoryList);
    }

    /**
     * 根据优惠券id获取
     * @param ids 优惠券id集合
     * @return List<StoreCoupon>
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
     * @return List<StoreCoupon>
     */
    @Override
    public List<StoreCoupon> findRegisterList() {
        String dateStr = DateUtil.nowDate(Constants.DATE_FORMAT);
        LambdaQueryWrapper<StoreCoupon> lqw = new LambdaQueryWrapper<>();
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
            // 判断是否达到可领取时间
            if (ObjectUtil.isNotNull(coupon.getReceiveStartTime())) {
                //非永久可领取
                int result = DateUtil.compareDate(dateStr, DateUtil.dateToStr(coupon.getReceiveStartTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
                if (result == -1) {
                    // 未开始
                    return false;
                }
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
     * 发送优惠券列表
     * @param request 搜索分页参数
     * @return 优惠券列表
     * PC管理员可发送优惠券：手动领取类型，状态开启，且有剩余数量的优惠券
     * 只支持优惠券名称模糊搜索
     */
    @Override
    public List<StoreCoupon> getSendList(SearchAndPageRequest request) {
        PageHelper.startPage(request.getPage(), request.getLimit());

        LambdaQueryWrapper<StoreCoupon> lqw = new LambdaQueryWrapper<>();
        lqw.select(StoreCoupon::getId, StoreCoupon::getName, StoreCoupon::getMoney, StoreCoupon::getMinPrice,
                StoreCoupon::getUseStartTime, StoreCoupon::getUseEndTime, StoreCoupon::getIsFixedTime, StoreCoupon::getDay,
                StoreCoupon::getIsLimited, StoreCoupon::getLastTotal);
        lqw.eq(StoreCoupon::getIsDel, false);
        if (ObjectUtil.isNotNull(request.getType())) {
            lqw.eq(StoreCoupon::getType, request.getType());
        }
        lqw.eq(StoreCoupon::getStatus, true);
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lqw.like(StoreCoupon::getName, request.getKeywords());
        }
        lqw.and(o -> o.eq(StoreCoupon::getIsLimited, false).or().ge(StoreCoupon::getLastTotal, 0));
        lqw.and(o -> o.isNull(StoreCoupon::getReceiveEndTime).or().gt(StoreCoupon::getReceiveEndTime, DateUtil.nowDate(Constants.DATE_FORMAT)));
        lqw.orderByDesc(StoreCoupon::getSort, StoreCoupon::getId);
        return dao.selectList(lqw);
    }

    /**
     * 删除优惠券
     * @param id 优惠券id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        StoreCoupon coupon = getById(id);
        if (ObjectUtil.isNull(coupon) || coupon.getIsDel()) {
            throw new CrmebException("优惠券不存在");
        }
        coupon.setIsDel(true);
        return dao.updateById(coupon) > 0;
    }

    /**
     * 移动端优惠券列表
     * @param type 类型，1-通用，2-商品，3-品类
     * @param productId 产品id，搜索产品指定优惠券
     * @param pageParamRequest 分页参数
     * @return List<StoreCouponFrontResponse>
     */
    @Override
    public List<StoreCouponFrontResponse> getH5List(Integer type, Integer productId, PageParamRequest pageParamRequest) {
        // 获取优惠券列表
        List<StoreCoupon> list = getListByReceive(type, productId, pageParamRequest);
        if (ObjectUtil.isNull(list)) {
            return null;
        }
        //获取用户当前已领取未使用的优惠券
        HashMap<Integer, StoreCouponUser> couponUserMap = null;
        Integer userId = userService.getUserId();
        if (userId > 0) {
            couponUserMap = storeCouponUserService.getMapByUserId(userId);
        }
        List<StoreCouponFrontResponse> storeCouponFrontResponseArrayList = new ArrayList<>();
        for (StoreCoupon storeCoupon : list) {
            StoreCouponFrontResponse response = new StoreCouponFrontResponse();
            BeanUtils.copyProperties(storeCoupon, response);

            if (userId > 0) {
                if (CollUtil.isNotEmpty(couponUserMap) && couponUserMap.containsKey(storeCoupon.getId())) {
                    response.setIsUse(true);
                }
            }

            if (response.getReceiveEndTime() == null) {
                response.setReceiveStartTime(null);
            }

            // 更改使用时间格式，去掉时分秒
            response.setUseStartTimeStr(DateUtil.dateToStr(storeCoupon.getUseStartTime(), Constants.DATE_FORMAT_DATE));
            response.setUseEndTimeStr(DateUtil.dateToStr(storeCoupon.getUseEndTime(), Constants.DATE_FORMAT_DATE));
            storeCouponFrontResponseArrayList.add(response);
        }

        return storeCouponFrontResponseArrayList;
    }

    /**
     * 修改优惠券状态
     * @param id 优惠券id
     * @param status 状态
     */
    @Override
    public Boolean updateStatus(Integer id, Boolean status) {
        StoreCoupon coupon = getById(id);
        if (ObjectUtil.isNull(coupon)) {
            throw new CrmebException("优惠券不存在");
        }
        if (coupon.getStatus().equals(status)) {
            throw new CrmebException("优惠券状态无需变更");
        }
        StoreCoupon storeCoupon = new StoreCoupon();
        storeCoupon.setId(id);
        storeCoupon.setStatus(status);
        return updateById(storeCoupon);
    }

    /**
     * 用户可领取的优惠券
     * @return List<StoreCoupon>
     */
    private List<StoreCoupon> getListByReceive(Integer type, Integer productId, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Date date = DateUtil.nowDateTime();
        //带 StoreCoupon 类的多条件查询
        LambdaQueryWrapper<StoreCoupon> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCoupon::getIsDel, false);
        lqw.eq(StoreCoupon::getStatus, 1);
        //剩余数量大于0 或者不设置上限
        lqw.and(i -> i.gt(StoreCoupon::getLastTotal, 0).or().eq(StoreCoupon::getIsLimited, false));
        //领取时间范围, 结束时间为null则是不限时
        lqw.and(i -> i.isNull(StoreCoupon::getReceiveEndTime).or( p -> p.lt(StoreCoupon::getReceiveStartTime, date).gt(StoreCoupon::getReceiveEndTime, date)));
        // 用户使用时间范围，结束时间为null则是不限时
        lqw.and(i -> i.isNull(StoreCoupon::getUseEndTime).or(p -> p.gt(StoreCoupon::getUseEndTime, date)));
        lqw.eq(StoreCoupon::getType, 1);
        if (productId > 0) {
            //有商品id  通用券可以领取，商品券可以领取，分类券可以领取
            getPrimaryKeySql(lqw, productId.toString());
        }
        switch (type) {
            case 1:
                lqw.eq(StoreCoupon::getUseType, CouponConstants.COUPON_USE_TYPE_COMMON);
                break;
            case 2:
                lqw.eq(StoreCoupon::getUseType, CouponConstants.COUPON_USE_TYPE_PRODUCT);
                break;
            case 3:
                lqw.eq(StoreCoupon::getUseType, CouponConstants.COUPON_USE_TYPE_CATEGORY);
                break;
        }

        lqw.orderByDesc(StoreCoupon::getSort).orderByDesc(StoreCoupon::getId);
        return dao.selectList(lqw);
    }

    private void getPrimaryKeySql(LambdaQueryWrapper<StoreCoupon> lambdaQueryWrapper, String productIdStr) {
        if (StrUtil.isBlank(productIdStr)) {
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

