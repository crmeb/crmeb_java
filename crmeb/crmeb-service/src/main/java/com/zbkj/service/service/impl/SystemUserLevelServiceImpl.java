package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.request.SystemUserLevelRequest;
import com.zbkj.common.request.SystemUserLevelUpdateShowRequest;
import com.zbkj.service.dao.SystemUserLevelDao;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.SystemUserLevelService;
import com.zbkj.service.service.UserLevelService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * SystemUserLevelServiceImpl 接口实现
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
public class SystemUserLevelServiceImpl extends ServiceImpl<SystemUserLevelDao, SystemUserLevel> implements SystemUserLevelService {

    @Resource
    private SystemUserLevelDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionTemplate transactionTemplate;


    /**
     * 分页显示设置用户等级表
     * @return List<SystemUserLevel>
     */
    @Override
    public List<SystemUserLevel> getList() {
        LambdaQueryWrapper<SystemUserLevel> levelLambdaQueryWrapper = new LambdaQueryWrapper<>();
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
    public Boolean create(SystemUserLevelRequest request) {
        checkLevel(request);
        SystemUserLevel systemUserLevel = new SystemUserLevel();
        BeanUtils.copyProperties(request, systemUserLevel);
        systemUserLevel.setIcon(systemAttachmentService.clearPrefix(request.getIcon()));
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
     * 系统等级更新
     * @param id    等级id
     * @param request   等级数据
     * @return Boolean
     */
    @Override
    public Boolean update(Integer id, SystemUserLevelRequest request) {
        SystemUserLevel level = getById(id);
        if (ObjectUtil.isNull(level) || level.getIsDel()) {
            throw new CrmebException("等级不存在");
        }
        request.setId(id);
        checkLevel(request);
        SystemUserLevel systemUserLevel = new SystemUserLevel();
        BeanUtils.copyProperties(request, systemUserLevel);
        systemUserLevel.setIcon(systemAttachmentService.clearPrefix(request.getIcon()));
        systemUserLevel.setIsShow(level.getIsShow());
        return transactionTemplate.execute(e -> {
            dao.updateById(systemUserLevel);
            // 删除对应的用户等级数据
            userLevelService.deleteByLevelId(id);
            // 清除对应的用户等级
            userService.removeLevelByLevelId(id);
            return Boolean.TRUE;
        });
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

    /**
     * 删除系统等级
     * @param id 等级id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        SystemUserLevel level = getById(id);
        if (ObjectUtil.isNull(level) || level.getIsDel()) {
            throw new CrmebException("系统等级不存在");
        }
        level.setIsDel(true);
        return transactionTemplate.execute(e -> {
            dao.updateById(level);
            // 删除对应的用户等级数据
            userLevelService.deleteByLevelId(id);
            // 清除对应的用户等级
            userService.removeLevelByLevelId(id);
            return Boolean.TRUE;
        });
    }

    /**
     * 使用/禁用
     * @param request request
     */
    @Override
    public Boolean updateShow(SystemUserLevelUpdateShowRequest request) {
        SystemUserLevel level = getById(request.getId());
        if (ObjectUtil.isNull(level) || level.getIsDel()) {
            throw new CrmebException("等级不存在");
        }
        if (level.getIsShow().equals(request.getIsShow())) {
            return Boolean.TRUE;
        }
        level.setIsShow(request.getIsShow());
        if (request.getIsShow()) {// 启用直接保存
            return dao.updateById(level) > 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        return transactionTemplate.execute(e -> {
            dao.updateById(level);
            // 删除对应的用户等级数据
            userLevelService.deleteByLevelId(request.getId());
            // 清除对应的用户等级
            userService.removeLevelByLevelId(request.getId());
            return Boolean.TRUE;
        });
    }

    /**
     * 获取可用等级列表
     * @return List
     */
    @Override
    public List<SystemUserLevel> getUsableList() {
        LambdaQueryWrapper<SystemUserLevel> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemUserLevel::getIsShow, true);
        lqw.eq(SystemUserLevel::getIsDel, false);
        lqw.orderByAsc(SystemUserLevel::getGrade);
        return dao.selectList(lqw);
    }
}

