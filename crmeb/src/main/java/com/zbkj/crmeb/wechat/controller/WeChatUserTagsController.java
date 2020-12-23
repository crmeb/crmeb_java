package com.zbkj.crmeb.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.CommonResult;
import com.constants.WeChatConstants;
import com.utils.RedisUtil;
import com.zbkj.crmeb.wechat.service.WeChatService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


/**
 * 微信用户表 前端控制器
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
@RequestMapping("api/admin/wechat/user/tags")
@Api(tags = "微信开放平台 -- 微信用户标签")
public class WeChatUserTagsController {

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 标签列表
     * @param isAsync 是否同步，默认读缓存
     * @author Mr.Zhang
     * @since 2020-04-11
     */
    @ApiOperation(value = "列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParam(name="isAsync", value="是否同步微信数据, true是，false否")
    public CommonResult<Object> getList(@RequestParam(name = "isAsync", defaultValue = "false") Boolean isAsync){
        Object list = redisUtil.get(WeChatConstants.REDIS_TAGS_LIST_KEY);
        if(list == null || list.equals("") || isAsync){
            //如果没有， 去读取
            JSONObject tagsList = weChatService.getTagsList();
            redisUtil.set(WeChatConstants.REDIS_TAGS_LIST_KEY, tagsList);
            list = tagsList;
        }
        return CommonResult.success(list);
    }

    /**
     * 创建标签
     * @param name 标签名称
     * @author Mr.Zhang
     * @since 2020-04-11
     */
    @ApiOperation(value = "创建")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ApiImplicitParam(name="name",value="标签名称")
    public CommonResult<JSONObject> save(@RequestParam(name = "name") String name){
        return CommonResult.success(weChatService.createTags(name));
    }

    /**
     * 修改标签
     * @param id 标签id
     * @param name 标签名称
     * @author Mr.Zhang
     * @since 2020-04-11
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="标签ID"),
            @ApiImplicitParam(name="name", value="标签名称")
    })
    public CommonResult<JSONObject> update(@RequestParam(name = "id") String id,
                                           @RequestParam(name = "name") String name){
        return CommonResult.success(weChatService.updateTags(id, name));
    }

    /**
     * 删除标签
     * @param id 标签id
     * @author Mr.Zhang
     * @since 2020-04-11
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="标签ID")
    public CommonResult<JSONObject> delete(@RequestParam(name = "id") String id){
        return CommonResult.success(weChatService.deleteTags(id));
    }

    /**
     * 获取标签下粉丝列表
     * @param id 标签id
     * @param nextOpenId 第一个拉取的OPENID，不填默认从头开始拉取
     * @author Mr.Zhang
     * @since 2020-04-11
     */
    @ApiOperation(value = "获取标签下粉丝列表")
    @RequestMapping(value = "/getUserListById", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="标签ID"),
            @ApiImplicitParam(name="nextOpenId", value="第一个拉取的OPENID，不填默认从头开始拉取")
    })
    public CommonResult<JSONObject> getTagsListById(@RequestParam(name = "id") String id,
                                           @RequestParam(name = "nextOpenId", defaultValue = "") String nextOpenId){
        return CommonResult.success(weChatService.getUserListByTagsId(id, nextOpenId));
    }

    /**
     * 批量为用户打标签
     * @param id 标签id
     * @param openIdList 用户openId数组
     * @author Mr.Zhang
     * @since 2020-04-11
     */
    @ApiOperation(value = "批量为用户打标签")
    @RequestMapping(value = "/memberBatchTags", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="标签ID"),
            @ApiImplicitParam(name="openIdList", value="用户openId数组")
    })
    public CommonResult<JSONObject> memberBatchTags(@RequestParam(name = "id") String id,
                                                    @RequestParam(name = "openIdList") String openIdList){
        return CommonResult.success(weChatService.memberBatchTags(id, openIdList));
    }

    /**
     * 批量为用户取消标签
     * @param id 标签id
     * @param openIdList 用户openId数组
     * @author Mr.Zhang
     * @since 2020-04-11
     */
    @ApiOperation(value = "批量为用户取消标签")
    @RequestMapping(value = "/memberBatchUnTags", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="标签ID"),
            @ApiImplicitParam(name="openIdList", value="用户openId数组")
    })
    public CommonResult<JSONObject> memberBatchUnTags(@RequestParam(name = "id") String id,
                                                    @RequestParam(name = "openIdList") String openIdList){
        return CommonResult.success(weChatService.memberBatchUnTags(id, openIdList));
    }

    /**
     * 获取用户身上的标签列表
     * @param openId 用户id
     * @author Mr.Zhang
     * @since 2020-04-11
     */
    @ApiOperation(value = "获取用户身上的标签列表")
    @RequestMapping(value = "/getTagsListByUserId", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="isAsync", value="是否同步微信数据, true是，false否"),
            @ApiImplicitParam(name="openId", value="用户openId")
    })
    public CommonResult<Object> getTagsListByUserId(@RequestParam(name = "isAsync", defaultValue = "false") Boolean isAsync,
                                                    @RequestParam(name = "openId") String openId){
        String redisKey = WeChatConstants.REDIS_TAGS_LIST_USER_KEY + "_" + openId;
        Object list = redisUtil.get(redisKey);
        if(list == null || list.equals("") || isAsync){
            //如果没有， 去读取
            JSONObject tagsList = weChatService.getTagsListByUserId(openId);
            redisUtil.set(redisKey, tagsList);
            list = tagsList;
        }
        return CommonResult.success(list);
    }

}



