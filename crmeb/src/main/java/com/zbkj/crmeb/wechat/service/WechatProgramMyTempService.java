package com.zbkj.crmeb.wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.model.WechatProgramMyTemp;
import com.zbkj.crmeb.wechat.request.WechatProgramMyTempRequest;
import com.zbkj.crmeb.wechat.request.WechatProgramMyTempSearchRequest;

import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @description WechatProgramMyTempService 接口
* @date 2020-08-28
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