package com.zbkj.crmeb.category.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.category.dao.CategoryDao;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.request.CategoryRequest;
import com.zbkj.crmeb.category.request.CategorySearchRequest;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CategoryServiceImpl 接口实现
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Resource
    private CategoryDao dao;


    /**
     * 获取分类下子类的数量
     * @param request 请求参数
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return List<Category>
     */
    @Override
    public List<Category> getList(CategorySearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Category category = new Category();
        BeanUtils.copyProperties(request,category);
//        objectQueryWrapper.setEntity(category);
        if(null != category.getPid()){
            lambdaQueryWrapper.eq(Category::getPid, category.getPid());
        }
        if(null != category.getType()){
            lambdaQueryWrapper.eq(Category::getType, category.getType());
        }
        if(null != category.getStatus()){
            lambdaQueryWrapper.eq(Category::getStatus, category.getStatus());
        }
        if(null != category.getName()){
            lambdaQueryWrapper.like(Category::getName, category.getName());
        }
        lambdaQueryWrapper.orderByDesc(Category::getSort).orderByDesc(Category::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 通过id集合获取列表
     * @param idList List<Integer> id集合
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return List<Category>
     */
    @Override
    public List<Category> getByIds(List<Integer> idList) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Category::getId, idList);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 通过id集合获取列表 id => name
     * @param cateIdList List<Integer> id集合
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return HashMap<Integer, String>
     */
    @Override
    public HashMap<Integer, String> getListInId(List<Integer> cateIdList) {
        HashMap<Integer, String> map = new HashMap<>();
        List<Category> list = getByIds(cateIdList);
        for (Category category : list){
            map.put(category.getId(), category.getName());
        }

        return map;
    }

    /**
     * 批量查询分类id是否合法
     * @param categoryIdList List<Integer> id集合
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @Override
    public void checkCategoryIsExist(List<Integer> categoryIdList) {
        //查询分类id是否合法
        HashMap<Integer, String> categoryServiceListInId = getListInId(categoryIdList);
        if(categoryServiceListInId.size() != categoryIdList.size()){
            for (Integer categoryId: categoryIdList) {
                String categoryName = CrmebUtil.getStrValueByIndex(categoryServiceListInId, categoryId);

                if(categoryName.equals("")){
                    //如果取出的分类名称是空， 那么有问题
                    throw new CrmebException("ID是 " + categoryId + "的分类不存在，或者分类名称为空！");
                }
            }
        }
    }

    /**
     * 查询id和url是否存在
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return Boolean
     */
    @Override
    public Boolean checkAuth(List<Integer> pathIdList, String uri) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Category::getId, pathIdList).eq(Category::getUrl, uri);
        List<Category> categoryList = dao.selectList(lambdaQueryWrapper);
        if(categoryList.size() < 1){
            return false;
        }

        return true;
    }

    /**
     * 修改
     * @param request CategoryRequest
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return bool
     */
    @Override
    public boolean update(CategoryRequest request, Integer id) {
        try{
            //修改分类信息
            Category category = new Category();
            BeanUtils.copyProperties(request, category);
            category.setId(id);
            category.setPath(getPathByPId(category.getPid()));

            updateById(category);

            //如状态为关闭，那么所以子集的状态都关闭
            if(!request.getStatus()){
                updateStatusByPid(id, false);
            }else{
                //如是开启，则父类的状态为开启
                updatePidStatusById(id);
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 开启父级状态
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private void updatePidStatusById(Integer id) {
        Category category = getById(id);
        List<Integer> categoryIdList = CrmebUtil.stringToArrayByRegex(category.getPath(), "/");
        categoryIdList.removeIf(i -> i.equals(0));
        ArrayList<Category> categoryArrayList = new ArrayList<>();
        if(categoryIdList.size() < 1){
            return;
        }
        for (Integer categoryId: categoryIdList) {
            Category categoryVo = new Category();
            categoryVo.setId(categoryId);
            categoryVo.setStatus(true);
            categoryArrayList.add(categoryVo);
        }
        updateBatchById(categoryArrayList);
    }

    /**
     * 获取分类下子类的数量
     * @param pid Integer
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return bool
     */
    private int getChildCountByPid(Integer pid) {
        //查看是否有子类
        QueryWrapper<Category> objectQueryWrapper = new QueryWrapper<>();
//        objectQueryWrapper.eq("status", 1);
        objectQueryWrapper.like("path", "/"+pid+"/");
        return dao.selectCount(objectQueryWrapper);
    }

    /**
     * 修改分类以及子类的状态
     * @param pid Integer
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return bool
     */
    private int updateStatusByPid(Integer pid, boolean status) {
        //查看是否有子类
        Category category = new Category();
        category.setStatus(status);

        QueryWrapper<Category> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.like("path", "/"+pid+"/");
        return dao.update(category, objectQueryWrapper);
    }

    @Override
    public String getPathByPId(Integer pid) {
        Category category = getById(pid);
        if(null != category){
            return category.getPath() + pid + "/";
        }
        return null;
    }

    /**
     * 带结构的无线级分类
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @Override
    public List<CategoryTreeVo> getListTree(Integer type, Integer status, String name) {
        return getTree(type, status,name,null);
    }

    /**
     * 带权限的属性结构
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @Override
    public List<CategoryTreeVo> getListTree(Integer type, Integer status, List<Integer> categoryIdList) {
        return getTree(type, status,null,categoryIdList);
    }

    /**
     * 带结构的无线级分类
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private List<CategoryTreeVo> getTree(Integer type, Integer status,String name, List<Integer> categoryIdList) {
        //循环数据，把数据对象变成带list结构的vo
        List<CategoryTreeVo> treeList = new ArrayList<>();

        LambdaQueryWrapper<Category> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Category::getType, type);

        if(null != categoryIdList && categoryIdList.size() > 0){
            lambdaQueryWrapper.in(Category::getId, categoryIdList);
        }

        if(status >= 0){
            lambdaQueryWrapper.eq(Category::getStatus, status);
        }
        if(StringUtils.isNotBlank(name)){ // 根据名称模糊搜索
            lambdaQueryWrapper.like(Category::getName,name);
        }

        lambdaQueryWrapper.orderByDesc(Category::getSort);
        lambdaQueryWrapper.orderByAsc(Category::getId);
        List<Category> allTree = dao.selectList(lambdaQueryWrapper);
        if(allTree == null){
            return null;
        }
        // 根据名称搜索特殊处理 这里仅仅处理两层搜索后有子父级关系的数据
        if(StringUtils.isNotBlank(name) && allTree.size() >0){
            List<Category> searchCategory = new ArrayList<>();
            for (Category category : allTree) {
                if(category.getPid() >0){
                    searchCategory.add(dao.selectById(category.getPid()));
                }
            }
            allTree.addAll(searchCategory);
        }

        for (Category category: allTree) {
            CategoryTreeVo categoryTreeVo = new CategoryTreeVo();
            BeanUtils.copyProperties(category, categoryTreeVo);
            treeList.add(categoryTreeVo);
        }


        //返回
        Map<Integer, CategoryTreeVo> map = new HashMap<>();
        //ID 为 key 存储到map 中
        for (CategoryTreeVo categoryTreeVo1 : treeList) {
            map.put(categoryTreeVo1.getId(), categoryTreeVo1);
        }

        List<CategoryTreeVo> list = new ArrayList<>();
        for (CategoryTreeVo tree : treeList) {
            //子集ID返回对象，有则添加。
            CategoryTreeVo tree1 = map.get(tree.getPid());
            if(tree1 != null){
                tree1.getChild().add(tree);
            }else {
                list.add(tree);
            }
        }
        return list;
    }

    /**
     * 删除分类表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return bool
     */
    @Override
    public int delete(Integer id) {
        //查看是否有子类, 物理删除
        if(getChildCountByPid(id) > 0){
            throw new CrmebException("当前分类下有子类，请先删除子类！");
        }

        return dao.deleteById(id);
    }

    /**
     * 获取分类下子类
     * @param pid Integer
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return List<Category>
     */
    @Override
    public List<Category> getChildVoListByPid(Integer pid) {
        //查看是否有子类
        QueryWrapper<Category> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("status", 1);
        objectQueryWrapper.like("path", "/"+pid+"/");
        return dao.selectList(objectQueryWrapper);
    }

    /**
     * 检测分类名称是否存在
     * @param name String 分类名
     * @param type int 类型
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return int
     */
    @Override
    public int checkName(String name, Integer type) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Category::getName, name)
                .eq(Category::getType, type);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 检测url是否存在
     * @param uri String url
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return int
     */
    @Override
    public boolean checkUrl(String uri) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Category::getUrl, uri);
        return dao.selectCount(lambdaQueryWrapper) > 0;
    }

    @Override
    public boolean updateStatus(Integer id) {
        Category category = getById(id);
        category.setStatus(!category.getStatus());
        return updateById(category);
    }
}

