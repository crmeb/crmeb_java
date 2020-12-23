package com.zbkj.crmeb.wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.model.WechatProgramMyTemp;
import com.zbkj.crmeb.wechat.request.WechatProgramMyTempRequest;
import com.zbkj.crmeb.wechat.request.WechatProgramMyTempSearchRequest;

import java.util.HashMap;
import java.util.List;

/**
 *  WechatProgramMyTempService 接口
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
public interface WechatProgramMyTempService extends IService<WechatProgramMyTemp> {

    List<WechatProgramMyTemp> getList(WechatProgramMyTempSearchRequest request, PageParamRequest pageParamRequest);

    WechatProgramMyTemp info(Integer id);

    boolean updateStatus(Integer id, boolean status);

    boolean create(WechatProgramMyTempRequest wechatProgramMyTempRequest);

    boolean update(Integer id, WechatProgramMyTempRequest wechatProgramMyTempRequest);

    void push(int myTempId, HashMap<String, String> map, Integer userId);

    void async(WechatProgramMyTemp wechatProgramMyTemp);

    List<WechatProgramMyTemp> getAll();

    boolean updateType(Integer id, String type);
}