package com.zbkj.crmeb.system.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import com.zbkj.crmeb.system.model.SystemRole;
import com.zbkj.crmeb.system.request.SystemRoleRequest;
import com.zbkj.crmeb.system.request.SystemRoleSearchRequest;
import com.zbkj.crmeb.system.service.SystemRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 身份管理表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/role")
@Api(tags = "设置 -- 权限管理 -- 身份管理")
public class SystemRoleController {

    @Autowired
    private SystemRoleService systemRoleService;

    /**
     * 分页显示身份管理表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemRole>>  getList(@Validated SystemRoleSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<SystemRole> systemRoleCommonPage = CommonPage.restPage(systemRoleService.getList(request, pageParamRequest));
        return CommonResult.success(systemRoleCommonPage);
    }

    /**
     * 新增身份管理表
     * @param systemRoleRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated SystemRoleRequest systemRoleRequest){
        SystemRole systemRole = new SystemRole();
        BeanUtils.copyProperties(systemRoleRequest, systemRole);

        if(systemRoleService.save(systemRole)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除身份管理表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(systemRoleService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改身份管理表
     * @param id integer id
     * @param systemRoleRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated SystemRoleRequest systemRoleRequest){
        SystemRole systemRole = new SystemRole();
        BeanUtils.copyProperties(systemRoleRequest, systemRole);
        systemRole.setId(id);

        if(systemRoleService.updateById(systemRole)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询身份管理表信息
     * @param ids String
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<List<SystemRole>> info(@RequestParam(value = "ids") String ids){
//        SystemRole systemRole = systemRoleService.getById(CrmebUtil.stringToArray(ids));
        List<SystemRole> systemRole = systemRoleService.getListInIds(CrmebUtil.stringToArray(ids));
        return CommonResult.success(systemRole);
   }

    /**
     * 管理员菜单权限
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "菜单")
    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public CommonResult<List<CategoryTreeVo>> menu(){
        List<CategoryTreeVo> categoryTreeVoList = systemRoleService.menu();
        return CommonResult.success(categoryTreeVoList);
    }
}



