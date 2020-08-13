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
* @author Mr.Zhang
* @Description UserSignService 接口
* @since 2020-04-30
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