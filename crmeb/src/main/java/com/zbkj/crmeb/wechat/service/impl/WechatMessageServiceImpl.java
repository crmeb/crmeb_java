package com.zbkj.crmeb.wechat.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;

import com.zbkj.crmeb.wechat.model.WechatMessage;
import com.zbkj.crmeb.wechat.dao.WechatMessageDao;
import com.zbkj.crmeb.wechat.request.WechatMessageSearchRequest;
import com.zbkj.crmeb.wechat.service.WechatMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description WechatMessageServiceImpl 接口实现
* @since 2020-04-18
*/
@Service
public class WechatMessageServiceImpl extends ServiceImpl<WechatMessageDao, WechatMessage> implements WechatMessageService {

    @Resource
    private WechatMessageDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-18
    * @return List<WechatMessage>
    */
    @Override
    public List<WechatMessage> getList(WechatMessageSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

}

