package com.zbkj.crmeb.wechat.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.wechat.model.TemplateMessage;
import com.zbkj.crmeb.wechat.request.TemplateMessageSearchRequest;
import com.zbkj.crmeb.wechat.vo.TemplateMessageIndustryVo;

import java.util.HashMap;
import java.util.List;

/**
 *  TemplateMessageService 接口
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
public interface TemplateMessageService extends IService<TemplateMessage> {

    List<TemplateMessage> getList(TemplateMessageSearchRequest request, PageParamRequest pageParamRequest);

    void push(String tempKey, HashMap<String, String> map, Integer userId, String type);

    TemplateMessage infoException(Integer id);

    void consumePublic();

    void consumeProgram();

    TemplateMessageIndustryVo getIndustry();
}