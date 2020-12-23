package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.request.UserSignInfoRequest;
import com.zbkj.crmeb.front.response.UserSignInfoResponse;
import com.zbkj.crmeb.system.vo.SystemGroupDataSignConfigVo;
import com.zbkj.crmeb.user.model.UserSign;
import com.zbkj.crmeb.user.vo.UserSignMonthVo;
import com.zbkj.crmeb.user.vo.UserSignVo;

import java.util.HashMap;
import java.util.List;

/**
 * UserSignService 接口实现
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
public interface UserSignService extends IService<UserSign> {

    List<UserSignVo> getList(PageParamRequest pageParamRequest);

    List<UserSign> getListByCondition(UserSign sign,PageParamRequest pageParamRequest);

    SystemGroupDataSignConfigVo sign();

    HashMap<String, Object> get();

    List<SystemGroupDataSignConfigVo> config();

    List<UserSignMonthVo> getListGroupMonth(PageParamRequest pageParamRequest);

    UserSignInfoResponse getUserInfo(UserSignInfoRequest request);
}