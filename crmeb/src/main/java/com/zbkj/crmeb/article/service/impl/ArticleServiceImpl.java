package com.zbkj.crmeb.article.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Resource
    private ArticleDao dao;

    @Autowired
    private CategoryService categoryService;


    /**
    * 列表
    * @param request ArticleSearchRequest 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-18
    * @return List<Article>
    */
    @Override
    public PageInfo<ArticleVo> getList(ArticleSearchRequest request, PageParamRequest pageParamRequest) {
        Page<Article> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();

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

        if(request.getIsBanner() != null){
            lambdaQueryWrapper.eq(Article::getIsBanner, request.getIsBanner());
        }

        if(request.getIsHot() != null){
            lambdaQueryWrapper.eq(Article::getIsHot, request.getIsHot());
        }

        if(request.getHide() != null){
            lambdaQueryWrapper.eq(Article::getHide, request.getHide());
        }

        if(request.getStatus() != null){
            lambdaQueryWrapper.eq(Article::getStatus, request.getStatus());
        }

        if(null != request.getIsHaveMediaId()){
            lambdaQueryWrapper.isNotNull(Article::getMediaId).ne(Article::getMediaId, "");
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
    public ArticleVo getVoByFront(Integer id) {
        Article article = getById(id);
        if (ObjectUtil.isNull(article)) {
            throw new CrmebException("文章不存在");
        }

        if(article.getStatus()){
            throw new CrmebException("文章不存在");
        }

        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        if(!StringUtils.isBlank(article.getImageInput())) {
            articleVo.setImageInput(CrmebUtil.jsonToListString(article.getImageInput()));
        }

        //分类名称
        Category category = categoryService.getById(article.getCid());
        if(null != category){
            articleVo.setCategoryName(category.getName());
        }
        String visit = Optional.ofNullable(article.getVisit()).orElse("");
        int num;
        if (StrUtil.isBlank(visit)) {
            num = 0;
        } else {
            num = Integer.parseInt(visit) + 1;
        }
        article.setVisit(String.valueOf(num));
        dao.updateById(article);
        return articleVo;
    }
}

