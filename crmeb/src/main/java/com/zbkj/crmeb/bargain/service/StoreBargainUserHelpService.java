package com.zbkj.crmeb.bargain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.bargain.model.StoreBargainUserHelp;
import com.zbkj.crmeb.bargain.response.StoreBargainUserHelpResponse;
import com.zbkj.crmeb.front.request.BargainFrontRequest;
import com.zbkj.crmeb.front.response.BargainCountResponse;

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

    List<StoreBargainUserHelpResponse> getList(Integer bargainUserId);

    /**
     * 获取帮忙砍价好友数量
     * @param bargainId
     * @return
     */
    Long getHelpCountByBargainId(Integer bargainId);

    /**
     * 获取帮忙砍价好友数量
     * @param bargainId
     * @param bargainUid
     * @return
     */
    Long getHelpCountByBargainIdAndBargainUid(Integer bargainId, Integer bargainUid);

    /**
     * H5 获取帮忙好友砍价数据
     * @param bargainFrontRequest
     * @return
     */
    BargainCountResponse getH5CountByBargainId(BargainFrontRequest bargainFrontRequest);

    /**
     * H5 帮忙好友砍价信息列表
     * @param bargainFrontRequest
     * @param pageParamRequest
     * @return
     */
    PageInfo<StoreBargainUserHelpResponse> getHelpList(BargainFrontRequest bargainFrontRequest, PageParamRequest pageParamRequest);

    /**
     * 砍价
     * @param bargainFrontRequest
     * @return
     */
    Map<String, Object> help(BargainFrontRequest bargainFrontRequest);

    /**
     * 获取参与砍价人员数量
     */
    Long getHelpPeopleCount();

    /**
     * 获取参与砍价人员数量
     */
    Long getHelpPeopleCountByBargainId(Integer bargainId);

    /**
     * 获取用户还剩余的砍价金额
     * @param bargainId 砍价商品编号
     * @param bargainUserUid 砍价发起用户uid
     * @return
     */
    BigDecimal getSurplusPrice(Integer bargainId, Integer bargainUserUid);

    /**
     * 砍价发起用户信息
     * @param bargainFrontRequest
     * @return
     */
    Map<String, String> startUser(BargainFrontRequest bargainFrontRequest);
}