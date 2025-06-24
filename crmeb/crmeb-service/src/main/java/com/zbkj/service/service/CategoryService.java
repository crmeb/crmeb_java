package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.model.category.Category;
import com.zbkj.common.request.CategoryRequest;
import com.zbkj.common.request.CategorySearchRequest;
import com.zbkj.common.vo.CategoryTreeVo;

import java.util.HashMap;
import java.util.List;

/**
*   CategoryService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface CategoryService extends IService<Category> {

    List<Category> getList(CategorySearchRequest request);

    int delete(Integer id);

    /**
     * 获取树形结构数据
     * @param type 分类
     * @param status 状态
     * @param name 名称
     * @return List
     */
    List<CategoryTreeVo> getListTree(Integer type, Integer status, String name);

    /**
     * 获取树形结构数据
     * @param type 分类
     * @param status 状态
     * @param categoryIdList 分类idList
     * @return List
     */
    List<CategoryTreeVo> getListTree(Integer type, Integer status, List<Integer> categoryIdList);

    List<Category> getByIds(List<Integer> ids);

    /**
     * 获取父级id集合
     * @param idList
     * @return
     */
    List<Category> getByPIds(List<Integer> idList);

    HashMap<Integer, String> getListInId(List<Integer> cateIdList);

    Boolean checkAuth(List<Integer> pathIdList, String uri);

    boolean update(CategoryRequest request, Integer id);

    List<Category> getChildVoListByPid(Integer pid);
    /**
     * 获取分类下子类时判断当前分类有效
     */
    List<Category> getChildVoStatusOnListByPid(Integer pid);

    boolean checkUrl(String uri);

    boolean updateStatus(Integer id);

    /**
     * 新增分类表
     */
    Boolean create(CategoryRequest categoryRequest);

    /**
     * 获取文章分类列表
     * @return List<Category>
     */
    List<Category> findArticleCategoryList();
}
