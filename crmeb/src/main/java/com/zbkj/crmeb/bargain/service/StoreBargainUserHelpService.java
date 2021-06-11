package com.zbkj.crmeb.bargain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.bargain.model.StoreBargainUserHelp;
import com.zbkj.crmeb.bargain.response.StoreBargainUserHelpResponse;
import com.zbkj.crmeb.front.request.BargainFrontRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户帮助砍价 Service
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
public interface StoreBargainUserHelpService extends IService<StoreBargainUserHelp> {

    /**
     * 列表
     * @param bargainUserId 砍价活动id
     * @return List<StoreBargainUserHelpResponse>
     */
    List<StoreBargainUserHelpResponse> getList(Integer bargainUserId);

    /**
     * 获取帮忙砍价好友数量
     * @param bargainId 砍价商品Id
     * @return Long
     */
    Long getHelpCountByBargainId(Integer bargainId);

    /**
     * 获取帮忙砍价好友数量
     * @param bargainId 砍价商品Id
     * @param bargainUid 砍价用户uid
     * @return Long
     */
    Long getHelpCountByBargainIdAndBargainUid(Integer bargainId, Integer bargainUid);

    /**
     * 砍价
     * @param bargainFrontRequest 砍价请求参数
     * @return Map<String, Object>
     */
    Map<String, Object> help(BargainFrontRequest bargainFrontRequest);

    /**
     * 获取用户还剩余的砍价金额
     * @param bargainId 砍价商品编号
     * @param bargainUserUid 砍价发起用户uid
     * @return BigDecimal
     */
    BigDecimal getSurplusPrice(Integer bargainId, Integer bargainUserUid);

    /**
     * 获取参与砍价总人数（次）
     * @return Integer
     */
    Integer getCount();

    /**
     * 获取好友助力列表
     * @param bargainUserId 砍价用户表id
     * @return List<StoreBargainUserHelp>
     */
    List<StoreBargainUserHelp> getHelpListByBargainUserId(Integer bargainUserId);

    /**
     * 是否帮砍过
     * @param bargainUserId 用户砍价活动id
     * @param uid 用户uid
     * @return Boolean
     */
    Boolean getIsHelp(Integer bargainUserId, Integer uid);

    /**
     * 获取该砍价商品用户的帮砍次数
     * @param bargainId 砍价商品id
     * @param uid 用户uid
     * @param bargainUserIdList 用户参与砍价活动id数组
     * @return Integer
     */
    Integer getHelpCountByBargainIdAndUidInBUserId(Integer bargainId, Integer uid, List<Integer> bargainUserIdList);
}