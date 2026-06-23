package com.zbkj.service.service;


import com.zbkj.common.vo.*;

import java.util.List;

/**
 * 视频号交易组件服务——商品部分
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
public interface WechatVideoSpuService {

    /**
     * 获取商品类目详情
     * @return List<ShopCatDetailVo>
     */
    List<ShopCatDetailVo> getShopCat();

    /**
     * 获取品牌列表
     * @return List<ShopBrandVo>
     */
    List<ShopBrandVo> getShopBrandList();

    /**
     * 添加商品
     * 若无品牌可指定无品牌(无品牌brand_id: 2100000000)
     * @return ShopSpuAddResponseVo
     */
    ShopSpuAddResponseVo shopSpuAdd(ShopSpuAddVo shopSpuVo);

    /**
     * 删除商品
     * @return Boolean
     */
    Boolean shopSpuDel(ShopSpuCommonVo shopSpuCommonVo);

    /**
     * 撤回商品审核
     * 对于审核中（edit_status=2）的商品无法重复提交，需要调用此接口，使商品流转进入未审核的状态（edit_status=1）,即可重新提交商品。
     */
    Boolean shopSpuDelAudit(ShopSpuCommonVo shopSpuCommonVo);

    /**
     * 获取商品
     * @return ShopSpuVo
     */
    ShopSpuVo shopSpuGet(ShopSpuCommonVo shopSpuCommonVo);

    /**
     * 获取商品列表
     * @return ShopSpuListResponseVo
     */
    ShopSpuListResponseVo shopSpuGetList(ShopSpuListRequestVo shopSpuListRequestVo);

    /**
     * 更新商品
     * 注意：更新成功后会更新到草稿数据并直接提交审核，审核完成后有回调，也可通过get接口的edit_status查看是否通过审核。
     * @return ShopSpuAddResponseVo
     */
    ShopSpuAddResponseVo shopSpuUpdate(ShopSpuAddVo shopSpuVo);

    /**
     * 上架商品
     * @return Boolean
     */
    Boolean shopSpuListing(ShopSpuCommonVo shopSpuCommonVo);

    /**
     * 下架商品
     * @return Boolean
     */
    Boolean shopSpuDelisting(ShopSpuCommonVo shopSpuCommonVo);


}
