package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.log.SensitiveMethodLog;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.service.dao.SensitiveMethodLogDao;
import com.zbkj.service.service.SensitiveMethodLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * SensitiveMethoyLogServiceImpl 接口实现
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Service
public class SensitiveMethodLogServiceImpl extends ServiceImpl<SensitiveMethodLogDao, SensitiveMethodLog> implements SensitiveMethodLogService {

    @Resource
    private SensitiveMethodLogDao dao;

    /**
     * 添加敏感记录
     * @param methodLog 记录信息
     */
    @Override
    public void addLog(SensitiveMethodLog methodLog) {
        save(methodLog);
    }

    /**
     * 分页列表
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<SensitiveMethodLog> getPageList(PageParamRequest pageParamRequest) {
        Page<SensitiveMethodLog> logPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<SensitiveMethodLog> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(SensitiveMethodLog::getId);
        List<SensitiveMethodLog> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(logPage, list);
    }
}

