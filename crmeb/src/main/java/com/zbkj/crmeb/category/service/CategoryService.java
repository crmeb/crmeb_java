package com.zbkj.crmeb.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.request.CategoryRequest;
import com.zbkj.crmeb.category.request.CategorySearchRequest;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;

import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @Description CategoryService 接口
* @since 2020-04-16
*/
public interface CategoryService extends IService<Category> {
    List<Category> getList(CategorySearchRequest request, PageParamRequest pageParamRequest);

    int delete(Integer id);

    String getPathByPId(Integer pid);

    List<CategoryTreeVo> getListTree(Integer type, Integer status);
    List<CategoryTreeVo> getListTree(Integer type, Integer status, List<Integer> categoryIdList);

    List<Category> getByIds(List<Integer> ids);

    HashMap<Integer, String> getListInId(List<Integer> cateIdList);

    void checkCategoryIsExist(List<Integer> categoryIdList);

    Boolean checkAuth(List<Integer> pathIdList, String uri);

    boolean update(CategoryRequest request, Integer id);

    List<Category> getChildVoListByPid(Integer pid);

    int checkName(String name, Integer type);

    boolean checkUrl(String uri);
}
