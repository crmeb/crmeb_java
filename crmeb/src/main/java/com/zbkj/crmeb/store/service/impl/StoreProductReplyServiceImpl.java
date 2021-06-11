package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.bargain.model.StoreBargain;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.front.response.ProductDetailReplyResponse;
import com.zbkj.crmeb.front.response.ProductReplyResponse;
import com.zbkj.crmeb.seckill.model.StoreSeckill;
import com.zbkj.crmeb.store.dao.StoreProductReplyDao;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductReply;
import com.zbkj.crmeb.store.request.StoreProductReplyAddRequest;
import com.zbkj.crmeb.store.request.StoreProductReplySearchRequest;
import com.zbkj.crmeb.store.response.StoreProductReplyResponse;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreProductReplyService;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StoreProductReplyServiceImpl 接口实现
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
public class StoreProductReplyServiceImpl extends ServiceImpl<StoreProductReplyDao, StoreProductReply>
        implements StoreProductReplyService {

    @Resource
    private StoreProductReplyDao dao;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TransactionTemplate transactionTemplate;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-27
    * @return List<StoreProductReply>
    */
    @Override
    public PageInfo<StoreProductReplyResponse> getList(StoreProductReplySearchRequest request, PageParamRequest pageParamRequest) {
        Page<StoreProductReply> pageStoreReply = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductReply 类的多条件查询
        LambdaQueryWrapper<StoreProductReply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(null != request.getIsDel()){
            lambdaQueryWrapper.eq(StoreProductReply::getIsDel, request.getIsDel());
        }
        if(null != request.getIsReply()){
            lambdaQueryWrapper.eq(StoreProductReply::getIsReply, request.getIsReply());
        }
        if(null != request.getOid()){
            lambdaQueryWrapper.eq(StoreProductReply::getOid, request.getOid());
        }
        if(!StringUtils.isBlank(request.getProductId())){
            lambdaQueryWrapper.in(StoreProductReply::getProductId, CrmebUtil.stringToArray(request.getProductId()));
        }
        if(StrUtil.isNotBlank(request.getProductSearch())){
            IndexStoreProductSearchRequest storeProductPram = new IndexStoreProductSearchRequest();
            storeProductPram.setKeywords(request.getProductSearch());
            List<StoreProduct> storeProducts = storeProductService.getList(storeProductPram, new PageParamRequest());
            List<Integer> productIds = storeProducts.stream().map(StoreProduct::getId).collect(Collectors.toList());
            if(productIds.size() > 0){
                lambdaQueryWrapper.in(StoreProductReply::getProductId, productIds);
            }
        }
        if(!StringUtils.isBlank(request.getUid())){
            lambdaQueryWrapper.in(StoreProductReply::getUid, CrmebUtil.stringToArray(request.getUid()));
        }
        if(StringUtils.isNotBlank(request.getNickname())){
            lambdaQueryWrapper.like(StoreProductReply::getNickname,request.getNickname());
        }
        //评价等级|0=全部,1=好评,2=中评,3=差评
        List<Integer> typeList = new ArrayList<>();
        switch (request.getType()){
            case 1:
                typeList.add(5);
                typeList.add(4);
                break;
            case 2:
                typeList.add(3);
                break;
            case 3:
                typeList.add(2);
                typeList.add(1);
                break;
            default:
               break;

        }
        if(typeList.size() > 0 ){
            lambdaQueryWrapper.in(StoreProductReply::getProductScore, typeList);
        }

        if(request.getStar() > 0){
            lambdaQueryWrapper.eq(StoreProductReply::getProductScore, request.getStar());
        }

        if(StringUtils.isNotBlank(request.getDateLimit())){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            lambdaQueryWrapper.between(StoreProductReply::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lambdaQueryWrapper.orderByDesc(StoreProductReply::getId);
        List<StoreProductReply> dataList = dao.selectList(lambdaQueryWrapper);
        List<StoreProductReplyResponse> dataResList = new ArrayList<>();
        for (StoreProductReply productReply : dataList) {
            StoreProductReplyResponse productReplyResponse = new StoreProductReplyResponse();
            BeanUtils.copyProperties(productReply, productReplyResponse);
            StoreProduct storeProduct = storeProductService.getById(productReply.getProductId());
            productReplyResponse.setStoreProduct(storeProduct);
            productReplyResponse.setPics(CrmebUtil.stringToArrayStr(productReply.getPics()));
            dataResList.add(productReplyResponse);
        }
        return CommonPage.copyPageInfo(pageStoreReply, dataResList);
    }

    /**
     * 商品分数
     * @return Integer
     */
    @Override
    public Integer getSumStar(Integer productId) {
        QueryWrapper<StoreProductReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(sum(product_score),0) as product_score", "IFNULL(sum(service_score),0) as service_score");
        queryWrapper.eq("is_del", 0);
        queryWrapper.eq("product_id", productId);
        StoreProductReply storeProductReply = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(storeProductReply)){
            return 0;
        }
        if (storeProductReply.getProductScore() == 0 || storeProductReply.getServiceScore() == 0) {
            return 0;
        }
        // 星数 = （商品评星 + 服务评星） / 2
        BigDecimal sumScore = new BigDecimal(storeProductReply.getProductScore() + storeProductReply.getServiceScore());
        BigDecimal divide = sumScore.divide(BigDecimal.valueOf(2L), 0, BigDecimal.ROUND_DOWN);
        return divide.intValue();
    }

    /**
     * 创建订单商品评价
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean create(StoreProductReplyAddRequest request) {
        User user = userService.getInfoException();
        StoreOrder storeOrder = storeOrderService.getByOderId(request.getOrderNo());
        if (ObjectUtil.isNull(storeOrder) || !storeOrder.getUid().equals(user.getUid())) {
            throw new CrmebException("该订单不存在");
        }
        StoreProductReply storeProductReply = new StoreProductReply();
        BeanUtils.copyProperties(request, storeProductReply);
        storeProductReply.setOid(storeOrder.getId());
        Integer count = checkIsReply(storeProductReply);
        storeProductReply.setAvatar(systemAttachmentService.clearPrefix(user.getAvatar()));
        storeProductReply.setNickname(user.getNickname());
        if(StringUtils.isNotBlank(request.getPics())){
            String pics = request.getPics().replace("[\"","").replace("\"]","")
                    .replace("\"","");
            storeProductReply.setPics(systemAttachmentService.clearPrefix(ArrayUtils.toString(pics)));
        }
        Boolean execute = transactionTemplate.execute(e -> {
            boolean result = save(storeProductReply);
            //修改订单信息
            completeOrder(storeProductReply, count, storeOrder);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("评价订单失败");
        }
        return execute;
    }

    /**
     * 添加虚拟评论
     * @param request 评论参数
     * @return 评论结果
     */
    @Override
    public boolean virtualCreate(StoreProductReplyAddRequest request) {
        StoreProductReply storeProductReply = new StoreProductReply();
        BeanUtils.copyProperties(request, storeProductReply);
        if(StringUtils.isNotBlank(request.getPics())){
            String pics = request.getPics()
                    .replace("[","")
                    .replace("]","")
                    .replace("\"","");
            storeProductReply.setPics(systemAttachmentService.clearPrefix(ArrayUtils.toString(pics)));
        }
        storeProductReply.setAvatar(systemAttachmentService.clearPrefix(storeProductReply.getAvatar()));
        storeProductReply.setUnique(CrmebUtil.randomCount(11111,9999)+"");
        return save(storeProductReply);
    }

    /**
     * 订单是否已回复
     * @param unique 订单id
     * @param replayType 类型
     * @return 回复内容
     */
    @Override
    public List<StoreProductReply> isReply(String unique, String replayType, Integer orderId) {
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getUnique, unique);
        lqw.eq(StoreProductReply::getReplyType, replayType);
        lqw.eq(StoreProductReply::getOid, orderId);
        return dao.selectList(lqw);
    }

    /**
     * 订单是否已回复
     * @param unique skuId
     * @param orderId 订单id
     * @return 回复内容
     */
    @Override
    public Boolean isReply(String unique, Integer orderId) {
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getUnique, unique);
        lqw.eq(StoreProductReply::getOid, orderId);
        List<StoreProductReply> replyList = dao.selectList(lqw);
        if (CollUtil.isEmpty(replyList)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 商品评论列表
     * @param productId     商品ID
     * @param type          商品类型
     * @return
     */
    @Override
    public List<StoreProductReply> getAllByPidAndType(Integer productId, String type) {
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getProductId, productId);
        lqw.eq(StoreProductReply::getReplyType, type);
        lqw.eq(StoreProductReply::getIsDel, false);
        lqw.orderByDesc(StoreProductReply::getId);
        return dao.selectList(lqw);
    }

    /**
     * H5商品评论统计
     * @param productId 商品编号
     * @return MyRecord
     */
    @Override
    public MyRecord getH5Count(Integer productId) {
        // 评论总数
        Integer sumCount = getCountByScore(productId, "all");
        // 好评总数
        Integer goodCount = getCountByScore(productId, "good");
        // 中评总数
        Integer mediumCount = getCountByScore(productId, "medium");
        // 差评总数
        Integer poorCount = getCountByScore(productId, "poor");
        // 好评率
        String replyChance = "0";
        if(sumCount > 0 && goodCount > 0){
            replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
        }
        // 评分星数(商品评星 + 服务评星)/2
        Integer replyStar = 0;
        if (sumCount > 0) {
            replyStar = getSumStar(productId);

        }
        MyRecord record = new MyRecord();
        record.set("sumCount", sumCount);
        record.set("goodCount", goodCount);
        record.set("mediumCount", mediumCount);
        record.set("poorCount", poorCount);
        record.set("replyChance", replyChance);
        record.set("replyStar", replyStar);
        return record;
    }

    /**
     * H5商品详情评论信息
     * @param proId 商品编号
     * @return ProductDetailReplyResponse
     */
    @Override
    public ProductDetailReplyResponse getH5ProductReply(Integer proId) {
        ProductDetailReplyResponse response = new ProductDetailReplyResponse();

        // 评论总数
        Integer sumCount = getCountByScore(proId, "all");
        if (sumCount.equals(0)) {
            response.setSumCount(0);
            response.setReplyChance("0");
            return response;
        }
        // 好评总数
        Integer goodCount = getCountByScore(proId, "good");
        // 好评率
        String replyChance = "0";
        if(sumCount > 0 && goodCount > 0){
            replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
        }

        // 查询最后一条评论
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getProductId, proId);
        lqw.eq(StoreProductReply::getIsDel, false);
        lqw.orderByDesc(StoreProductReply::getId);
        lqw.last(" limit 1");
        StoreProductReply storeProductReply = dao.selectOne(lqw);
        ProductReplyResponse productReplyResponse = new ProductReplyResponse();
        BeanUtils.copyProperties(storeProductReply, productReplyResponse);
        // 评价图
        productReplyResponse.setPics(CrmebUtil.stringToArrayStr(storeProductReply.getPics()));
        // 昵称
        String nickname = storeProductReply.getNickname();
        if (StrUtil.isNotBlank(nickname)) {
            if (nickname.length() == 1) {
                nickname = nickname.concat("**");
            } else if (nickname.length() == 2) {
                nickname = nickname.substring(0, 1) + "**";
            } else {
                nickname = nickname.substring(0, 1) + "**" + nickname.substring(nickname.length() - 1);
            }
            productReplyResponse.setNickname(nickname);
        }
        // 星数 = （商品评星 + 服务评星） / 2
        BigDecimal sumScore = new BigDecimal(storeProductReply.getProductScore() + storeProductReply.getServiceScore());
        BigDecimal divide = sumScore.divide(BigDecimal.valueOf(2L), 0, BigDecimal.ROUND_DOWN);
        productReplyResponse.setScore(divide.intValue());

        response.setSumCount(sumCount);
        response.setReplyChance(replyChance);
        response.setProductReply(productReplyResponse);
        return response;
    }

    /**
     * 移动端商品评论列表
     * @param proId 商品编号
     * @param type 评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return PageInfo<ProductReplyResponse>
     */
    @Override
    public PageInfo<ProductReplyResponse> getH5List(Integer proId, Integer type, PageParamRequest pageParamRequest) {
        Page<StoreProductReply> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductReply 类的多条件查询
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getIsDel, false);
        lqw.eq(StoreProductReply::getProductId, proId);
        //评价等级|0=全部,1=好评,2=中评,3=差评
        List<Integer> typeList = new ArrayList<>();
        switch (type){
            case 1:
                lqw.apply(" (product_score + service_score) >= 8");
                break;
            case 2:
                lqw.apply(" (product_score + service_score) < 8 and (product_score + service_score) > 4");
                break;
            case 3:
                lqw.apply(" (product_score + service_score) <= 4");
                break;
            default:
                break;

        }
        lqw.orderByDesc(StoreProductReply::getId);
        List<StoreProductReply> replyList = dao.selectList(lqw);
        List<ProductReplyResponse> responseList = new ArrayList<>();
        for (StoreProductReply productReply : replyList) {
            ProductReplyResponse productReplyResponse = new ProductReplyResponse();
            BeanUtils.copyProperties(productReply, productReplyResponse);
            // 评价图
            productReplyResponse.setPics(CrmebUtil.stringToArrayStr(productReply.getPics()));
            // 昵称
            String nickname = productReply.getNickname();
            if (StrUtil.isNotBlank(nickname)) {
                if (nickname.length() == 1) {
                    nickname = nickname.concat("**");
                } else if (nickname.length() == 2) {
                    nickname = nickname.substring(0, 1) + "**";
                } else {
                    nickname = nickname.substring(0, 1) + "**" + nickname.substring(nickname.length() - 1);
                }
                productReplyResponse.setNickname(nickname);
            }
            // 星数 = （商品评星 + 服务评星） / 2
            BigDecimal sumScore = new BigDecimal(productReply.getProductScore() + productReply.getServiceScore());
            BigDecimal divide = sumScore.divide(BigDecimal.valueOf(2L), 0, BigDecimal.ROUND_DOWN);
            productReplyResponse.setScore(divide.intValue());

            responseList.add(productReplyResponse);
        }
        return CommonPage.copyPageInfo(startPage, responseList);
    }

    // 获取统计数据（好评、中评、差评）
    private Integer getCountByScore(Integer productId, String type) {
        LambdaQueryWrapper<StoreProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductReply::getProductId, productId);
        lqw.eq(StoreProductReply::getIsDel, false);

        switch (type) {
            case "all":
                break;
            case "good":
                lqw.apply( " (product_score + service_score) >= 8");
//                lqw.in(StoreProductReply::getProductScore, 4, 5);
                break;
            case "medium":
                lqw.apply( " (product_score + service_score) < 8 and (product_score + service_score) > 4");
//                lqw.eq(StoreProductReply::getProductScore, 3);
                break;
            case "poor":
                lqw.apply( " (product_score + service_score) <= 4");
//                lqw.in(StoreProductReply::getProductScore, 2, 1);
                break;
        }
        return dao.selectCount(lqw);
    }


    private StoreOrder getOrder(StoreProductReply storeProductReply) {
        //订单信息
        StoreOrder storeOrder = new StoreOrder();
        storeOrder.setId(storeProductReply.getOid());
        storeOrder.setUid(storeProductReply.getUid());
        storeOrder = storeOrderService.getInfoByEntity(storeOrder);
        if(null == storeOrder){
            throw new CrmebException("订单信息不存在");
        }
        return storeOrder;
    }

    /**
     * 如果所有的都已评价，那么订单完成
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return Integer
     */
    private void completeOrder(StoreProductReply storeProductReply, Integer count, StoreOrder storeOrder) {
        Integer replyCount = getReplyCountByEntity(storeProductReply, true);

        if(replyCount.equals(count)){
            //全部商品都已评价
            storeOrder.setStatus(Constants.ORDER_STATUS_INT_COMPLETE);
            storeOrderService.updateById(storeOrder);
            redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER, storeOrder.getId());
        }
    }

    /**
     * 检测当前商品是否可以评论
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return Integer
     */
    private Integer checkIsReply(StoreProductReply storeProductReply) {

        //查看商品信息
        List<StoreOrderInfoOldVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(storeProductReply.getOid());
        if(null == orderInfoVoList || orderInfoVoList.size() < 1){
            throw new CrmebException("没有找到商品信息");
        }

        boolean findResult = false;
        for (StoreOrderInfoOldVo orderInfoVo : orderInfoVoList) {
//            Integer productId = orderInfoVo.getInfo().getInteger("product_id");
            Integer productId = orderInfoVo.getInfo().getProductId();
            if(productId < 1){
                continue;
            }

            if(storeProductReply.getProductId().equals(productId)){
                findResult = true;
                break;
            }
        }

        if(!findResult){
            throw new CrmebException("没有找到商品信息");
        }

        //商品是否已评价
        Integer replyCount = getReplyCountByEntity(storeProductReply, false);
        if(replyCount > 0){
            throw new CrmebException("该商品已评价");
        }

        return orderInfoVoList.size();
    }

    /**
     * 根据商品id  订单id  用户id 获取评论信息
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return Integer
     */
    private Integer getReplyCountByEntity(StoreProductReply request, boolean isAll) {
        LambdaQueryWrapper<StoreProductReply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProductReply::getOid, request.getOid());
//        .eq(StoreProductReply::getUnique, request.getUnique());
        if(null != request.getUid()){
            lambdaQueryWrapper.eq(StoreProductReply::getUid, request.getUid());
        }
        if(!isAll){
            lambdaQueryWrapper.eq(StoreProductReply::getProductId, request.getProductId());

        }
        return dao.selectCount(lambdaQueryWrapper);
    }

}

