package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.zbkj.crmeb.article.request.ArticleSearchRequest;
import com.zbkj.crmeb.article.service.ArticleService;
import com.zbkj.crmeb.article.vo.ArticleVo;
import com.zbkj.crmeb.category.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 文章
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
@RestController("ArticleFrontController")
@RequestMapping("api/front/article")
@Api(tags = "文章")

public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页列表
     * @param cid String 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list/{cid}", method = RequestMethod.GET)
    public CommonResult<CommonPage<ArticleVo>>  getList(@PathVariable(name="cid") String cid,
                                                      @Validated PageParamRequest pageParamRequest){
        ArticleSearchRequest request = new ArticleSearchRequest();
        request.setCid(cid);
        request.setHide(false);
        request.setStatus(false);
        CommonPage<ArticleVo> articleCommonPage = CommonPage.restPage(articleService.getList(request, pageParamRequest));
        return CommonResult.success(articleCommonPage);
    }

    /**
     * 热门列表
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "热门列表")
    @RequestMapping(value = "/hot/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ArticleVo>>  getHotList(){
        ArticleSearchRequest request = new ArticleSearchRequest();
        request.setIsHot(true);
        request.setHide(false);
        request.setStatus(false);
        CommonPage<ArticleVo> articleCommonPage = CommonPage.restPage(articleService.getList(request, new PageParamRequest()));
        return CommonResult.success(articleCommonPage);
    }

    /**
     * 轮播列表
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "轮播列表")
    @RequestMapping(value = "/banner/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ArticleVo>>  getList(){
        ArticleSearchRequest request = new ArticleSearchRequest();
        request.setIsBanner(true);
        request.setHide(false);
        request.setStatus(false);
        CommonPage<ArticleVo> articleCommonPage = CommonPage.restPage(articleService.getList(request, new PageParamRequest()));
        return CommonResult.success(articleCommonPage);
    }

    /**
     * 分类列表
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "分类列表")
    @RequestMapping(value = "/category/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<com.zbkj.crmeb.category.vo.CategoryTreeVo>> categoryList(){
        return CommonResult.success(CommonPage.restPage(categoryService.getListTree(Constants.CATEGORY_TYPE_ARTICLE, 1,"")));
    }

    /**
     * 查询文章详情
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="文章ID")
    public CommonResult<ArticleVo> info(@RequestParam(value = "id") Integer id){
        return CommonResult.success(articleService.getVoByFront(id));
   }
}



