package com.zbkj.crmeb.article.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.article.dao.ArticleDao;
import com.zbkj.crmeb.article.model.Article;
import com.zbkj.crmeb.article.request.ArticleSearchRequest;
import com.zbkj.crmeb.article.service.ArticleService;
import com.zbkj.crmeb.article.vo.ArticleVo;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.front.response.ArticleResponse;
import com.zbkj.crmeb.system.service.SystemConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.constants.Constants.ARTICLE_BANNER_LIMIT;

/**
* ArticleServiceImpl 接口实现
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
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, Article> implements ArticleService {

    private Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Resource
    private ArticleDao dao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SystemConfigService systemConfigService;
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

    @Override
    public PageInfo<ArticleVo> getAdminList(ArticleSearchRequest request, PageParamRequest pageParamRequest) {
        Page<Article> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Article> lambdaQueryWrapper = Wrappers.lambdaQuery();

        if(StringUtils.isNotBlank(request.getCid())){
            lambdaQueryWrapper.eq(Article::getCid, request.getCid());
        }

        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i.or().like(Article::getTitle, request.getKeywords())
                    .or().like(Article::getAuthor, request.getKeywords())
                    .or().like(Article::getSynopsis, request.getKeywords())
                    .or().like(Article::getShareTitle, request.getKeywords())
                    .or().like(Article::getShareSynopsis, request.getKeywords()));
        }

        lambdaQueryWrapper.orderByDesc(Article::getSort).orderByDesc(Article::getVisit).orderByDesc(Article::getCreateTime);
        List<Article> articleList = dao.selectList(lambdaQueryWrapper);

        ArrayList<ArticleVo> articleVoArrayList = new ArrayList<>();
        if(articleList.size() < 1){
            return CommonPage.copyPageInfo(articlePage, articleVoArrayList);
        }

        for (Article article : articleList) {
            ArticleVo articleVo = new ArticleVo();
            BeanUtils.copyProperties(article, articleVo);
            if(!StringUtils.isBlank(article.getImageInput()) ){
                articleVo.setImageInput(CrmebUtil.jsonToListString(article.getImageInput()));
                articleVo.setImageInputs(article.getImageInput());
            }
            articleVoArrayList.add(articleVo);
        }

        return CommonPage.copyPageInfo(articlePage, articleVoArrayList);
    }

    /**
     * 关联产品
     * @param id Integer
     * @param productId 产品id
     * @author Mr.Zhang
     * @since 2020-04-18
     * @return bool
     */
    @Override
    public boolean update(Integer id, Integer productId) {
        Article article = new Article();
        article.setId(id);
        article.setProductId(productId);
        updateById(article);
        return true;
    }

    /**
     * 查询文章详情
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     * @return ArticleVo
     */
    @Override
    public ArticleResponse getVoByFront(Integer id) {
        Article article = getById(id);
        if (ObjectUtil.isNull(article)) {
            throw new CrmebException("文章不存在");
        }

        if(article.getStatus()){
            throw new CrmebException("文章不存在");
        }

        ArticleResponse articleResponse = new ArticleResponse();
        BeanUtils.copyProperties(article, articleResponse);

        try {
            String visit = Optional.ofNullable(article.getVisit()).orElse("0");
            int num = Integer.parseInt(visit) + 1;
            article.setVisit(String.valueOf(num));
            dao.updateById(article);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查看文章详情，更新浏览量失败，errorMsg = " + e.getMessage());
        }
        return articleResponse;
    }

    /**
     * 获取移动端banner列表
     * @return List<Article>
     */
    @Override
    public List<Article> getBannerList() {
        // 根据配置控制banner的数量
        String articleBannerLimitString = systemConfigService.getValueByKey(ARTICLE_BANNER_LIMIT);
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
}

