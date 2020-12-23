package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.bargain.model.StoreBargain;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.front.request.CartResetRequest;
import com.zbkj.crmeb.seckill.model.StoreSeckill;
import com.zbkj.crmeb.seckill.service.StoreSeckillMangerService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.store.dao.StoreCartDao;
import com.zbkj.crmeb.store.model.StoreCart;
import com.zbkj.crmeb.store.model.StoreProductAttrValue;
import com.zbkj.crmeb.store.response.StoreCartResponse;
import com.zbkj.crmeb.store.response.StoreProductCartProductInfoResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserLevel;
import com.zbkj.crmeb.user.service.UserLevelService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * StoreCartServiceImpl 接口实现
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
    private SystemConfigService systemConfigService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private OrderUtils orderUtils;

    @Autowired
    private StoreSeckillService storeSeckillService;

    @Autowired
    private StoreSeckillMangerService storeSeckillMangerService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    /**
    * 列表
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang edite by stivepeim 2020-7-4
    * @since 2020-05-28
    * @return List<StoreCart>
    */
    @Override
    public List<StoreCartResponse> getList(PageParamRequest pageParamRequest, StoreCart cart, boolean isValid) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //带 StoreCart 类的多条件查询
        LambdaQueryWrapper<StoreCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCart::getUid, userService.getUserIdException());
        lambdaQueryWrapper.eq(StoreCart::getStatus, isValid);
        lambdaQueryWrapper.eq(StoreCart::getIsNew, false);
        lambdaQueryWrapper.orderByDesc(StoreCart::getCreateTime);
        List<StoreCart> storeCarts = dao.selectList(lambdaQueryWrapper);
        List<StoreCartResponse> response = new ArrayList<>();

        for (StoreCart storeCart : storeCarts) {
            List<StoreProductAttrValue> productAttrValues =
                    storeProductAttrValueService.getListByProductIdAndAttrId(
                            storeCart.getProductId(),
                            storeCart.getProductAttrUnique(),Constants.PRODUCT_TYPE_NORMAL);
            // 属性不存在证明失效
            StoreCartResponse storeCartResponse = new StoreCartResponse();
            if(productAttrValues.size() == 0){
                BeanUtils.copyProperties(storeCart,storeCartResponse);
                storeCartResponse.setAttrStatus(false);
                response.add(storeCartResponse);
            }
            BeanUtils.copyProperties(storeCart,storeCartResponse);
            StoreProductResponse product = storeProductService.getByProductId(storeCart.getProductId());
            StoreProductCartProductInfoResponse p = new StoreProductCartProductInfoResponse();
            BeanUtils.copyProperties(product,p);
            storeCartResponse.setProductInfo(p);
            for (StoreProductAttrValue productAttrValue : productAttrValues) {
                // 商品是否失效
                if(StringUtils.isBlank(productAttrValue.getSuk())){
                    p.setAttrInfo(productAttrValue.setSuk("已失效"));
                }else{
                    p.setAttrInfo(productAttrValue);
                }
                storeCartResponse.setAttrStatus(productAttrValue.getStock() > 0);
                storeCartResponse.setTruePrice(setVipPrice(productAttrValue.getPrice(),userService.getUserIdException(),true));
                storeCartResponse.setVipTruePrice(setVipPrice(productAttrValue.getPrice(),userService.getUserIdException(),false));
                storeCartResponse.setTrueStock(product.getStock());
                storeCartResponse.setCostPrice(product.getCost());
                response.add(storeCartResponse);
            }
        }
        return response;
    }

    /**
     * 根据用户id和购物车id查询
     * @param userId 用户id
     * @param cartIds 购物车id集合
     * @param isNew     是否立即购买
     * @return 购物车列表
     */
    @Override
    public List<StoreCartResponse> getListByUserIdAndCartIds(Integer userId, List<String> cartIds,Integer isNew) {
        LambdaQueryWrapper<StoreCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreCart::getId,cartIds);
        lambdaQueryWrapper.eq(StoreCart::getUid, userId);
        if(null != isNew) lambdaQueryWrapper.eq(StoreCart::getIsNew, isNew);
        lambdaQueryWrapper.orderByDesc(StoreCart::getCreateTime);
        List<StoreCart> storeCarts = dao.selectList(lambdaQueryWrapper);
        List<StoreCartResponse> response = new ArrayList<>();

        for (StoreCart storeCart : storeCarts) {
            List<StoreProductAttrValue> productAttrValues =
                    storeProductAttrValueService.getListByProductIdAndAttrId(
                            storeCart.getProductId(),
                            storeCart.getProductAttrUnique(),
                            Constants.PRODUCT_TYPE_NORMAL);
            for (StoreProductAttrValue productAttrValue : productAttrValues) {
                StoreCartResponse storeCartResponse = new StoreCartResponse();
                BeanUtils.copyProperties(storeCart,storeCartResponse);
                StoreProductResponse product = storeProductService.getByProductId(productAttrValue.getProductId());
                StoreProductCartProductInfoResponse p = new StoreProductCartProductInfoResponse();
                BeanUtils.copyProperties(product,p);
                p.setAttrInfo(productAttrValue);
                storeCartResponse.setProductInfo(p);
                storeCartResponse.setTruePrice(setVipPrice(productAttrValue.getPrice(),userService.getUserIdException(),true));
                storeCartResponse.setVipTruePrice(setVipPrice(productAttrValue.getPrice(),userService.getUserIdException(),false));
                storeCartResponse.setTrueStock(product.getStock());
                storeCartResponse.setCostPrice(product.getCost());
                response.add(storeCartResponse);
            }
        }
        return response;
    }

    /**
     * 根据用户id和购物车id集合获取购物车列表
     * @param userId 当前用户id
     * @param cartIds 购物车id集合
     * @return 购物车列表集合
     */
    @Override
    public List<StoreCart> getList(Integer userId, List<Integer> cartIds) {
        LambdaQueryWrapper<StoreCart> lqwStoreList = new LambdaQueryWrapper<>();
        lqwStoreList.eq(StoreCart::getUid,userId);
        lqwStoreList.eq(StoreCart::getType, "product");
//        lqwStoreList.eq(StoreCart::getI)
        lqwStoreList.in(StoreCart::getId,cartIds);
        lqwStoreList.orderByDesc(StoreCart::getCreateTime);
        return dao.selectList(lqwStoreList);
    }

    /**
     * 购物车数量
     * @param userId Integer 用户id
     * @param type String 类型
     * @param numType boolean 数量类型
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return List<StoreCart>
     */
    @Override
    public Integer getUserCount(Integer userId, String type, boolean numType) {
        if(numType){
            return getUserSumByType(userId, type);
        }else{
            return getUserCountByType(userId, type);
        }
    }

    /**
     * 新增商品至购物车
     * @param storeCart 购物车参数
     * @return 添加后的成功标识
     */
    @Override
    public String saveCate(StoreCart storeCart) {
        // 判断商品正常
        StoreProductResponse existProduct = storeProductService.getByProductId(storeCart.getProductId());
        if(null == existProduct) throw new CrmebException("商品不存在");

        /**
         * ================================
         * 活动商品部分
         * ================================
         */

        // 活动校验
        if (!storeCart.getIsNew() && (ObjectUtil.isNotNull(storeCart.getSeckillId()) || ObjectUtil.isNotNull(storeCart.getBargainId()) || ObjectUtil.isNotNull(storeCart.getCombinationId()))) {
            throw new CrmebException("活动商品不能加入购物车");
        }

        // 秒杀商品业务处理
        if(ObjectUtil.isNotNull(storeCart.getSeckillId()) && storeCart.getIsNew()){
            storeCart.setCartNum(1); // 秒杀仅仅只能购买一件商品
            List<String> cacheSecKillIds = buildCartInfoForSeckill(storeCart);
            return cacheSecKillIds.get(0);
         }

        // 砍价商品业务处理
        if (ObjectUtil.isNotNull(storeCart.getBargainId()) && storeCart.getIsNew()) {
            storeCart.setCartNum(1); // 砍价一次仅仅只能购买一件商品
            List<String> cacheBargainIds = buildCartInfoForBargain(storeCart);
            return cacheBargainIds.get(0);
        }

        // 拼团商品业务处理
        if (ObjectUtil.isNotNull(storeCart.getCombinationId()) && storeCart.getIsNew()) {
            List<String> cacheBargainIds = buildCartInfoForCombination(storeCart);
            return cacheBargainIds.get(0);
        }

        /**
         * ================================
         * 普通商品部分
         * ================================
         */
        // 是否已经有同类型商品在购物车，有则添加数量没有则新增
        StoreCart storeCartPram = new StoreCart();
        storeCartPram.setProductAttrUnique(storeCart.getProductAttrUnique());
        storeCartPram.setUid(userService.getUserId());
        storeCartPram.setIsNew(false);
        List<StoreCart> existCarts = getByEntity(storeCartPram); // 这里仅仅能获取一条以信息
        if(existCarts.size() > 0 && !storeCart.getIsNew()){ // 加入购物车
            StoreCart forUpdateStoreCart = existCarts.get(0);
            forUpdateStoreCart.setCartNum(forUpdateStoreCart.getCartNum()+storeCart.getCartNum());
            boolean updateResult = updateById(forUpdateStoreCart);
            if(!updateResult) throw new CrmebException("添加购物车失败");
            return forUpdateStoreCart.getId()+"";
        }else{// 立即购买
            User currentUser = userService.getInfo();
            storeCart.setUid(currentUser.getUid());
            storeCart.setType("product");
//            storeCart.setIsNew(false);
//            if (storeCart.getIsNew()) {// 立即购买才为true
//                storeCart.setIsNew(true);
//            }
            if(dao.insert(storeCart) <= 0) throw new CrmebException("添加购物车失败");
            return storeCart.getId()+"";
        }
    }


    /**
     * 设置会员价格
     * @param price 原来价格
     * @param userId 会员id
     * @param isSingle
     * @return
     */
    @Override
    public BigDecimal setVipPrice(BigDecimal price, Integer userId, boolean isSingle) {
        // 判断会员功能是否开启
        Integer memberFuncStatus = Integer.valueOf(systemConfigService.getValueByKey("member_func_status"));
        Integer memberPriceStatus = Integer.valueOf(systemConfigService.getValueByKey("member_price_status"));
        if(memberFuncStatus <= 0 || memberPriceStatus <= 0){
            return isSingle ? price : BigDecimal.ZERO;
        }
//        User userInfo = userService.getById(userId);
        UserLevel userLevelInfo = userLevelService.getUserLevelByUserId(userId);
        if(userLevelInfo.getDiscount().compareTo(BigDecimal.ZERO) == 0){ // 不是会员原价返回
            return isSingle ? price : BigDecimal.ZERO;
        }
        BigDecimal discount = userLevelInfo.getDiscount().divide(BigDecimal.valueOf(100));

        return isSingle ? discount.divide(price) : discount.multiply(price);
    }

    /**
     * 删除购物车信息
     * @param ids 待删除id
     * @return 删除结果状态
     */
    @Override
    public boolean deleteCartByIds(List<Integer> ids) {
        return dao.deleteBatchIds(ids) > 0;
    }

    /**
     * 购物车基本查询
     * @param storeCart 购物车参数
     * @return 购物车结果数据
     */
    @Override
    public List<StoreCart> getByEntity(StoreCart storeCart) {
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
    public boolean productStatusNotEnable(Integer productId) {
//        StoreProductResponse storeProductResponse = storeProductService.getByProductId(productId);
//        if(null == storeProductResponse) return false;
        StoreCart storeCartPram = new StoreCart();
        storeCartPram.setProductId(productId);
        List<StoreCart> existStoreCartProducts = getByEntity(storeCartPram);
        if(null == existStoreCartProducts) return false;
        existStoreCartProducts = existStoreCartProducts.stream().map(e->{
            e.setStatus(false);
            return e;
        }).collect(Collectors.toList());
        return updateBatchById(existStoreCartProducts);
    }

    /**
     * 购物车重选
     * @param resetRequest 重选数据
     * @return 重选结果
     */
    @Override
    public boolean resetCart(CartResetRequest resetRequest) {
        LambdaQueryWrapper<StoreCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCart::getId, resetRequest.getId());
        StoreCart storeCart = dao.selectOne(lqw);
        if(null == storeCart) throw new CrmebException("购物车不存在");
        if(null == resetRequest.getNum() || resetRequest.getNum() <= 0 || resetRequest.getNum() >= 999)
            throw new CrmebException("数量不合法");
        storeCart.setCartNum(resetRequest.getNum());
        storeCart.setProductAttrUnique(resetRequest.getUnique()+"");
        boolean updateResult = dao.updateById(storeCart) > 0;
        if(!updateResult) throw new CrmebException("重选添加购物车失败");
        productStatusEnableFlag(resetRequest.getId(), true);
        return updateResult;
    }

    ///////////////////////////////////////////////////////////////////自定义方法
    /**
     * 购物车商品种类数量
     * @param userId Integer 用户id
     * @param type String 类型
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return Integer
     */
    private Integer getUserCountByType(Integer userId, String type) {
        //购物车商品种类数量
        LambdaQueryWrapper<StoreCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCart::getUid, userId)
                .eq(StoreCart::getType, type)
                .eq(StoreCart::getIsNew, false);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 购物车商品总数量
     * @param userId Integer 用户id
     * @param type String 类型
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return Integer
     */
    private Integer getUserSumByType(Integer userId, String type) {
        QueryWrapper<StoreCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(cart_num) as cart_num")
                .eq("uid", userId)
                .eq("type", type)
                .eq("is_new", false);
        StoreCart storeCart = dao.selectOne(queryWrapper);
        if(null == storeCart || null == storeCart.getCartNum()){
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
    private boolean productStatusEnableFlag(Long carId,boolean flag) {
        StoreCart storeCartPram = new StoreCart();
        storeCartPram.setId(carId);
        List<StoreCart> existStoreCartProducts = getByEntity(storeCartPram);
        if(null == existStoreCartProducts) return false;
        existStoreCartProducts = existStoreCartProducts.stream().map(e->{
            e.setStatus(flag);
            return e;
        }).collect(Collectors.toList());
        return updateBatchById(existStoreCartProducts);
    }

    /**
     * 组装秒杀下单前数据
     * @param storeCartPram 秒杀参数
     * @return  组装好的下单前秒杀数据
     */
    private  List<String> buildCartInfoForSeckill(StoreCart storeCartPram){
        User currentUser = userService.getInfoException();
        List<String> cacheIdsResult = new ArrayList<>();
        List<StoreCartResponse> storeCartResponses = new ArrayList<>();
        StoreCartResponse storeCartResponse = new StoreCartResponse();
        StoreProductCartProductInfoResponse spcpInfo = new StoreProductCartProductInfoResponse();

        // 秒杀商品数据验证
        StoreSeckill storeSeckill = orderUtils.validSecKill(storeCartPram, currentUser);

        BeanUtils.copyProperties(storeSeckill, spcpInfo);

        // 判断商品对应属性是否有效
        StoreProductAttrValue apAttrValuePram = new StoreProductAttrValue();
        apAttrValuePram.setProductId(storeCartPram.getSeckillId());
        apAttrValuePram.setId(Integer.valueOf(storeCartPram.getProductAttrUnique()));
        apAttrValuePram.setType(Constants.PRODUCT_TYPE_SECKILL);
        List<StoreProductAttrValue> seckillAttrValues = storeProductAttrValueService.getByEntity(apAttrValuePram);
        StoreProductAttrValue existSPAttrValue = new StoreProductAttrValue();
        if(null != seckillAttrValues && seckillAttrValues.size() > 0) existSPAttrValue = seckillAttrValues.get(0);
        if(null == existSPAttrValue) throw new CrmebException("请选择有效的商品属性");
        if(existSPAttrValue.getStock() <= 0) throw new CrmebException("该商品库存不足");

        spcpInfo.setAttrInfo(existSPAttrValue);
        spcpInfo.setStoreInfo(storeSeckill.getInfo());
        spcpInfo.setStoreName(storeSeckill.getTitle());

        storeCartResponse.setSeckillId(storeCartPram.getSeckillId());
        storeCartResponse.setProductInfo(spcpInfo);
        storeCartResponse.setTrueStock(storeCartResponse.getProductInfo().getAttrInfo().getStock());
        storeCartResponse.setCostPrice(storeCartResponse.getProductInfo().getAttrInfo().getCost());
        storeCartResponse.setTruePrice(existSPAttrValue.getPrice());
        storeCartResponse.setVipTruePrice(BigDecimal.ZERO);

        storeCartResponse.setType(Constants.PRODUCT_TYPE_SECKILL+"");// 秒杀=1
        storeCartResponse.setProductId(storeCartPram.getProductId());
        storeCartResponse.setProductAttrUnique(storeCartPram.getProductAttrUnique());
        storeCartResponse.setCartNum(1);
        storeCartResponses.add(storeCartResponse);

        cacheIdsResult.add(orderUtils.setCacheOrderData(currentUser, storeCartResponses));
        return cacheIdsResult;
    }

    /**
     * 组装砍价下单前数据
     * @param storeCartPram 砍价参数
     * @return  组装好的下单前砍价数据
     */
    private List<String> buildCartInfoForBargain(StoreCart storeCartPram) {
        User currentUser = userService.getInfoException();
        List<String> cacheIdsResult = new ArrayList<>();
        List<StoreCartResponse> storeCartResponses = new ArrayList<>();
        StoreCartResponse storeCartResponse = new StoreCartResponse();
        StoreProductCartProductInfoResponse spcpInfo = new StoreProductCartProductInfoResponse();

        // 砍价商品数据验证
        StoreBargain storeBargain = orderUtils.validBargain(storeCartPram, currentUser);
        BeanUtils.copyProperties(storeBargain, spcpInfo);

        // 判断商品对应属性是否有效
        StoreProductAttrValue apAttrValuePram = new StoreProductAttrValue();
        apAttrValuePram.setProductId(storeCartPram.getSeckillId());
        apAttrValuePram.setId(Integer.valueOf(storeCartPram.getProductAttrUnique()));
        apAttrValuePram.setType(Constants.PRODUCT_TYPE_BARGAIN);
        List<StoreProductAttrValue> bargainAttrValues = storeProductAttrValueService.getByEntity(apAttrValuePram);
        StoreProductAttrValue existSPAttrValue = new StoreProductAttrValue();
        if(CollUtil.isNotEmpty(bargainAttrValues)) existSPAttrValue = bargainAttrValues.get(0);
        if(ObjectUtil.isEmpty(existSPAttrValue)) throw new CrmebException("请选择有效的商品属性");
        if(existSPAttrValue.getStock() <= 0) throw new CrmebException("该商品库存不足");

        spcpInfo.setAttrInfo(existSPAttrValue);
        spcpInfo.setStoreInfo(storeBargain.getInfo());
        spcpInfo.setStoreName(storeBargain.getTitle());

        storeCartResponse.setBargainId(storeCartPram.getBargainId());
        storeCartResponse.setProductInfo(spcpInfo);
        storeCartResponse.setTrueStock(storeCartResponse.getProductInfo().getAttrInfo().getStock());
        storeCartResponse.setCostPrice(storeCartResponse.getProductInfo().getAttrInfo().getCost());
        storeCartResponse.setTruePrice(storeBargain.getMinPrice());
        storeCartResponse.setVipTruePrice(BigDecimal.ZERO);

        storeCartResponse.setType(Constants.PRODUCT_TYPE_BARGAIN +"");// 砍价=2
        storeCartResponse.setProductId(storeCartPram.getProductId());
        storeCartResponse.setProductAttrUnique(storeCartPram.getProductAttrUnique());
        storeCartResponse.setCartNum(1);
        storeCartResponses.add(storeCartResponse);

        cacheIdsResult.add(orderUtils.setCacheOrderData(currentUser, storeCartResponses));
        return cacheIdsResult;
    }

    /**
     * 组装拼团下单前数据
     * @param storeCartPram 砍价参数
     * @return  组装好的下单前砍价数据
     */
    private List<String> buildCartInfoForCombination(StoreCart storeCartPram) {
        User currentUser = userService.getInfoException();
        List<String> cacheIdsResult = new ArrayList<>();
        List<StoreCartResponse> storeCartResponses = new ArrayList<>();
        StoreCartResponse storeCartResponse = new StoreCartResponse();
        StoreProductCartProductInfoResponse spcpInfo = new StoreProductCartProductInfoResponse();

        // 拼团商品数据验证
        StoreCombination storeCombination = orderUtils.validCombination(storeCartPram, currentUser);
        BeanUtils.copyProperties(storeCombination, spcpInfo);

        // 判断商品对应属性是否有效
        StoreProductAttrValue apAttrValuePram = new StoreProductAttrValue();
        apAttrValuePram.setProductId(storeCartPram.getCombinationId());
        apAttrValuePram.setId(Integer.valueOf(storeCartPram.getProductAttrUnique()));
        apAttrValuePram.setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttrValue> combinationAttrValues = storeProductAttrValueService.getByEntity(apAttrValuePram);
        StoreProductAttrValue existSPAttrValue = new StoreProductAttrValue();
        if(CollUtil.isNotEmpty(combinationAttrValues)) existSPAttrValue = combinationAttrValues.get(0);
        if(ObjectUtil.isEmpty(existSPAttrValue)) throw new CrmebException("请选择有效的商品属性");
        if(existSPAttrValue.getStock() <= 0) throw new CrmebException("该商品库存不足");

        spcpInfo.setAttrInfo(existSPAttrValue);
        spcpInfo.setStoreInfo(storeCombination.getInfo());
        spcpInfo.setStoreName(storeCombination.getTitle());

        storeCartResponse.setCombinationId(storeCartPram.getCombinationId());
        storeCartResponse.setPinkId(Optional.ofNullable(storeCartPram.getPinkId()).orElse(0));
        storeCartResponse.setProductInfo(spcpInfo);
        storeCartResponse.setTrueStock(storeCartResponse.getProductInfo().getAttrInfo().getStock());
        storeCartResponse.setCostPrice(storeCartResponse.getProductInfo().getAttrInfo().getCost());
        storeCartResponse.setTruePrice(storeCombination.getPrice());
        storeCartResponse.setVipTruePrice(BigDecimal.ZERO);

        storeCartResponse.setType(Constants.PRODUCT_TYPE_PINGTUAN+"");// 砍价=3
        storeCartResponse.setProductId(storeCartPram.getProductId());
        storeCartResponse.setProductAttrUnique(storeCartPram.getProductAttrUnique());
        storeCartResponse.setCartNum(storeCartPram.getCartNum());
        storeCartResponses.add(storeCartResponse);

        cacheIdsResult.add(orderUtils.setCacheOrderData(currentUser, storeCartResponses));
        return cacheIdsResult;
    }
}

