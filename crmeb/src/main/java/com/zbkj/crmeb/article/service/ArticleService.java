package com.zbkj.crmeb.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.article.model.Article;
import com.zbkj.crmeb.article.request.ArticleSearchRequest;
import com.zbkj.crmeb.article.vo.ArticleVo;

/**
* @author Mr.Zhang
* @Description ArticleService 接口
* @since 2020-04-18
*/
public interface ArticleService extends IService<Article> {

    PageInfo<ArticleVo> getList(ArticleSearchRequest request, PageParamRequest pageParamRequest);

    boolean update(Integer id, Integer productId);

    ArticleVo getVoByFront(Integer id);
}
