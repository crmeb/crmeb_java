package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import com.zbkj.crmeb.system.model.SystemRole;
import com.zbkj.crmeb.system.request.SystemRoleSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemRoleService 接口
* @since 2020-04-18
*/
public interface SystemRoleService extends IService<SystemRole> {

    List<SystemRole> getList(SystemRoleSearchRequest request, PageParamRequest pageParamRequest);

    Boolean checkAuth(String uri);

    List<CategoryTreeVo> menu();
}
