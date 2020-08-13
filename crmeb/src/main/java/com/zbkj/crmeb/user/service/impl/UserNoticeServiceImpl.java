package com.zbkj.crmeb.user.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;

import com.zbkj.crmeb.user.model.UserNotice;
import com.zbkj.crmeb.user.dao.UserNoticeDao;
import com.zbkj.crmeb.user.request.UserNoticeSearchRequest;
import com.zbkj.crmeb.user.service.UserNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @Description UserNoticeServiceImpl 接口实现
* @since 2020-04-28
*/
@Service
public class UserNoticeServiceImpl extends ServiceImpl<UserNoticeDao, UserNotice> implements UserNoticeService {

    @Resource
    private UserNoticeDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-28
    * @return List<UserNotice>
    */
    @Override
    public List<UserNotice> getList(UserNoticeSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

}

