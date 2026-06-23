package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.wechat.video.PayComponentCat;
import com.zbkj.common.vo.CatItem;

import java.util.List;

/**
 *
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
public interface PayComponentCatService extends IService<PayComponentCat> {

    /**
     * 自动更新自定义交易组件类目
     */
    void autoUpdate();

    /**
     * 获取类目
     * @return List<FirstCatVo>
     */
    List<CatItem> getList();

    /**
     * 根据第三级id获取类目
     * @param thirdCatId 第三级id
     * @return PayComponentCat
     */
    PayComponentCat getByThirdCatId(Integer thirdCatId);
}
