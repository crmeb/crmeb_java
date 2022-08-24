package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.model.wechat.WechatReply;
import com.zbkj.common.request.WechatReplyRequest;
import com.zbkj.common.request.WechatReplySearchRequest;
import com.zbkj.service.dao.WechatReplyDao;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.WechatReplyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * WechatReplyServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class WechatReplyServiceImpl extends ServiceImpl<WechatReplyDao, WechatReply> implements WechatReplyService {

    @Resource
    private WechatReplyDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<WechatReply>
    */
    @Override
    public List<WechatReply> getList(WechatReplySearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<WechatReply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //类型
        if (StringUtils.isNotBlank(request.getType())) {
            lambdaQueryWrapper.eq(WechatReply::getType, request.getType());
        }
        //关键字
        if (StringUtils.isNotBlank(request.getKeywords())) {
            lambdaQueryWrapper.eq(WechatReply::getKeywords, request.getKeywords());
        }
        lambdaQueryWrapper.orderByDesc(WechatReply::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 新增微信关键字回复表
     * @param wechatReply 新增参数
     */
    @Override
    public Boolean create(WechatReply wechatReply) {
        //检测重复
        WechatReply voByKeywords = getVoByKeywords(wechatReply.getKeywords());
        if (voByKeywords != null) {
            throw new CrmebException(wechatReply.getKeywords() + "关键字已经存在");
        }
        wechatReply.setData(systemAttachmentService.clearPrefix(wechatReply.getData()));
        return save(wechatReply);
    }

    /**
     * 修改微信关键字回复表
     * @param wechatReply 修改参数
     */
    private Boolean updateVo(WechatReply wechatReply) {
        //检测重复
        WechatReply voByKeywords = getVoByKeywords(wechatReply.getKeywords());

        if (voByKeywords != null && !wechatReply.getId().equals(voByKeywords.getId())) {
            throw new CrmebException(wechatReply.getKeywords() + "关键字已经存在");
        }

        wechatReply.setData(systemAttachmentService.clearPrefix(wechatReply.getData()));
        return updateById(wechatReply);
    }


    /**
     * 根据关键字查询数据
     * @param keywords 新增参数
     * @return WechatReply
     */
    @Override
    public WechatReply getVoByKeywords(String keywords) {
        //检测重复
        LambdaQueryWrapper<WechatReply> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper.eq(WechatReply::getKeywords, keywords);
        return dao.selectOne(objectLambdaQueryWrapper);
    }

    /**
     * 根据关键字查询数据
     * @param id Integer id
     * @return WechatReply
     */
    private WechatReply getInfoException(Integer id, boolean isTrue) {
        //检测重复
        WechatReply info = getInfo(id);
        if (null == info) {
            throw new CrmebException("没有找到相关数据");
        }

        if (!info.getStatus() && isTrue) {
            throw new CrmebException("没有找到相关数据");
        }

        return info;
    }

    /**
     * 根据关键字查询数据
     * @param id Integer id
     * @return WechatReply
     */
    @Override
    public WechatReply getInfo(Integer id) {
       return getById(id);
    }

    /**
     * 修改微信关键字回复表
     * @param wechatReplyRequest 修改参数
     */
    @Override
    public Boolean updateReply(WechatReplyRequest wechatReplyRequest) {
        WechatReply wechatReply = getInfoException(wechatReplyRequest.getId(), false);
        BeanUtils.copyProperties(wechatReplyRequest, wechatReply);
        return updateVo(wechatReply);
    }

    /**
     * 修改状态
     * @param id integer id
     * @param status boolean 状态
     */
    @Override
    public Boolean updateStatus(Integer id, Boolean status) {
        WechatReply wechatReply = getInfoException(id, false);
        wechatReply.setStatus(status);
        return updateVo(wechatReply);
    }

}

