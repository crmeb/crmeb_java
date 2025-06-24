package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.model.wechat.WechatProgramMyTemp;
import com.zbkj.common.request.WechatProgramMyTempSearchRequest;

import java.util.HashMap;
import java.util.List;

/**
 *  WechatProgramMyTempService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface WechatProgramMyTempService extends IService<WechatProgramMyTemp> {

    List<WechatProgramMyTemp> getList(WechatProgramMyTempSearchRequest request, PageParamRequest pageParamRequest);

    void push(int myTempId, HashMap<String, String> map, Integer userId);

}
