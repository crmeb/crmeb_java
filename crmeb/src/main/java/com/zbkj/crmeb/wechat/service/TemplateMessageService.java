package com.zbkj.crmeb.wechat.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.wechat.model.TemplateMessage;
import com.zbkj.crmeb.wechat.request.TemplateMessageSearchRequest;
import com.zbkj.crmeb.wechat.vo.TemplateMessageIndustryVo;

import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @description TemplateMessageService 接口
* @date 2020-06-03
*/
public interface TemplateMessageService extends IService<TemplateMessage> {

    List<TemplateMessage> getList(TemplateMessageSearchRequest request, PageParamRequest pageParamRequest);

    void push(String tempKey, HashMap<String, String> map, Integer userId, String type);

    TemplateMessage infoException(Integer id);

    void consumePublic();

    void consumeProgram();

    TemplateMessageIndustryVo getIndustry();
}