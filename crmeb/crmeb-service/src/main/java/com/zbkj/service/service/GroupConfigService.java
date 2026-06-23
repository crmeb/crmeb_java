package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.system.GroupConfig;

import java.util.List;

public interface GroupConfigService extends IService<GroupConfig> {

    /**
     * 通过tag获取数据列表
     *
     * @param tag 标签
     * @param sortRule 排序规则
     * @param status 展示状态：1-展示
     */
    List<GroupConfig> findByTag(Integer tag, String sortRule, Boolean status);

    /**
     * 添加数据
     * @param configList 组合配置
     */
    Boolean saveList(List<GroupConfig> configList);

    /**
     * 按tag删除
     */
    Boolean deleteByTag(Integer tag);


}
