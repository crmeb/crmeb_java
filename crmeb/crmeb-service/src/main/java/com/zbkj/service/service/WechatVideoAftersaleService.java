package com.zbkj.service.service;


import com.zbkj.common.vo.ShopAftersaleAddVo;
import com.zbkj.common.vo.ShopAftersaleUpdateVo;
import com.zbkj.common.vo.ShopAftersaleVo;
import com.zbkj.common.vo.ShopOrderCommonVo;

/**
 * 视频号交易组件服务——售后部分
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface WechatVideoAftersaleService {

    /**
     * 创建售后
     * @return Boolean
     */
    Boolean shopAftersaleAdd(ShopAftersaleAddVo shopAftersaleAddVo);

    /**
     * 获取售后
     * @return ShopAftersaleVo
     */
    ShopAftersaleVo shopAftersaleGet(ShopOrderCommonVo shopOrderCommonVo);

    /**
     * 更新售后
     * @return Boolean
     */
    Boolean shopAftersaleUpdate(ShopAftersaleUpdateVo shopAftersaleUpdateVo);
}
