package com.zbkj.service.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.theme.Theme;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.theme.ThemeImageRequest;
import com.zbkj.common.request.theme.ThemeImportRequest;
import com.zbkj.common.request.theme.ThemeSaveRequest;
import com.zbkj.common.request.theme.ThemeSearchRequest;
import com.zbkj.common.request.theme.ThemeTitleInfoRequest;
import com.zbkj.common.response.theme.ThemeResponse;
import com.zbkj.common.response.theme.ThemeArticleListResponse;
import com.zbkj.common.response.theme.ThemeProductResponse;
import com.zbkj.common.response.theme.ThemeTextFieldResponse;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 主题服务
 */
public interface ThemeService extends IService<Theme> {

    /**
     * 分页列表
     *
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return 主题列表
     */
    PageInfo<ThemeResponse> getList(ThemeSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 保存主题标题信息，id为0时新增。
     *
     * @param id 主题ID
     * @param tid 复制的模板主题ID
     * @param request 标题信息
     * @return 主题ID
     */
    Integer saveTitleInfo(Integer id, Integer tid, ThemeTitleInfoRequest request);

    /**
     * 保存主题模块数据，id为0时新增。
     *
     * @param id 主题ID
     * @param request 保存参数
     * @return 主题ID
     */
    Integer saveTheme(Integer id, ThemeSaveRequest request);

    /**
     * 保存主题图片，id为0时新增。
     *
     * @param id 主题ID
     * @param request 图片保存参数
     * @return 主题ID
     */
    Integer saveThemeImage(Integer id, ThemeImageRequest request);

    /**
     * 使用主题，切换当前启用主题。
     *
     * @param id 主题ID
     * @return 是否成功
     */
    Boolean useTheme(Integer id);

    /**
     * 使用主题数据，将源主题的指定模块复制到目标主题。
     *
     * @param id 目标主题ID
     * @param themeId 源主题ID
     * @param type 模块类型：home/category/detail/user/theme
     * @return 是否成功
     */
    Boolean useThemeData(Integer id, Integer themeId, String type);

    /**
     * 还原主题默认配置。
     *
     * @param id 主题ID
     * @return 是否成功
     */
    Boolean restoreTheme(Integer id);

    /**
     * 获取正在使用的主题。
     *
     * @return 正在使用的主题信息
     */
    JSONObject getUsingTheme();

    /**
     * 软删除主题
     *
     * @param id 主题ID
     * @return 是否成功
     */
    Boolean delete(Integer id);

    /**
     * 查询主题详情，id为0时查询当前启用主题
     *
     * @param id 主题ID
     * @return 主题详情
     */
    Theme getInfo(Integer id);

    /**
     * 查询主题详情数据，id为0时查询当前启用主题。
     *
     * @param id 主题ID
     * @param type 数据类型：home/category/detail/user/theme
     * @return 对应JSON对象数据
     */
    JSONObject getInfoData(Integer id, String type);

    /**
     * 查询主题版本号，id为0时查询当前启用主题。
     *
     * @param id 主题ID
     * @return 主题版本号
     */
    String getVersion(Integer id);

    /**
     * 导出主题数据包。
     *
     * @param id 主题ID
     * @return ZIP下载地址
     */
    String exportTheme(Integer id);

    /**
     * 创建主题导出记录并异步导出主题数据包。
     *
     * @param id 主题ID
     * @return 下载记录ID
     */
    Integer createExportRecord(Integer id);

    /**
     * 查询主题导出下载地址。
     *
     * @param recordId 下载记录ID
     * @return ZIP下载地址，未生成时为空字符串
     */
    String getExportDownloadUrl(Integer recordId);

    /**
     * 导入主题数据包。
     *
     * @param request 导入参数
     * @return 导入后的主题ID
     */
    Integer importTheme(ThemeImportRequest request);

    /**
     * 获取主题文本字段配置
     *
     * @return 文本字段配置
     */
    ThemeTextFieldResponse getTextField();

    /**
     * 获取主题商品列表
     *
     * @param priceOrder 价格排序：asc/desc
     * @param salesOrder 销量排序：asc/desc
     * @param cateId 分类ID
     * @param storeName 商品名称
     * @param pageParamRequest 分页参数
     * @return 商品列表
     */
    List<ThemeProductResponse> getProductList(String priceOrder, String salesOrder, String cateId, String storeName, PageParamRequest pageParamRequest);

    /**
     * 获取主题文章列表
     *
     * @param cid 文章分类ID
     * @param pageParamRequest 分页参数
     * @return 文章列表
     */
    ThemeArticleListResponse getArticleList(String cid, PageParamRequest pageParamRequest);

    /**
     * 获取移动端主题信息
     *
     * @param themeId 主题ID，0表示当前启用主题
     * @param type 数据类型：home/category/detail/user/theme/base
     * @return 主题信息
     */
    JSONObject getThemeInfo(Integer themeId, String type);

    /**
     * 获取移动端主题商品列表
     *
     * @param ids 商品ID，多个用英文逗号分隔
     * @param cateIds 商品分类ID，多个用英文逗号分隔
     * @param order 排序字段：0-销量，1-价格，其他-后台排序
     * @param sort 排序方向：0-降序，非0-升序
     * @param limit 返回数量
     * @return 商品列表
     */
    List<JSONObject> getThemeProduct(String ids, String cateIds, Integer order, Integer sort, Integer limit);

    /**
     * 获取移动端主题优惠券列表
     *
     * @param limit 返回数量
     * @return 优惠券列表与类型统计
     */
    JSONObject getThemeCoupon(Integer limit);

    /**
     * 获取移动端主题秒杀商品列表
     *
     * @param limit 返回数量
     * @return 当前正在进行的秒杀商品列表
     */
    List<JSONObject> getThemeSeckill(Integer limit);

    /**
     * 获取移动端主题拼团商品列表
     *
     * @param limit 返回数量
     * @return 当前正在进行的拼团商品列表
     */
    List<JSONObject> getThemeCombination(Integer limit);

    /**
     * 获取移动端主题砍价商品列表
     *
     * @param limit 返回数量
     * @return 当前正在进行的砍价商品列表
     */
    List<JSONObject> getThemeBargain(Integer limit);
}
