package com.zbkj.crmeb.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.CommonResult;
import com.constants.WeChatConstants;
import com.utils.RedisUtil;
import com.zbkj.crmeb.wechat.service.WeChatService;
import com.zbkj.crmeb.wechat.service.WechatPublicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 微信缓存表 前端控制器
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
@RequestMapping("api/admin/wechat/menu")
@Api(tags = "微信开放平台 -- 菜单管理")
public class WeChatController {

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WechatPublicService wechatPublicService;

    /**
     * 获取微信菜单
     * @author Mr.Zhang
     * @since 2020-04-22
     */
    @ApiOperation(value = "获取自定义菜单")
    @RequestMapping(value = "/public/get", method = RequestMethod.GET)
    public CommonResult<Object> get(){
        return CommonResult.success(wechatPublicService.getCustomizeMenus());
    }

    /**
     * 创建微信菜单
     * @param data 菜单数据，具体json格式参考微信开放平台
     * @author Mr.Zhang
     * @since 2020-04-22
     */
    @ApiOperation(value = "保存自定义菜单")
    @RequestMapping(value = "/public/create", method = RequestMethod.POST)
    public CommonResult<JSONObject> create(@RequestBody String data){
        return CommonResult.success(wechatPublicService.createMenus(data));
    }

    /**
     * 删除微信菜单
     * @author Mr.Zhang
     * @since 2020-04-22
     */
    @ApiOperation(value = "删除自定义菜单")
    @RequestMapping(value = "/public/delete", method = RequestMethod.GET)
    public CommonResult<JSONObject> delete(){
        return CommonResult.success(wechatPublicService.deleteMenus());
    }

    //自定义菜单

    /**
     * 获取微信菜单
     * @author Mr.Zhang
     * @since 2020-04-22
     */
    @ApiOperation(value = "获取个性化菜单")
    @RequestMapping(value = "/public/self/get", method = RequestMethod.GET)
    @ApiImplicitParam(name="isAsync", value="是否同步微信数据, true是，false否")
    public CommonResult<Object> getSelf(@RequestParam(name = "isAsync", defaultValue = "false") Boolean isAsync){
        Object list = redisUtil.get(WeChatConstants.REDIS_PUBLIC_MENU_SELF_KEY);
        if(list == null || list.equals("") || isAsync){
            //如果没有， 去读取
            JSONObject tagsList = weChatService.getSelf();
            redisUtil.set(WeChatConstants.REDIS_PUBLIC_MENU_SELF_KEY, tagsList);
            list = tagsList;
        }
        return CommonResult.success(list);
    }

    /**
     * 创建微信菜单
     * @param data 菜单数据，具体json格式参考微信开放平台
     * @author Mr.Zhang
     * @since 2020-04-22
     */
    @ApiOperation(value = "保存个性化菜单")
    @RequestMapping(value = "/public/self/create", method = RequestMethod.POST)
    public CommonResult<JSONObject> createSelf(@RequestBody String data){
        return CommonResult.success(weChatService.createSelf(data));
    }

    /**
     * 删除微信菜单
     * @param menuId 菜单id
     * @author Mr.Zhang
     * @since 2020-04-22
     */
    @ApiOperation(value = "删除个性化菜单")
    @RequestMapping(value = "/public/self/delete", method = RequestMethod.GET)

    //参数说明
    @ApiImplicitParam(name="menuId",value="菜单ID")
    public CommonResult<JSONObject> deleteSelf(@RequestParam(name = "menuId") String menuId){
        return CommonResult.success(weChatService.deleteSelf(menuId));
    }
}



