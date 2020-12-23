package com.zbkj.crmeb.pub.controller;

import com.common.CommonResult;
import com.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


/**
 * 后台管理员表 前端控制器
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
@RequestMapping("api/public/test/redis")
@Api(tags = "Redis测试操作")
public class RedisTestController {

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 新增
     * @param key string key
     * @param value string value
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/set", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="key", value="redis key"),
            @ApiImplicitParam(name="value", value="内容"),
    })
    public CommonResult<Object> set(@RequestParam(name = "key", required = true) String key,
                                    @RequestParam(name = "value", required = true) String value){
        redisUtil.set(key, value);
        Object val = redisUtil.get(key);
        return CommonResult.success(val);
    }

    /**
     * 查询
     * @param key string key
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "查询")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ApiImplicitParam(name="key", value="redis key")
    public CommonResult<Object> get(@RequestParam(name = "key", required = true) String key){
        Object value = redisUtil.get(key);
        return CommonResult.success(value);
    }

    /**
     * 删除
     * @param key string key
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiImplicitParam(name="key", value="redis key")
    public CommonResult<Object> delete(@RequestParam(name = "key", required = true) String key){
        redisUtil.remove(key);
        return CommonResult.success();
    }
}



