package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.service.dao.GroupConfigDao;
import com.zbkj.service.service.GroupConfigService;
import com.zbkj.service.service.SystemAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * SystemConfigServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class GroupConfigServiceImpl extends ServiceImpl<GroupConfigDao, GroupConfig> implements GroupConfigService {

    @Resource
    private GroupConfigDao dao;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 通过tag获取数据列表
     *
     * @param tag 标签
     * @param sortRule 排序规则
     * @param status 展示状态：1-展示
     */
    @Override
    public List<GroupConfig> findByTag(Integer tag, String sortRule, Boolean status) {
        LambdaQueryWrapper<GroupConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(GroupConfig::getTag, tag);
        lqw.eq(GroupConfig::getIsDel, 0);
        if (ObjectUtil.isNotNull(status)) {
            lqw.eq(GroupConfig::getStatus, status ? 1 : 0);
        }
        if (StrUtil.isBlank(sortRule) || sortRule.equals(Constants.SORT_ASC)) {
            lqw.orderByAsc(GroupConfig::getSort);
        } else {
            lqw.orderByDesc(GroupConfig::getSort);
        }
        lqw.orderByDesc(GroupConfig::getId);
        return dao.selectList(lqw);
    }

    /**
     * 添加数据
     * @param configList 组合配置
     */
    @Override
    public Boolean saveList(List<GroupConfig> configList) {
        Integer tag = configList.get(0).getTag();
        configList.forEach(groupConfig -> {
            if (StrUtil.isNotBlank(groupConfig.getImageUrl())) {
                groupConfig.setImageUrl(systemAttachmentService.clearPrefix(groupConfig.getImageUrl()));
            }
        });
        return transactionTemplate.execute(e -> {
            deleteByTag(tag);
            saveBatch(configList);
            return Boolean.TRUE;
        });
    }

    /**
     * 通过tag删除数据
     *
     * @param tag 标签
     */
    @Override
    public Boolean deleteByTag(Integer tag) {
        LambdaUpdateWrapper<GroupConfig> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(GroupConfig::getIsDel, 1);
        wrapper.eq(GroupConfig::getTag, tag);
        wrapper.eq(GroupConfig::getIsDel, 0);
        return update(wrapper);
    }


}
