package com.zbkj.crmeb.front.controller;


import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.front.request.PasswordRequest;
import com.zbkj.crmeb.front.request.UserBindingPhoneUpdateRequest;
import com.zbkj.crmeb.front.request.UserEditRequest;
import com.zbkj.crmeb.front.request.UserSpreadPeopleRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.front.service.UserCenterService;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.model.UserIntegralRecord;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户 -- 用户中心
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
@Slf4j
@RestController("FrontUserController")
@RequestMapping("api/front")
@Api(tags = "用户 -- 用户中心")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 修改密码
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "手机号修改密码")
    @RequestMapping(value = "/register/reset", method = RequestMethod.POST)
    public CommonResult<Boolean> password(@RequestBody @Validated PasswordRequest request){
        return CommonResult.success(userService.password(request));
    }

    /**
     * 修改个人资料
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "修改个人资料")
    @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
    public CommonResult<Boolean> personInfo(@RequestBody @Validated UserEditRequest request){
        User user = userService.getInfo();
        user.setAvatar(systemAttachmentService.clearPrefix(request.getAvatar()));
        user.setNickname(request.getNickname());
        return CommonResult.success(userService.updateById(user));
    }

    /**
     * 个人中心-用户信息
     */
    @ApiOperation(value = "个人中心-用户信息")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public CommonResult<UserCenterResponse> getUserCenter(){
        return CommonResult.success(userService.getUserCenter());
    }

    /**
     * 换绑手机号校验
     */
    @ApiOperation(value = "换绑手机号校验")
    @RequestMapping(value = "update/binding/verify", method = RequestMethod.POST)
    public CommonResult<Boolean> updatePhoneVerify(@RequestBody @Validated UserBindingPhoneUpdateRequest request){
        return CommonResult.success(userService.updatePhoneVerify(request));
    }

    /**
     * 绑定手机号
     */
    @ApiOperation(value = "换绑手机号")
    @RequestMapping(value = "update/binding", method = RequestMethod.POST)
    public CommonResult<Boolean> updatePhone(@RequestBody @Validated UserBindingPhoneUpdateRequest request){
        return CommonResult.success(userService.updatePhone(request));
    }

    /**
     * 用户中心菜单
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "获取个人中心菜单")
    @RequestMapping(value = "/menu/user", method = RequestMethod.GET)
    public CommonResult<HashMap<String, Object>> getMenuUser(){
        return CommonResult.success(systemGroupDataService.getMenuUser());
    }

    /**
     * 推广数据接口(昨天的佣金 累计提现金额 当前佣金)
     */
    @ApiOperation(value = "推广数据接口(昨天的佣金 累计提现金额 当前佣金)")
    @RequestMapping(value = "/commission", method = RequestMethod.GET)
    public CommonResult<UserCommissionResponse> getCommission(){
        return CommonResult.success(userCenterService.getCommission());
    }

    /**
     * 推广佣金明细
     */
    @ApiOperation(value = "推广佣金明细")
    @RequestMapping(value = "/spread/commission/detail", method = RequestMethod.GET)
    public CommonResult<CommonPage<SpreadCommissionDetailResponse>> getSpreadCommissionDetail(@Validated PageParamRequest pageParamRequest){
        PageInfo<SpreadCommissionDetailResponse> commissionDetail = userCenterService.getSpreadCommissionDetail(pageParamRequest);
        return CommonResult.success(CommonPage.restPage(commissionDetail));
    }

    /**
     * 推广佣金/提现总和
     * @author Mr.Zhang
     * @since 2020-06-08
     */
    @ApiOperation(value = "推广佣金/提现总和")
    @RequestMapping(value = "/spread/count/{type}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "类型 佣金类型3=佣金,4=提现", allowableValues = "range[3,4]", dataType = "int")
    public CommonResult<Map<String, BigDecimal>> getSpreadCountByType(@PathVariable int type){
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("count", userCenterService.getSpreadCountByType(type));
        return CommonResult.success(map);
    }

    /**
     * 提现申请
     * @author Mr.Zhang
     * @since 2020-06-08
     */
    @ApiOperation(value = "提现申请")
    @RequestMapping(value = "/extract/cash", method = RequestMethod.POST)
    public CommonResult<Boolean> extractCash(@RequestBody @Validated UserExtractRequest request){
        return CommonResult.success(userCenterService.extractCash(request));
    }

    /**
     * 提现记录
     */
    @ApiOperation(value = "提现记录")
    @RequestMapping(value = "/extract/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserExtractRecordResponse>> getExtractRecord(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getExtractRecord(pageParamRequest)));
    }

    /**
     * 提现用户信息
     */
    @ApiOperation(value = "提现用户信息")
    @RequestMapping(value = "/extract/user", method = RequestMethod.GET)
    public CommonResult<UserExtractCashResponse> getExtractUser(){
        return CommonResult.success(userCenterService.getExtractUser());
    }

    /**
     * 提现银行
     */
    @ApiOperation(value = "提现银行/提现最低金额")
    @RequestMapping(value = "/extract/bank", method = RequestMethod.GET)
    public CommonResult<List<String>> getExtractBank(){
        return CommonResult.success(userCenterService.getExtractBank());
    }

    /**
     * 会员等级列表
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "会员等级列表")
    @RequestMapping(value = "/user/level/grade", method = RequestMethod.GET)
    public CommonResult<List<SystemUserLevel>> getUserLevelList(){
        return CommonResult.success(userCenterService.getUserLevelList());
    }

    /**
     * 推广人统计
     */
    @ApiOperation(value = "推广人统计")
    @RequestMapping(value = "/spread/people/count", method = RequestMethod.GET)
    public CommonResult<UserSpreadPeopleResponse>  getSpreadPeopleCount(){
        return CommonResult.success(userCenterService.getSpreadPeopleCount());
    }

    /**
     * 推广人列表
     */
    @ApiOperation(value = "推广人列表")
    @RequestMapping(value = "/spread/people", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserSpreadPeopleItemResponse>> getSpreadPeopleList(@Validated UserSpreadPeopleRequest request, @Validated PageParamRequest pageParamRequest) {
        List<UserSpreadPeopleItemResponse> spreadPeopleList = userCenterService.getSpreadPeopleList(request, pageParamRequest);
        CommonPage<UserSpreadPeopleItemResponse> commonPage = CommonPage.restPage(spreadPeopleList);
        return CommonResult.success(commonPage);
    }

    /**
     * 用户积分信息
     */
    @ApiOperation(value = "用户积分信息")
    @RequestMapping(value = "/integral/user", method = RequestMethod.GET)
    public CommonResult<IntegralUserResponse> getIntegralUser(){
        return CommonResult.success(userCenterService.getIntegralUser());
    }

    /**
     * 积分记录
     */
    @ApiOperation(value = "积分记录")
    @RequestMapping(value = "/integral/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserIntegralRecord>> getIntegralList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserIntegralRecordList(pageParamRequest)));
    }

    /**
     * 经验记录
     */
    @ApiOperation(value = "经验记录")
    @RequestMapping(value = "/user/expList", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserBill>> getExperienceList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserExperienceList(pageParamRequest)));
    }

    /**
     * 用户资金统计
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    @ApiOperation(value = "用户资金统计")
    @RequestMapping(value = "/user/balance", method = RequestMethod.GET)
    public CommonResult<UserBalanceResponse>  getUserBalance(){
        return CommonResult.success(userCenterService.getUserBalance());
    }

    /**
     * 推广订单
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    @ApiOperation(value = "推广订单")
    @RequestMapping(value = "/spread/order", method = RequestMethod.GET)
    public CommonResult<UserSpreadOrderResponse>  getSpreadOrder(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getSpreadOrder(pageParamRequest));
    }

    /**
     * 推广人排行
     * @return List<User>
     */
    @ApiOperation(value = "推广人排行")
    @RequestMapping(value = "/rank", method = RequestMethod.GET)
    public CommonResult<List<User>> getTopSpreadPeopleListByDate(@RequestParam(required = false) String type, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getTopSpreadPeopleListByDate(type, pageParamRequest));
    }

    /**
     * 佣金排行
     * @return 优惠券集合
     */
    @ApiOperation(value = "佣金排行")
    @RequestMapping(value = "/brokerage_rank", method = RequestMethod.GET)
    public CommonResult<List<User>> getTopBrokerageListByDate(@RequestParam String type, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getTopBrokerageListByDate(type, pageParamRequest));
    }

    /**
     * 当前用户在佣金排行第几名
     * @return 优惠券集合
     */
    @ApiOperation(value = "当前用户在佣金排行第几名")
    @RequestMapping(value = "/user/brokerageRankNumber", method = RequestMethod.GET)
    public CommonResult<Integer> getNumberByTop(@RequestParam String type){
        return CommonResult.success(userCenterService.getNumberByTop(type));
    }

    /**
     * 海报背景图
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    @ApiOperation(value = "推广海报图")
    @RequestMapping(value = "/user/spread/banner", method = RequestMethod.GET)
    public CommonResult<List<UserSpreadBannerResponse>>  getSpreadBannerList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getSpreadBannerList(pageParamRequest));
    }

    /**
     * 绑定推广关系（登录状态）
     * @param spreadPid 推广id
     * @return 绑定结果
     */
    @ApiOperation(value = "绑定推广关系（登录状态）")
    @RequestMapping(value = "/user/bindSpread", method = RequestMethod.GET)
    public CommonResult<Boolean> bindsSpread(Integer spreadPid){
        userService.bindSpread(spreadPid);
        return CommonResult.success();
    }
}



