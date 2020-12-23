package com.zbkj.crmeb.wechat.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.model.WechatQrcode;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.wechat.request.WechatQrcodeSearchRequest;

import java.util.List;

/**
 *  WechatQrcodeService 接口
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
public interface WechatQrcodeService extends IService<WechatQrcode> {

    List<WechatQrcode> getList(WechatQrcodeSearchRequest request, PageParamRequest pageParamRequest);
}