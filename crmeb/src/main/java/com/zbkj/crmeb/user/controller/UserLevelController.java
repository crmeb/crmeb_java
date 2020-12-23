package com.zbkj.crmeb.user.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.user.model.UserLevel;
import com.zbkj.crmeb.user.request.UserLevelSearchRequest;
import com.zbkj.crmeb.user.service.UserLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户等级记录表 前端控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
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
}



