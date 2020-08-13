package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.request.StoreBargainUserRequest;
import com.zbkj.crmeb.store.request.StoreBargainUserSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.store.service.StoreBargainUserService;
import com.zbkj.crmeb.store.model.StoreBargainUser;


/**
 * 用户参与砍价表 前端控制器
 */
//@Slf4j
//@RestController
//@RequestMapping("api/admin/store/bargain/user")
//@Api(tags = "商品 -- 砍价 -- 用户参与") //配合swagger使用

public class StoreBargainUserController {

    @Autowired
    private StoreBargainUserService storeBargainUserService;

    /**
     * 分页显示用户参与砍价表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreBargainUser>>  getList(
            @Validated StoreBargainUserSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreBargainUser> storeBargainUserCommonPage =
                CommonPage.restPage(storeBargainUserService.getList(request, pageParamRequest));
        return CommonResult.success(storeBargainUserCommonPage);
    }

    /**
     * 新增用户参与砍价表
     * @param storeBargainUserRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated StoreBargainUserRequest storeBargainUserRequest){
        StoreBargainUser storeBargainUser = new StoreBargainUser();
        BeanUtils.copyProperties(storeBargainUserRequest, storeBargainUser);

        if(storeBargainUserService.save(storeBargainUser)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除用户参与砍价表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeBargainUserService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改用户参与砍价表
     * @param id integer id
     * @param storeBargainUserRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated StoreBargainUserRequest storeBargainUserRequest){
        StoreBargainUser storeBargainUser = new StoreBargainUser();
        BeanUtils.copyProperties(storeBargainUserRequest, storeBargainUser);
        storeBargainUser.setId(id);

        if(storeBargainUserService.updateById(storeBargainUser)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询用户参与砍价表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreBargainUser> info(@RequestParam(value = "id") Integer id){
        StoreBargainUser storeBargainUser = storeBargainUserService.getById(id);
        return CommonResult.success(storeBargainUser);
   }
}



