package com.zbkj.crmeb.front.controller;


import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.zbkj.crmeb.front.response.IndexInfoResponse;
import com.zbkj.crmeb.front.response.IndexProductBannerResponse;
import com.zbkj.crmeb.front.service.IndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

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
@RestController("IndexController")
@RequestMapping("api/front")
@Api(tags = "首页")
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 首页产品的轮播图和产品信息
     * @author Mr.Zhang
     * @since 2020-06-02
     */
    @ApiOperation(value = "首页产品的轮播图和产品信息")
    @RequestMapping(value = "/groom/list/{type}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】", dataType = "int", required = true)
    public CommonResult<IndexProductBannerResponse> getProductBanner(@PathVariable(value = "type") int type, PageParamRequest pageParamRequest){
        if(type < Constants.INDEX_RECOMMEND_BANNER || type > Constants.INDEX_BENEFIT_BANNER){
            return CommonResult.validateFailed();
        }
        return CommonResult.success(indexService.getProductBanner(type, pageParamRequest));
    }

    /**
     * 首页数据
     * @author Mr.Zhang
     * @since 2020-06-02
     */
    @ApiOperation(value = "首页数据")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<IndexInfoResponse> getIndexInfo(){
        return CommonResult.success(indexService.getIndexInfo());
    }

    /**
     * 热门搜索
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @ApiOperation(value = "热门搜索")
    @RequestMapping(value = "/search/keyword", method = RequestMethod.GET)
    public CommonResult<List<HashMap<String, Object>>> hotKeywords(){
        return CommonResult.success(indexService.hotKeywords());
    }

    /**
     * 分享配置
     * @author Mr.Zhang
     * @since 2020-05-25
     */
    @ApiOperation(value = "分享配置")
    @RequestMapping(value = "/share", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> share(){
        return CommonResult.success(indexService.getShareConfig());
    }

    /**
     * 公共配置 云智服
     * @return 公共配置
     */
    @ApiOperation(value = "公共配置")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public CommonResult<HashMap<String,String>> getConfig(){
        return CommonResult.success(indexService.getCommConfig());
    }
}



