package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.article.Article;
import com.zbkj.common.model.category.Category;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.ArticleRequest;
import com.zbkj.common.request.ArticleSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.ArticleResponse;
import com.zbkj.common.vo.ArticleVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.service.dao.ArticleDao;
import com.zbkj.service.service.ArticleService;
import com.zbkj.service.service.CategoryService;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.SystemConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* ArticleServiceImpl 接口实现
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, Article> implements ArticleService {

    private Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Resource
    private ArticleDao dao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
    * 列表
    * @param cid 文章分类id
    * @param pageParamRequest 分页类参数
    * @return PageInfo<Article>
    */
    @Override
    public PageInfo<ArticleResponse> getList(String cid, PageParamRequest pageParamRequest) {
        Page<Article> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Article> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Article::getCid, cid);
        lambdaQueryWrapper.eq(Article::getHide, false);
        lambdaQueryWrapper.eq(Article::getStatus, false);
        lambdaQueryWrapper.orderByDesc(Article::getSort).orderByDesc(Article::getVisit).orderByDesc(Article::getCreateTime);
        List<Article> articleList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(articleList)) {
            return CommonPage.copyPageInfo(articlePage, CollUtil.newArrayList());
        }
        List<ArticleResponse> responseList = articleList.stream().map(e -> {
            ArticleResponse articleResponse = new ArticleResponse();
            BeanUtils.copyProperties(e, articleResponse);
            return articleResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(articlePage, responseList);
    }

    /**
     * 获取文章列表
     * @param request 请求参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ArticleVo> getAdminList(ArticleSearchRequest request, PageParamRequest pageParamRequest) {
        Page<Article> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Article> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(request.getCid())) {
            lambdaQueryWrapper.eq(Article::getCid, request.getCid());
        }
        if (!StringUtils.isBlank(request.getKeywords())) {
            lambdaQueryWrapper.and(i -> i.or().like(Article::getTitle, request.getKeywords())
                    .or().like(Article::getAuthor, request.getKeywords())
                    .or().like(Article::getSynopsis, request.getKeywords()));
        }
        lambdaQueryWrapper.orderByDesc(Article::getVisit).orderByDesc(Article::getId);
        List<Article> articleList = dao.selectList(lambdaQueryWrapper);

        ArrayList<ArticleVo> articleVoArrayList = new ArrayList<>();
        if (articleList.size() < 1) {
            return CommonPage.copyPageInfo(articlePage, articleVoArrayList);
        }
        for (Article article : articleList) {
            ArticleVo articleVo = new ArticleVo();
            BeanUtils.copyProperties(article, articleVo);
            if (!StrUtil.isBlank(article.getImageInput()) ) {
                articleVo.setImageInput(article.getImageInput());
            }
            articleVoArrayList.add(articleVo);
        }
        return CommonPage.copyPageInfo(articlePage, articleVoArrayList);
    }

    /**
     * 查询文章详情
     * @param id Integer
     * @return ArticleVo
     */
    @Override
    public ArticleResponse getVoByFront(Integer id) {
        Article article = getById(id);
        if (ObjectUtil.isNull(article)) {
            throw new CrmebException("文章不存在");
        }

        if (article.getStatus()) {
            throw new CrmebException("文章不存在");
        }

        ArticleResponse articleResponse = new ArticleResponse();
        BeanUtils.copyProperties(article, articleResponse);

        String visit = StrUtil.isNotBlank(article.getVisit()) ? article.getVisit() : "0";
        int num = Integer.parseInt(visit) + 1;
        article.setVisit(String.valueOf(num));
        dao.updateById(article);
        return articleResponse;
    }

    /**
     * 获取移动端banner列表
     * @return List<Article>
     */
    @Override
    public List<Article> getBannerList() {
        // 根据配置控制banner的数量
        String articleBannerLimitString = systemConfigService.getValueByKey(Constants.ARTICLE_BANNER_LIMIT);
        int articleBannerLimit = Integer.parseInt(articleBannerLimitString);

        LambdaQueryWrapper<Article> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(Article::getId, Article::getImageInput);
        lambdaQueryWrapper.eq(Article::getIsBanner, true);
        lambdaQueryWrapper.eq(Article::getHide, false);
        lambdaQueryWrapper.eq(Article::getStatus, false);
        lambdaQueryWrapper.orderByDesc(Article::getSort);
        lambdaQueryWrapper.last(" limit " + articleBannerLimit);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 获取移动端热门列表
     * @return List<ArticleResponse>
     */
    @Override
    public List<ArticleResponse> getHotList() {
        LambdaQueryWrapper<Article> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(Article::getId, Article::getImageInput, Article::getTitle, Article::getCreateTime);
        lambdaQueryWrapper.eq(Article::getIsHot, true);
        lambdaQueryWrapper.eq(Article::getHide, false);
        lambdaQueryWrapper.eq(Article::getStatus, false);
        lambdaQueryWrapper.orderByDesc(Article::getSort);
        lambdaQueryWrapper.last(" limit 20");
        List<Article> articleList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(articleList)) {
            return CollUtil.newArrayList();
        }
        List<ArticleResponse> responseList = articleList.stream().map(e -> {
            ArticleResponse articleResponse = new ArticleResponse();
            BeanUtils.copyProperties(e, articleResponse);
            return articleResponse;
        }).collect(Collectors.toList());
        return responseList;
    }

    /**
     * 获取文章分类列表(移动端)
     * @return List<Category>
     */
    @Override
    public List<Category> getCategoryList() {
        return categoryService.findArticleCategoryList();
    }

    /**
     * 文章新增
     * @param articleRequest 文章新增参数
     * @return Boolean
     */
    @Override
    public Boolean create(ArticleRequest articleRequest) {
        Article article = new Article();
        BeanUtils.copyProperties(articleRequest, article);
        article.setImageInput(systemAttachmentService.clearPrefix(article.getImageInput()));
        article.setContent(systemAttachmentService.clearPrefix(article.getContent()));
        article.setVisit("0");
        return save(article);
    }

    /**
     * 文章删除
     * @param id 文章id
     * @return Boolean
     */
    @Override
    public Boolean deleteById(Integer id) {
        Article article = getById(id);
        if (ObjectUtil.isNull(article)) {
            throw new CrmebException("文章已删除");
        }
        return removeById(id);
    }

    /**
     * 文章修改
     * @param id 文章id
     * @param articleRequest 文章修改参数
     */
    @Override
    public Boolean updateArticle(Integer id, ArticleRequest articleRequest) {
        Article article = new Article();
        BeanUtils.copyProperties(articleRequest, article);
        article.setId(id);
        article.setImageInput(systemAttachmentService.clearPrefix(article.getImageInput()));
        article.setContent(systemAttachmentService.clearPrefix(article.getContent()));
        return updateById(article);
    }

    /**
     * 获取文章详情
     * @param id 文章id
     * @return Article
     */
    @Override
    public Article getDetail(Integer id) {
        Article article = getById(id);
        if (ObjectUtil.isNull(article)) {
            throw new CrmebException("文章不存在");
        }
        return article;
    }
}

