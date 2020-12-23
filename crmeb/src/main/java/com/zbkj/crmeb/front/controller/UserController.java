package com.zbkj.crmeb.front.controller;


import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.front.request.PasswordRequest;
import com.zbkj.crmeb.front.request.UserBindingRequest;
import com.zbkj.crmeb.front.request.UserEditRequest;
import com.zbkj.crmeb.front.request.UserSpreadPeopleRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.front.service.UserCenterService;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
     * 获取用户个人资料
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "获取个人资料")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public CommonResult<UserCenterResponse> getUserCenter(){
        return CommonResult.success(userService.getUserCenter());
    }

    /**
     * 获取用户个人资料
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "当前登录用户信息")
    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public CommonResult<User> getInfo(){
        return CommonResult.success(userService.getUserPromoter());
    }

    /**
     * 绑定手机号
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "绑定手机号")
    @RequestMapping(value = "/binding", method = RequestMethod.POST)
    public CommonResult<Boolean> bind(@RequestBody @Validated UserBindingRequest request){
        return CommonResult.success(userService.bind(request));
    }

    /**
     * 用户中心菜单
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "获取个人中心菜单")
    @RequestMapping(value = "/menu/user", method = RequestMethod.GET)
    public CommonResult<HashMap<String, Object>> getMenuUser(){

        HashMap<String, Object> map = new HashMap<>();
        map.put("routine_my_menus", systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_USER_CENTER_MENU));
        map.put("routine_my_banner", systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_USER_CENTER_BANNER));
        return CommonResult.success(map);
    }

    /**
     * 推广数据接口(昨天的佣金 累计提现金额 当前佣金)
     * @author Mr.Zhang
     * @since 2020-06-08
     */
    @ApiOperation(value = "推广数据接口(昨天的佣金 累计提现金额 当前佣金)")
    @RequestMapping(value = "/commission", method = RequestMethod.GET)
    public CommonResult<UserCommissionResponse> getCommission(){
        return CommonResult.success(userCenterService.getCommission());
    }

    /**
     * 推广佣金明细
     * @author Mr.Zhang
     * @since 2020-06-08
     */
    @ApiOperation(value = "推广佣金明细")
    @RequestMapping(value = "/spread/commission/{type}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "类型 佣金类型|0=全部,1=消费,2=充值,3=返佣,4=提现", allowableValues = "range[0,1,2,3，4]", dataType = "int")
    public CommonResult<CommonPage<UserSpreadCommissionResponse>> getSpreadCommissionByType(@PathVariable int type, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getSpreadCommissionByType(type, pageParamRequest)));
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
        switch (request.getExtractType()){
            case "weixin":
                if(StringUtils.isBlank(request.getWechat())){
                    throw new  CrmebException("请填写微信号！");
                }
                request.setAlipayCode(null);
                request.setBankCode(null);
                request.setBankName(null);
                break;
            case "alipay":
                if(StringUtils.isBlank(request.getAlipayCode())){
                    throw new  CrmebException("请填写支付宝账号！");
                }
                request.setWechat(null);
                request.setBankCode(null);
                request.setBankName(null);
                break;
            case "bank":
                if(StringUtils.isBlank(request.getBankName())){
                    throw new  CrmebException("请填写银行名称！");
                }
                if(StringUtils.isBlank(request.getBankCode())){
                    throw new  CrmebException("请填写银行卡号！");
                }
                request.setWechat(null);
                request.setAlipayCode(null);
                break;
            default:
               throw new  CrmebException("请选择支付方式");
        }
        return CommonResult.success(userCenterService.extractCash(request));
    }

    /**
     * 提现记录
     * @author HZW
     * @since 2020-10-27
     * @return
     */
    @ApiOperation(value = "提现记录")
    @RequestMapping(value = "/extract/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserExtractRecordResponse>> getExtractRecord(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getExtractRecord(pageParamRequest)));
    }

    /**
     * 提现总金额
     * @author HZW
     * @since 2020-10-27
     * @return
     */
    @ApiOperation(value = "提现总金额")
    @RequestMapping(value = "/extract/totalMoney", method = RequestMethod.GET)
    public CommonResult<Map<String, BigDecimal>> getTotalMoney(){
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("count", userCenterService.getExtractTotalMoney());
        return CommonResult.success(map);
    }

    /**
     * 提现银行/提现最低金额
     * @author Mr.Zhang
     * @since 2020-06-08
     */
    @ApiOperation(value = "提现银行/提现最低金额")
    @RequestMapping(value = "/extract/bank", method = RequestMethod.GET)
    public CommonResult<UserExtractCashResponse> minExtractCash(){
        return CommonResult.success(userCenterService.minExtractCash());
    }

    /**
     * 会员等级列表
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "会员等级列表")
    @RequestMapping(value = "/user/level/grade", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemUserLevel>>  getUserLevelList(){
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserLevelList()));
    }

    /**
     * 推广用户
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "推广用户")
    @RequestMapping(value = "/spread/people", method = RequestMethod.GET)
    public CommonResult<UserSpreadPeopleResponse>  getSpreadPeopleList(@Validated UserSpreadPeopleRequest request, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getSpreadPeopleList(request, pageParamRequest));
    }

    /**
     * 积分记录
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "积分记录")
    @RequestMapping(value = "/integral/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserBill>>  getIntegralList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserBillList(Constants.USER_BILL_CATEGORY_INTEGRAL, pageParamRequest)));
    }

    /**
     * 经验记录
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "经验记录")
    @RequestMapping(value = "/user/expList", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserBill>>  getExperienceList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserBillList(Constants.USER_BILL_CATEGORY_EXPERIENCE, pageParamRequest)));
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
    @RequestMapping(value = "rank", method = RequestMethod.GET)
    public CommonResult<List<User>> getTopSpreadPeopleListByDate(@RequestParam String type, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getTopSpreadPeopleListByDate(type, pageParamRequest));
    }

    /**
     * 佣金排行
     * @return 优惠券集合
     */
    @ApiOperation(value = "佣金排行")
    @RequestMapping(value = "brokerage_rank", method = RequestMethod.GET)
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
}



