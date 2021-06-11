package com.zbkj.crmeb.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.article.model.Article;
import com.zbkj.crmeb.article.request.ArticleSearchRequest;
import com.zbkj.crmeb.article.vo.ArticleVo;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.front.response.ArticleResponse;

import java.util.List;

/**
*  ArticleService 接口
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
public interface ArticleService extends IService<Article> {

    /**
     * 文章列表
     * @param cid 文章分类id
     * @param pageParamRequest 分页类参数
     * @return PageInfo<Article>
     */
    PageInfo<ArticleResponse> getList(String cid, PageParamRequest pageParamRequest);

    PageInfo<ArticleVo> getAdminList(ArticleSearchRequest request, PageParamRequest pageParamRequest);

    boolean update(Integer id, Integer productId);

    /**
     * 文章详情
     * @param id 文章id
     * @return ArticleVo
     */
    ArticleResponse getVoByFront(Integer id);

    /**
     * 获取移动端banner列表
     * @return List<Article>
     */
    List<Article> getBannerList();

    /**
     * 获取移动端热门列表
     * @return List<ArticleResponse>
     */
    List<ArticleResponse> getHotList();

    /**
     * 获取文章分类列表
     * @return List<Category>
     */
    List<Category> getCategoryList();
}
