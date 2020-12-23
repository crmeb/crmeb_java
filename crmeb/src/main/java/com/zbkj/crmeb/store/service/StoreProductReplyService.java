package com.zbkj.crmeb.store.service;

import com.common.MyRecord;
import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.store.model.StoreProductReply;
import com.zbkj.crmeb.store.request.StoreProductReplyAddRequest;
import com.zbkj.crmeb.store.request.StoreProductReplySearchRequest;
import com.zbkj.crmeb.store.response.StoreProductReplyResponse;

import java.util.List;

/**
 * StoreProductReplyService 接口
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
public interface StoreProductReplyService extends IService<StoreProductReply> {

    PageInfo<StoreProductReplyResponse> getList(StoreProductReplySearchRequest request, PageParamRequest pageParamRequest);

    Integer getSumStar(Integer productId);

    boolean create(StoreProductReplyAddRequest request);

    /**
     * 添加虚拟评论
     * @param request 评论参数
     * @return 评论结果
     */
    boolean virtualCreate(StoreProductReplyAddRequest request);

    /**
     * 查询是否已经回复
     * @param unique
     * @param replayType
     * @return
     */
    List<StoreProductReply> isReply(String unique,String replayType, Integer orderId);

    /**
     * 获取商品评论列表
     * @param productId     商品ID
     * @param type          商品类型
     * @return
     */
    List<StoreProductReply> getAllByPidAndType(Integer productId, String type);

    /**
     * H5商品评论统计
     * @param productId 商品编号
     * @return
     */
    MyRecord getH5Count(Integer productId);

}
