package com.zbkj.admin.controller;


import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.model.user.User;
import com.zbkj.common.response.TopDetail;
import com.zbkj.common.response.UserResponse;
import com.zbkj.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 用户表 前端控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/user")
@Api(tags = "会员管理")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 分页显示用户表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:user:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserResponse>> getList(@ModelAttribute @Validated UserSearchRequest request,
                                                          @ModelAttribute PageParamRequest pageParamRequest) {
        CommonPage<UserResponse> userCommonPage = CommonPage.restPage(userService.getList(request, pageParamRequest));
        return CommonResult.success(userCommonPage);
    }

    /**
     * 修改用户表
     * @param id integer id
     * @param userRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:user:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated UserUpdateRequest userRequest) {
        userRequest.setUid(id);
        if (userService.updateUser(userRequest)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改用户手机号
     * @param id 用户uid
     * @param phone 手机号
     */
    @PreAuthorize("hasAuthority('admin:user:update:phone')")
    @ApiOperation(value = "修改用户手机号")
    @RequestMapping(value = "/update/phone", method = RequestMethod.GET)
    public CommonResult<String> updatePhone(@RequestParam(name = "id") Integer id, @RequestParam(name = "phone") String phone) {
        if (userService.updateUserPhone(id, phone)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 用户详情
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:user:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<User> info(@RequestParam(value = "id") Integer id) {
        return CommonResult.success(userService.getInfoByUid(id));
    }

    /**
     * 根据参数类型查询会员对应的信息
     * @param userId Integer 会员id
     * @param type int 类型 0=消费记录，1=积分明细，2=签到记录，3=持有优惠券，4=余额变动，5=好友关系
     * @param pageParamRequest PageParamRequest 分页
     */
    @PreAuthorize("hasAuthority('admin:user:infobycondition')")
    @ApiOperation(value="会员详情")
    @RequestMapping(value = "/infobycondition", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",example = "1", required = true),
            @ApiImplicitParam(name = "type", value="0=消费记录，1=积分明细，2=签到记录，3=持有优惠券，4=余额变动，5=好友关系", example = "0"
                    , required = true)
    })
    public CommonResult<CommonPage<T>> infoByCondition(@RequestParam(name = "userId") @Valid Integer userId,
                                                       @RequestParam(name = "type") @Valid @Max(5) @Min(0) int type,
                                                       @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage((List<T>) userService.getInfoByCondition(userId,type,pageParamRequest)));
    }

    /**
     * 会员详情页Top数据
     */
    @PreAuthorize("hasAuthority('admin:user:topdetail')")
    @ApiOperation(value = "会员详情页Top数据")
    @RequestMapping(value = "topdetail", method = RequestMethod.GET)
    public CommonResult<TopDetail> topDetail (@RequestParam @Valid Integer userId) {
        return CommonResult.success(userService.getTopDetail(userId));
    }

    /**
     * 操作积分
     */
    @PreAuthorize("hasAuthority('admin:user:operate:founds')")
    @ApiOperation(value = "积分余额")
    @RequestMapping(value = "/operate/founds", method = RequestMethod.GET)
    public CommonResult<Object> founds(@Validated UserOperateIntegralMoneyRequest request) {
        if (userService.updateIntegralMoney(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 会员分组
     * @param id String id
     * @param groupId Integer 分组Id
     */
    @PreAuthorize("hasAuthority('admin:user:group')")
    @ApiOperation(value = "分组")
    @RequestMapping(value = "/group", method = RequestMethod.POST)
    public CommonResult<String> group(@RequestParam String id, @RequestParam String groupId) {
        if (userService.group(id, groupId)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 会员标签
     * @param id String id
     * @param tagId Integer 标签id
     */
    @PreAuthorize("hasAuthority('admin:user:tag')")
    @ApiOperation(value = "标签")
    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    public CommonResult<String> tag(@RequestParam String id, @RequestParam String tagId) {
        if (userService.tag(id, tagId)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 修改上级推广人
     */
    @PreAuthorize("hasAuthority('admin:user:update:spread')")
    @ApiOperation(value = "修改上级推广人")
    @RequestMapping(value = "/update/spread", method = RequestMethod.POST)
    public CommonResult<String> editSpread(@Validated @RequestBody UserUpdateSpreadRequest request) {
        if (userService.editSpread(request)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    /**
     * 更新用户会员等级
     */
    @PreAuthorize("hasAuthority('admin:user:update:level')")
    @ApiOperation(value = "更新用户会员等级")
    @RequestMapping(value = "/update/level", method = RequestMethod.POST)
    public CommonResult<Object> updateUserLevel(@Validated @RequestBody UpdateUserLevelRequest request) {
        if (userService.updateUserLevel(request)) {
            return CommonResult.success("更新成功");
        }
        return CommonResult.failed("更新失败");
    }
}



