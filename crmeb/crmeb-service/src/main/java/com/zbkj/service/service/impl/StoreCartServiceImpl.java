package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CartNumRequest;
import com.zbkj.common.request.CartRequest;
import com.zbkj.common.request.CartResetRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.RedisConstatns;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.response.CartInfoResponse;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.model.cat.StoreCart;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.model.product.StoreProductAttrValue;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.User;
import com.zbkj.service.dao.StoreCartDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * StoreCartServiceImpl 接口实现
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
public class StoreCartServiceImpl extends ServiceImpl<StoreCartDao, StoreCart> implements StoreCartService {

    @Resource
    private StoreCartDao dao;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private RedisUtil redisUtil;

    /**
    * 列表
    * @param pageParamRequest 分页类参数
    * @param isValid 是否失效
    * @return List<CartInfoResponse>
    */
    @Override
    public PageInfo<CartInfoResponse> getList(PageParamRequest pageParamRequest, boolean isValid) {
        Integer userId = userService.getUserIdException();
        Page<StoreCart> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //带 StoreCart 类的多条件查询
        LambdaQueryWrapper<StoreCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCart::getUid, userId);
        lambdaQueryWrapper.eq(StoreCart::getStatus, isValid);
        lambdaQueryWrapper.eq(StoreCart::getIsNew, false);
        lambdaQueryWrapper.orderByDesc(StoreCart::getId);
        List<StoreCart> storeCarts = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(storeCarts)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }

        User user = userService.getInfo();
        SystemUserLevel userLevel = null;
        if (ObjectUtil.isNotNull(user) && user.getLevel() > 0) {
            userLevel = systemUserLevelService.getByLevelId(user.getLevel());
        }

        List<CartInfoResponse> response = new ArrayList<>();
        for (StoreCart storeCart : storeCarts) {
            CartInfoResponse cartInfoResponse = new CartInfoResponse();
            BeanUtils.copyProperties(storeCart, cartInfoResponse);
            // 获取商品信息
            StoreProduct storeProduct = storeProductService.getCartByProId(storeCart.getProductId());
            cartInfoResponse.setImage(storeProduct.getImage());
            cartInfoResponse.setStoreName(storeProduct.getStoreName());

            if (!isValid) {// 失效商品直接掠过
                cartInfoResponse.setAttrStatus(false);
                response.add(cartInfoResponse);
                continue ;
            }

            // 获取对应的商品规格信息(只会有一条信息)
            List<StoreProductAttrValue> attrValueList = storeProductAttrValueService.getListByProductIdAndAttrId(storeCart.getProductId(),
                    storeCart.getProductAttrUnique(), Constants.PRODUCT_TYPE_NORMAL);
            // 规格不存在即失效
            if (CollUtil.isEmpty(attrValueList)) {
                cartInfoResponse.setAttrStatus(false);
                response.add(cartInfoResponse);
                continue ;
            }
            StoreProductAttrValue attrValue = attrValueList.get(0);
            if (StrUtil.isNotBlank(attrValue.getImage())) {
                cartInfoResponse.setImage(attrValue.getImage());
            }
            cartInfoResponse.setAttrId(attrValue.getId());
            cartInfoResponse.setSuk(attrValue.getSuk());
            cartInfoResponse.setPrice(attrValue.getPrice());
            cartInfoResponse.setAttrId(attrValue.getId());
            cartInfoResponse.setAttrStatus(attrValue.getStock() > 0);
            cartInfoResponse.setStock(attrValue.getStock());
            if (ObjectUtil.isNotNull(userLevel)) {
                BigDecimal vipPrice = attrValue.getPrice().multiply(new BigDecimal(userLevel.getDiscount())).divide(new BigDecimal(100), 2 ,BigDecimal.ROUND_HALF_UP);
                cartInfoResponse.setVipPrice(vipPrice);
            }
            response.add(cartInfoResponse);
        }
        return CommonPage.copyPageInfo(page, response);
    }

    /**
     * 购物车数量
     * @param request 请求参数
     * @return Map<String, Integer>
     */
    @Override
    public Map<String, Integer> getUserCount(CartNumRequest request) {
        Integer userId = userService.getUserIdException();
        Map<String, Integer> map = new HashMap<>();
        int num;
        if ("total".equals(request.getType())) {
            num = getUserCountByStatus(userId, request.getNumType());
        } else {
            num = getUserSumByStatus(userId, request.getNumType());
        }
        map.put("count", num);
        return map;
    }

    /**
     * 新增商品至购物车
     * @param storeCartRequest 购物车参数
     * @return 添加后的成功标识
     */
    @Override
    public String saveCate(CartRequest storeCartRequest) {
        // 判断商品正常
        StoreProduct product = storeProductService.getById(storeCartRequest.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel() || !product.getIsShow()) {
            throw new CrmebException("未找到对应商品");
        }
        List<StoreProductAttrValue> attrValues = storeProductAttrValueService.getListByProductIdAndAttrId(product.getId(), storeCartRequest.getProductAttrUnique(), Constants.PRODUCT_TYPE_NORMAL);
        if (CollUtil.isEmpty(attrValues)) {
            throw new CrmebException("未找到对应的商品SKU");
        }

        // 普通商品部分(只有普通商品才能添加购物车)
        // 是否已经有同类型商品在购物车，有则添加数量没有则新增
        User currentUser = userService.getInfo();
        StoreCart storeCartPram = new StoreCart();
        storeCartPram.setProductAttrUnique(storeCartRequest.getProductAttrUnique());
        storeCartPram.setUid(currentUser.getUid());
        List<StoreCart> existCarts = getByEntity(storeCartPram); // 这里仅仅能获取一条以信息
        String todayStr = DateUtil.date().toString(Constants.DATE_FORMAT_DATE);
        if (existCarts.size() > 0) { // 购物车添加数量
            StoreCart forUpdateStoreCart = existCarts.get(0);
            forUpdateStoreCart.setCartNum(forUpdateStoreCart.getCartNum() + storeCartRequest.getCartNum());
            boolean updateResult = updateById(forUpdateStoreCart);
            if (!updateResult) throw new CrmebException("添加购物车失败");
            return forUpdateStoreCart.getId()+"";
        } else {// 新增购物车数据
            StoreCart storeCart = new StoreCart();
            BeanUtils.copyProperties(storeCartRequest, storeCart);
            storeCart.setUid(currentUser.getUid());
            storeCart.setType("product");
            if (dao.insert(storeCart) <= 0) throw new CrmebException("添加购物车失败");
            return storeCart.getId()+"";
        }
    }

    /**
     * 删除购物车信息
     * @param ids 待删除id
     * @return 删除结果状态
     */
    @Override
    public Boolean deleteCartByIds(List<Long> ids) {
        return dao.deleteBatchIds(ids) > 0;
    }

    /**
     * 购物车基本查询
     * @param storeCart 购物车参数
     * @return 购物车结果数据
     */
    private List<StoreCart> getByEntity(StoreCart storeCart) {
        LambdaQueryWrapper<StoreCart> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeCart);
        return dao.selectList(lqw);
    }

    /**
     * 检测商品是否有效 更新购物车商品状态
     * @param productId 商品id
     * @return 跟新结果
     */
    @Override
    public Boolean productStatusNotEnable(Integer productId) {
        StoreCart storeCartPram = new StoreCart();
        storeCartPram.setProductId(productId);
        List<StoreCart> existStoreCartProducts = getByEntity(storeCartPram);
        if (null == existStoreCartProducts) return true;
        existStoreCartProducts.forEach(e-> e.setStatus(false));
        return updateBatchById(existStoreCartProducts);
    }

    /**
     * 购物车重选
     * @param resetRequest 重选数据
     * @return 重选结果
     */
    @Override
    public Boolean resetCart(CartResetRequest resetRequest) {
        LambdaQueryWrapper<StoreCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCart::getId, resetRequest.getId());
        StoreCart storeCart = dao.selectOne(lqw);
        if (ObjectUtil.isNull(storeCart)) throw new CrmebException("购物车不存在");
        if (ObjectUtil.isNull(resetRequest.getNum()) || resetRequest.getNum() <= 0 || resetRequest.getNum() >= 999)
            throw new CrmebException("数量不合法");
        storeCart.setCartNum(resetRequest.getNum());
        storeCart.setProductAttrUnique(resetRequest.getUnique() + "");
        boolean updateResult = dao.updateById(storeCart) > 0;
        if (!updateResult) throw new CrmebException("重选添加购物车失败");
        productStatusEnableFlag(resetRequest.getId(), true);
        return updateResult;
    }

    /**
     * 对应sku购物车生效
     * @param skuIdList skuIdList
     * @return Boolean
     */
    @Override
    public Boolean productStatusNoEnable(List<Integer> skuIdList) {
        LambdaUpdateWrapper<StoreCart> lqw = new LambdaUpdateWrapper<>();
        lqw.set(StoreCart::getStatus, true);
        lqw.in(StoreCart::getProductAttrUnique, skuIdList);
        lqw.eq(StoreCart::getIsNew, false);
        return update(lqw);
    }

    /**
     * 删除商品对应的购物车
     * @param productId 商品id
     */
    @Override
    public Boolean productDelete(Integer productId) {
        StoreCart storeCartPram = new StoreCart();
        storeCartPram.setProductId(productId);
        List<StoreCart> existStoreCartProducts = getByEntity(storeCartPram);
        if (CollUtil.isNotEmpty(existStoreCartProducts)) return true;
        List<Long> cartIds = existStoreCartProducts.stream().map(StoreCart::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(cartIds)) {
            deleteCartByIds(cartIds);
        }
        return true;
    }

    /**
     * 通过id和uid获取购物车信息
     * @param id 购物车id
     * @param uid 用户uid
     * @return StoreCart
     */
    @Override
    public StoreCart getByIdAndUid(Long id, Integer uid) {
        LambdaQueryWrapper<StoreCart> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreCart::getId, id);
        lqw.eq(StoreCart::getUid, uid);
        lqw.eq(StoreCart::getIsNew, false);
        lqw.eq(StoreCart::getStatus, true);
        return dao.selectOne(lqw);
    }

    /**
     * 获取购物车商品数量（不区分规格）
     * @param uid 用户uid
     * @param proId 商品id
     */
    @Override
    public Integer getProductNumByUidAndProductId(Integer uid, Integer proId) {
        LambdaQueryWrapper<StoreCart> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreCart::getUid, uid);
        lqw.eq(StoreCart::getProductId, proId);
        lqw.eq(StoreCart::getStatus, true);
        List<StoreCart> cartList = dao.selectList(lqw);
        if (CollUtil.isNotEmpty(cartList)) {
            return cartList.stream().mapToInt(StoreCart::getCartNum).sum();
        }
        return 0;
    }

    /**
     * 修改购物车商品数量
     * @param id 购物车id
     * @param number 数量
     */
    @Override
    public Boolean updateCartNum(Integer id, Integer number) {
        if (ObjectUtil.isNull(number)) throw new CrmebException("商品数量不合法");
        if (number <=0 || number > 99) throw new CrmebException("商品数量不能小于1大于99");
        StoreCart storeCart = getById(id);
        if (ObjectUtil.isNull(storeCart)) throw new CrmebException("当前购物车不存在");
        if (storeCart.getCartNum().equals(number)) return true;
        storeCart.setCartNum(number);
        return updateById(storeCart);
    }

    ///////////////////////////////////////////////////////////////////自定义方法
    /**
     * 购物车商品数量（条数）
     * @param userId Integer 用户id
     * @param status Boolean 商品类型：true-有效商品，false-无效商品
     * @return Integer
     */
    private Integer getUserCountByStatus(Integer userId, Boolean status) {
        //购物车商品种类数量
        LambdaQueryWrapper<StoreCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCart::getUid, userId);
        lambdaQueryWrapper.eq(StoreCart::getStatus, status);
        lambdaQueryWrapper.eq(StoreCart::getIsNew, false);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 购物车购买商品总数量
     * @param userId Integer 用户id
     * @param status 商品类型：true-有效商品，false-无效商品
     * @return Integer
     */
    private Integer getUserSumByStatus(Integer userId, Boolean status) {
        QueryWrapper<StoreCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("ifnull(sum(cart_num), 0) as cart_num");
        queryWrapper.eq("uid", userId);
        queryWrapper.eq("is_new", false);
        queryWrapper.eq("status", status);
        StoreCart storeCart = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(storeCart)) {
            return 0;
        }
        return storeCart.getCartNum();
    }

    /**
     * 根据购物车id更新状态
     * @param carId 购物车id
     * @param flag 待更新状态值
     * @return 更新结果
     */
    private Boolean productStatusEnableFlag(Long carId, Boolean flag) {
        StoreCart storeCartPram = new StoreCart();
        storeCartPram.setId(carId);
        List<StoreCart> existStoreCartProducts = getByEntity(storeCartPram);
        if(ObjectUtil.isNull(existStoreCartProducts)) return false;
        existStoreCartProducts = existStoreCartProducts.stream().map(e->{
            e.setStatus(flag);
            return e;
        }).collect(Collectors.toList());
        return updateBatchById(existStoreCartProducts);
    }
}

