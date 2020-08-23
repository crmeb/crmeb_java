package com.zbkj.crmeb.store.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.store.dao.StoreProductReplyDao;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductReply;
import com.zbkj.crmeb.store.request.StoreProductReplyAddRequest;
import com.zbkj.crmeb.store.request.StoreProductReplySearchRequest;
import com.zbkj.crmeb.store.response.StoreProductReplyResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Mr.Zhang
* @description StoreProductReplyServiceImpl 接口实现
* @date 2020-05-27
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
        if(null != request.getProductSearch()){
            IndexStoreProductSearchRequest storeProductPram = new IndexStoreProductSearchRequest();
            storeProductPram.setKeywords(request.getProductSearch());
            List<StoreProduct> storeProducts = storeProductService.getList(storeProductPram, new PageParamRequest());
            List<Integer> productIds = storeProducts.stream().map(StoreProduct::getId).collect(Collectors.toList());
            lambdaQueryWrapper.in(!productIds.isEmpty(), StoreProductReply::getProductId, productIds);
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
            Date ds = DateUtil.strToDate(dateLimit.getStartTime(), Constants.DATE_FORMAT_DATE);
            Date de = DateUtil.strToDate(dateLimit.getEndTime(), Constants.DATE_FORMAT_DATE);
            lambdaQueryWrapper.between(StoreProductReply::getMerchantReplyTime,
                    DateUtil.getSecondTimestamp(ds),
                    DateUtil.getSecondTimestamp(de));
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
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return Integer
     */
    @Override
    public Integer getSumStar(Integer productId) {
        QueryWrapper<StoreProductReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(product_score) as product_score").eq("is_del", 0).eq("product_id", productId);
        StoreProductReply storeProductReply = dao.selectOne(queryWrapper);
        if(null == storeProductReply){
            return 0;
        }
        return storeProductReply.getProductScore();
    }

    /**
     * 评论
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return Integer
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public boolean create(StoreProductReplyAddRequest request) {
        try{
            StoreProductReply storeProductReply = new StoreProductReply();
            BeanUtils.copyProperties(request, storeProductReply);

            StoreOrder order = getOrder(storeProductReply);
            Integer count = checkIsReply(storeProductReply);

            //获取用户信息
            User user = userService.getInfo();
            storeProductReply.setAvatar(user.getAvatar());
            storeProductReply.setNickname(user.getNickname());
            if(StringUtils.isNotBlank(request.getPics())){
                String pics = request.getPics().replace("[\"","").replace("\"]","")
                        .replace("\"","");
                storeProductReply.setPics(systemAttachmentService.clearPrefix(ArrayUtils.toString(pics)));
            }
//            storeProductReply.setPics( ArrayUtils.toString(request.getPics()));
            boolean result = save(storeProductReply);

            //修改订单信息
            completeOrder(storeProductReply, count, order);

            return result;
        }catch (Exception e){
            throw new CrmebException("评价失败" + e.getMessage());
        }
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
        }
        redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER, storeOrder.getId());
    }

    /**
     * 检测当前商品是否可以评论
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return Integer
     */
    private Integer checkIsReply(StoreProductReply storeProductReply) {

        //查看商品信息
        List<StoreOrderInfoVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(storeProductReply.getOid());
        if(null == orderInfoVoList || orderInfoVoList.size() < 1){
            throw new CrmebException("没有找到商品信息");
        }

        boolean findResult = false;
        for (StoreOrderInfoVo orderInfoVo : orderInfoVoList) {
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
        lambdaQueryWrapper.eq(StoreProductReply::getOid, request.getOid())
        .eq(StoreProductReply::getUnique, request.getUnique());
        if(null != request.getUid()){
            lambdaQueryWrapper.eq(StoreProductReply::getUid, request.getUid());
        }
        if(!isAll){
            lambdaQueryWrapper.eq(StoreProductReply::getProductId, request.getProductId());

        }
        return dao.selectCount(lambdaQueryWrapper);
    }

}

