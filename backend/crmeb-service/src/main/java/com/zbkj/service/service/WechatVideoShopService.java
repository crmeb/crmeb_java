package com.zbkj.service.service;


import com.zbkj.common.vo.BaseResultResponseVo;
import com.zbkj.common.vo.RegisterCheckResponseVo;

/**
 * 视频号交易组件服务——商家入驻部分
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
public interface WechatVideoShopService {

    /**
     * 接入申请
     * @return 接入结果
     */
    BaseResultResponseVo shopRegisterApply();

    /**
     * 获取接入状态
     * @return 接入状态结果
     */
    RegisterCheckResponseVo shopRegisterCheck();
}
