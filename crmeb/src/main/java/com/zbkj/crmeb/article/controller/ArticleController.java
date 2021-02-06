package com.zbkj.crmeb.article.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.article.model.Article;
import com.zbkj.crmeb.article.request.ArticleRequest;
import com.zbkj.crmeb.article.request.ArticleSearchRequest;
import com.zbkj.crmeb.article.service.ArticleService;
import com.zbkj.crmeb.article.vo.ArticleVo;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 文章管理表 前端控制器
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
@RestController
@RequestMapping("api/admin/article")
@Api(tags = "文章管理")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 分页显示文章管理表
     * @param request ArticleSearchRequest 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParam(name="keywords", value="搜索关键字")
    public CommonResult<CommonPage<ArticleVo>> getList(@Validated ArticleSearchRequest request,
                                                       @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(articleService.getList(request, pageParamRequest)));
    }

    /**
     * 新增文章管理表
     * @param articleRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ArticleRequest articleRequest){
        Article article = new Article();
        BeanUtils.copyProperties(articleRequest, article);
        article.setImageInput(systemAttachmentService.clearPrefix(article.getImageInput()));
        article.setContent(systemAttachmentService.clearPrefix(article.getContent()));

        if(articleService.save(article)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除文章管理表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="文章ID")
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(articleService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 关联产品
     * @param id Integer
     * @param productId 产品id
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "绑定产品")
    @RequestMapping(value = "/bind/product", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="文章ID"),
            @ApiImplicitParam(name="productId", value="产品id")
    })
    public CommonResult<String> bind(@RequestParam(value = "id") Integer id,
                                       @RequestParam(value = "productId") Integer productId){
        if(articleService.update(id, productId)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改文章管理表
     * @param id integer id
     * @param articleRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name="id", value="文章ID")
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated ArticleRequest articleRequest){
        Article article = new Article();
        BeanUtils.copyProperties(articleRequest, article);
        article.setId(id);
        article.setImageInput(systemAttachmentService.clearPrefix(article.getImageInput()));
        article.setContent(systemAttachmentService.clearPrefix(article.getContent()));

        if(articleService.updateById(article)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询文章管理表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="文章ID")
    public CommonResult<Article> info(@RequestParam(value = "id") Integer id){
        Article article = articleService.getById(id);
        return CommonResult.success(article);
   }
}



