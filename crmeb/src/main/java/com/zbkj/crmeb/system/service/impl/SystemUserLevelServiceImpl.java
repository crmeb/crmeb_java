package com.zbkj.crmeb.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.system.dao.SystemUserLevelDao;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.system.request.SystemUserLevelRequest;
import com.zbkj.crmeb.system.request.SystemUserLevelSearchRequest;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemUserLevelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * SystemUserLevelServiceImpl 接口实现
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
@Service
public class SystemUserLevelServiceImpl extends ServiceImpl<SystemUserLevelDao, SystemUserLevel> implements SystemUserLevelService {

    @Resource
    private SystemUserLevelDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;


    /**
     * 分页显示设置用户等级表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return List<SystemUserLevel>
     */
    @Override
    public List<SystemUserLevel> getList(SystemUserLevelSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SystemUserLevel> levelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(request.getName())){
            levelLambdaQueryWrapper.like(SystemUserLevel::getName, request.getName());
        }

        if(request.getIsShow() != null){
            levelLambdaQueryWrapper.eq(SystemUserLevel::getIsShow, request.getIsShow());
        }

        levelLambdaQueryWrapper.eq(SystemUserLevel::getIsDel, false);
        levelLambdaQueryWrapper.orderByAsc(SystemUserLevel::getGrade);
        return dao.selectList(levelLambdaQueryWrapper);
    }

    /**
     * 新增设置用户等级表
     * @param request SystemUserLevelRequest 新增参数
     * @return boolean
     * 等级名称不能重复
     * 等级级别不能重复
     */
    @Override
    public boolean create(SystemUserLevelRequest request) {
        checkLevel(request);
        SystemUserLevel systemUserLevel = new SystemUserLevel();
        BeanUtils.copyProperties(request, systemUserLevel);
        clearPrefix(systemUserLevel);
        return save(systemUserLevel);
    }

    /**
     * 添加、修改校验
     * @param request 用户等级参数
     * 等级名称不能重复
     * 等级级别不能重复
     */
    private void checkLevel(SystemUserLevelRequest request) {
        SystemUserLevel temp;
        // 校验名称
        LambdaQueryWrapper<SystemUserLevel> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemUserLevel::getName, request.getName());
        if (ObjectUtil.isNotNull(request.getId())) {
            lqw.ne(SystemUserLevel::getId, request.getId());
        }
        lqw.eq(SystemUserLevel::getIsDel, false);
        temp = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(temp)) {
            throw new CrmebException("用户等级名称重复");
        }
        // 校验等级级别
        lqw.clear();
        lqw.eq(SystemUserLevel::getGrade, request.getGrade());
        if (ObjectUtil.isNotNull(request.getId())) {
            lqw.ne(SystemUserLevel::getId, request.getId());
        }
        lqw.eq(SystemUserLevel::getIsDel, false);
        temp = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(temp)) {
            throw new CrmebException("用户等级级别重复");
        }
        // 校验等级经验不能比上一级别的低,不能比下一级别高
        if (request.getGrade() > 1) {
            lqw.clear();
            lqw.lt(SystemUserLevel::getGrade, request.getGrade());
            if (ObjectUtil.isNotNull(request.getId())) {
                lqw.ne(SystemUserLevel::getId, request.getId());
            }
            lqw.eq(SystemUserLevel::getIsDel, false);
            lqw.orderByDesc(SystemUserLevel::getGrade);
            lqw.last(" limit 1");
            temp = dao.selectOne(lqw);
            if (ObjectUtil.isNotNull(temp) && temp.getExperience() >= request.getExperience()) {
                throw new CrmebException("当前等级的经验不能比上一级别的经验低");
            }
        }
        lqw.clear();
        lqw.gt(SystemUserLevel::getGrade, request.getGrade());
        if (ObjectUtil.isNotNull(request.getId())) {
            lqw.ne(SystemUserLevel::getId, request.getId());
        }
        lqw.eq(SystemUserLevel::getIsDel, false);
        lqw.orderByAsc(SystemUserLevel::getGrade);
        lqw.last(" limit 1");
        temp = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(temp) && temp.getExperience() <= request.getExperience()) {
            throw new CrmebException("当前等级的经验不能比下一级别的经验高");
        }
    }


    /**
     * 修改设置用户等级表
     * @param id integer id
     * @param request 修改参数
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return boolean
     */
    @Override
    public boolean update(Integer id, SystemUserLevelRequest request) {
        request.setId(id);
        checkLevel(request);
        SystemUserLevel systemUserLevel = new SystemUserLevel();
        BeanUtils.copyProperties(request, systemUserLevel);
        clearPrefix(systemUserLevel);
        return updateById(systemUserLevel);
    }

    /**
     * 清除前缀
     * @param systemUserLevel 需要去掉前缀的参数所在的对象
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    private void clearPrefix(SystemUserLevel systemUserLevel) {
        systemUserLevel.setImage(systemAttachmentService.clearPrefix(systemUserLevel.getImage()));
        systemUserLevel.setIcon(systemAttachmentService.clearPrefix(systemUserLevel.getIcon()));
    }

    @Override
    public SystemUserLevel getByLevelId(Integer levelId) {
        LambdaQueryWrapper<SystemUserLevel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemUserLevel::getIsShow, 1);
        lambdaQueryWrapper.eq(SystemUserLevel::getIsDel, 0);
        lambdaQueryWrapper.eq(SystemUserLevel::getId, levelId);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 获取系统等级列表（移动端）
     */
    @Override
    public List<SystemUserLevel> getH5LevelList() {
        LambdaQueryWrapper<SystemUserLevel> lqw = new LambdaQueryWrapper<>();
        lqw.select(SystemUserLevel::getId, SystemUserLevel::getName, SystemUserLevel::getIcon, SystemUserLevel::getExperience);
        lqw.eq(SystemUserLevel::getIsShow, true);
        lqw.eq(SystemUserLevel::getIsDel, false);
        lqw.orderByAsc(SystemUserLevel::getGrade);
        return dao.selectList(lqw);
    }
}

