package com.zbkj.crmeb.user.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.user.request.UserLevelRequest;
import com.zbkj.crmeb.user.request.UserLevelSearchRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.user.service.UserLevelService;
import com.zbkj.crmeb.user.model.UserLevel;


/**
 * 用户等级记录表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/user/level")
@Api(tags = "会员 -- 等级")
public class UserLevelController {

    @Autowired
    private UserLevelService userLevelService;

    /**
     * 分页显示用户等级记录表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserLevel>>  getList(@Validated UserLevelSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<UserLevel> userLevelCommonPage = CommonPage.restPage(userLevelService.getList(request, pageParamRequest));
        return CommonResult.success(userLevelCommonPage);
    }

    /**
     * 修改用户会员等级
     * @param userId integer id
     * @param levelId integer 等级
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/level", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId", value="会员ID", dataType = "Integer", required = true),
            @ApiImplicitParam(name="levelId", value="等级ID", dataType = "Integer", required = true)
    })
    public CommonResult<String> level(@RequestParam Integer userId, @RequestParam int levelId){
        if(userLevelService.level(userId, levelId)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 清除会员等级
     * @param userId integer id
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @ApiOperation(value = "清除会员等级")
    @RequestMapping(value = "/clean", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId", value="会员ID", dataType = "Integer", required = true),
    })
    public CommonResult<String> clean(@RequestParam Integer userId){
        if(userLevelService.clean(userId)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
}



