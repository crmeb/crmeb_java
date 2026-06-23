package com.zbkj.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.CategoryConstants;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.model.category.Category;
import com.zbkj.common.model.theme.Theme;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CategorySearchRequest;
import com.zbkj.common.response.theme.ThemeCategoryTreeResponse;
import com.zbkj.common.response.theme.ThemeArticleCategoryResponse;
import com.zbkj.common.response.theme.ThemeArticleListResponse;
import com.zbkj.common.response.theme.ThemeProductResponse;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.theme.ThemeImageRequest;
import com.zbkj.common.request.theme.ThemeImportRequest;
import com.zbkj.common.request.theme.ThemeSaveRequest;
import com.zbkj.common.request.theme.ThemeSearchRequest;
import com.zbkj.common.request.theme.ThemeTitleInfoRequest;
import com.zbkj.common.response.theme.ThemeTextFieldResponse;
import com.zbkj.common.response.theme.ThemeResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.CategoryTreeVo;
import com.zbkj.service.service.ThemeService;
import com.zbkj.service.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 主题表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/theme")
@Api(tags = "主题管理")
public class ThemeController {

    @Autowired
    private ThemeService themeService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页显示主题列表
     *
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @return 主题列表
     */
    @PreAuthorize("hasAuthority('admin:theme:list')")
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ThemeResponse>> getList(@Validated ThemeSearchRequest request,
                                                           @Validated PageParamRequest pageParamRequest) {
        CommonPage<ThemeResponse> themeCommonPage = CommonPage.restPage(themeService.getList(request, pageParamRequest));
        return CommonResult.success(themeCommonPage);
    }

    /**
     * 删除主题
     *
     * @param id 主题ID
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('admin:theme:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (themeService.delete(id)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 保存主题标题信息，id为0时新增
     *
     * @param id 主题ID，0表示新增
     * @param tid 复制的模板主题ID，0表示不复制
     * @param request 标题信息
     * @return 主题ID
     */
    @PreAuthorize("hasAuthority('admin:theme:save:title')")
    @ApiOperation(value = "保存标题信息")
    @RequestMapping(value = "/saveTitleInfo", method = RequestMethod.POST)
    public CommonResult<Integer> saveTitleInfo(@RequestParam(value = "id", required = false, defaultValue = "0") Integer id,
                                               @RequestParam(value = "tid", required = false, defaultValue = "0") Integer tid,
                                               @RequestBody @Validated ThemeTitleInfoRequest request) {
        return CommonResult.success(themeService.saveTitleInfo(id, tid, request));
    }

    /**
     * 保存主题模块数据，id为0时新增
     *
     * @param id 主题ID，0表示新增
     * @param request 模块保存参数
     * @return 主题ID
     */
    @PreAuthorize("hasAuthority('admin:theme:save:theme')")
    @ApiOperation(value = "保存主题")
    @RequestMapping(value = "/saveTheme", method = RequestMethod.POST)
    public CommonResult<JSONObject> saveTheme(@RequestParam(value = "id", required = false, defaultValue = "0") Integer id,
                                              @RequestBody @Validated ThemeSaveRequest request) {
        Integer themeId = themeService.saveTheme(id, request);
        JSONObject result = new JSONObject();
        result.put("id", themeId);
        return CommonResult.success(result);
    }

    /**
     * 保存主题图片，id为0时新增
     *
     * @param id 主题ID，0表示新增
     * @param request 图片保存参数
     * @return 主题ID
     */
    @PreAuthorize("hasAuthority('admin:theme:save:image')")
    @ApiOperation(value = "保存主题图片")
    @RequestMapping(value = "/saveThemeImage", method = RequestMethod.POST)
    public CommonResult<JSONObject> saveThemeImage(@RequestParam(value = "id", required = false, defaultValue = "0") Integer id,
                                                   @RequestBody @Validated ThemeImageRequest request) {
        Integer themeId = themeService.saveThemeImage(id, request);
        JSONObject result = new JSONObject();
        result.put("id", themeId);
        return CommonResult.success(result);
    }

    /**
     * 使用主题
     *
     * @param id 主题ID
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('admin:theme:use')")
    @ApiOperation(value = "使用主题")
    @RequestMapping(value = "/use/{id}", method = RequestMethod.GET)
    public CommonResult<String> useTheme(@PathVariable(value = "id") Integer id) {
        if (themeService.useTheme(id)) {
            return CommonResult.success("使用成功");
        }
        return CommonResult.failed();
    }

    /**
     * 使用主题数据
     *
     * @param id 目标主题ID
     * @param themeId 源主题ID
     * @param type 模块类型：home/category/detail/user/theme
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('admin:theme:use:data')")
    @ApiOperation(value = "使用主题数据")
    @RequestMapping(value = "/use_data/{id}", method = RequestMethod.GET)
    public CommonResult<String> useThemeData(@PathVariable(value = "id") Integer id,
                                             @RequestParam(value = "theme_id", required = false, defaultValue = "0") Integer themeId,
                                             @RequestParam(value = "type", required = false, defaultValue = "") String type) {
        if (themeService.useThemeData(id, themeId, type)) {
            return CommonResult.success("使用成功");
        }
        return CommonResult.failed();
    }

    /**
     * 还原主题默认配置
     *
     * @param id 主题ID
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('admin:theme:restore')")
    @ApiOperation(value = "还原主题")
    @RequestMapping(value = "/restore/{id}", method = RequestMethod.GET)
    public CommonResult<String> restoreTheme(@PathVariable(value = "id") Integer id) {
        if (themeService.restoreTheme(id)) {
            return CommonResult.success("还原成功");
        }
        return CommonResult.failed();
    }

    /**
     * 获取正在使用的主题
     *
     * @return 正在使用的主题信息
     */
    @PreAuthorize("hasAuthority('admin:theme:using')")
    @ApiOperation(value = "正在使用的主题")
    @RequestMapping(value = "/using", method = RequestMethod.GET)
    public CommonResult<JSONObject> getUsingTheme() {
        return CommonResult.success(themeService.getUsingTheme());
    }

    /**
     * 查询主题详情数据，id为0时查询当前启用主题
     *
     * @param id 主题ID，0表示当前启用主题
     * @param type 数据类型：home/category/detail/user/theme
     * @return 对应JSON对象数据
     */
    @PreAuthorize("hasAuthority('admin:theme:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<JSONObject> info(@RequestParam(value = "id", required = false) Integer id,
                                         @RequestParam(value = "type", required = false) String type) {
        return CommonResult.success(themeService.getInfoData(id, type));
    }

    /**
     * 查询主题版本号，id为0时查询当前启用主题
     *
     * @param id 主题ID，0或不传表示当前启用主题
     * @return 主题版本号
     */
    @PreAuthorize("hasAuthority('admin:theme:version')")
    @ApiOperation(value = "版本号")
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public CommonResult<String> version(@RequestParam(value = "id", required = false) Integer id) {
        return CommonResult.success(themeService.getVersion(id));
    }

    /**
     * 导出主题数据包
     *
     * @param id 主题ID
     * @return ZIP下载地址
     */
    @PreAuthorize("hasAuthority('admin:theme:export')")
    @ApiOperation(value = "导出主题")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public CommonResult<JSONObject> export(@RequestParam(value = "id") Integer id) {
        Integer recordId = themeService.createExportRecord(id);
        JSONObject result = new JSONObject();
        result.put("record_id", recordId);
        return CommonResult.success(result);
    }

    /**
     * 查询主题导出下载地址
     *
     * @param recordId 下载记录ID
     * @return ZIP下载地址
     */
    @PreAuthorize("hasAuthority('admin:theme:export')")
    @ApiOperation(value = "查询主题导出下载地址")
    @RequestMapping(value = "/export/url", method = RequestMethod.GET)
    public CommonResult<JSONObject> exportUrl(@RequestParam(value = "record_id") Integer recordId) {
        String downloadUrl = themeService.getExportDownloadUrl(recordId);
        JSONObject result = new JSONObject();
        result.put("download_url", downloadUrl);
        result.put("downloadUrl", downloadUrl);
        return CommonResult.success(result);
    }

    /**
     * 导入主题数据包
     *
     * @param request 导入参数
     * @return 导入后的主题ID
     */
    @PreAuthorize("hasAuthority('admin:theme:import')")
    @ApiOperation(value = "导入主题")
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public CommonResult<JSONObject> importTheme(@RequestBody @Validated ThemeImportRequest request) {
        Integer themeId = themeService.importTheme(request);
        JSONObject result = new JSONObject();
        result.put("theme_id", themeId);
        result.put("themeId", themeId);
        return CommonResult.success(result);
    }

    /**
     * 获取DIY文本字段配置
     *
     * @return 文本字段配置
     */
    @PreAuthorize("hasAuthority('admin:pagediy:info')")
    @ApiOperation(value = "文本字段")
    @RequestMapping(value = "/text/field", method = RequestMethod.GET)
    public CommonResult<ThemeTextFieldResponse> textField() {
        return CommonResult.success(themeService.getTextField());
    }

    /**
     * 获取产品分类树
     *
     * @return 产品分类树
     */
    @PreAuthorize("hasAuthority('admin:theme:info')")
    @ApiOperation(value = "产品分类")
    @RequestMapping(value = "/product/category", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "-1=全部，0=未生效，1=已生效", example = "1"),
            @ApiImplicitParam(name = "name", value = "模糊搜索", example = "电视")
    })
    public CommonResult<List<ThemeCategoryTreeResponse>> productCategory(@RequestParam(value = "status", required = false, defaultValue = "1") Integer status,
                                                                         @RequestParam(value = "name", required = false) String name) {
        List<CategoryTreeVo> categoryTreeVos = categoryService.getListTree(Constants.CATEGORY_TYPE_PRODUCT, status, name);
        if (categoryTreeVos == null || categoryTreeVos.isEmpty()) {
            return CommonResult.success(new ArrayList<ThemeCategoryTreeResponse>());
        }
        List<ThemeCategoryTreeResponse> categories = categoryTreeVos.stream()
                .map(this::toThemeCategoryTree)
                .collect(Collectors.toList());
        return CommonResult.success(categories);
    }

    private ThemeCategoryTreeResponse toThemeCategoryTree(CategoryTreeVo categoryTreeVo) {
        ThemeCategoryTreeResponse response = new ThemeCategoryTreeResponse();
        response.setId(categoryTreeVo.getId());
        response.setValue(categoryTreeVo.getId());
        response.setLabel(categoryTreeVo.getName());
        response.setTitle(categoryTreeVo.getName());
        response.setPid(categoryTreeVo.getPid());
        if (categoryTreeVo.getChild() != null && !categoryTreeVo.getChild().isEmpty()) {
            List<ThemeCategoryTreeResponse> children = categoryTreeVo.getChild().stream()
                    .map(this::toThemeCategoryTree)
                    .collect(Collectors.toList());
            response.setChildren(children);
        }
        return response;
    }

    /**
     * 获取文章分类列表
     *
     * @return 文章分类列表
     */
    @PreAuthorize("hasAuthority('admin:theme:info')")
    @ApiOperation(value = "文章分类")
    @RequestMapping(value = "/article/category", method = RequestMethod.GET)
    public CommonResult<List<ThemeArticleCategoryResponse>> articleCategory() {
        CategorySearchRequest request = new CategorySearchRequest();
        request.setType(CategoryConstants.CATEGORY_TYPE_ARTICLE);
        request.setStatus(CategoryConstants.CATEGORY_STATUS_NORMAL);
        List<Category> categoryList = categoryService.getList(request);
        if (categoryList == null || categoryList.isEmpty()) {
            return CommonResult.success(new ArrayList<ThemeArticleCategoryResponse>());
        }
        List<ThemeArticleCategoryResponse> categories = categoryList.stream()
                .map(this::toThemeArticleCategory)
                .collect(Collectors.toList());
        return CommonResult.success(categories);
    }

    private ThemeArticleCategoryResponse toThemeArticleCategory(Category category) {
        ThemeArticleCategoryResponse response = new ThemeArticleCategoryResponse();
        response.setId(category.getId());
        response.setPid(category.getPid());
        response.setTitle(category.getName());
        return response;
    }

    /**
     * 获取文章列表
     *
     * @param cid 文章分类ID
     * @param pageParamRequest 分页参数
     * @return 文章列表
     */
    @PreAuthorize("hasAuthority('admin:theme:info')")
    @ApiOperation(value = "文章列表")
    @RequestMapping(value = "/article/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", value = "文章分类id", example = "1"),
            @ApiImplicitParam(name = "page", value = "页码", example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页数量", example = "10")
    })
    public CommonResult<ThemeArticleListResponse> articleList(@RequestParam(value = "cid") String cid,
                                                              @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(themeService.getArticleList(cid, pageParamRequest));
    }

    /**
     * 获取商品列表
     *
     * @param pageParamRequest 分页参数
     * @param priceOrder 价格排序
     * @param salesOrder 销量排序
     * @param cateId 分类ID
     * @param storeName 商品名称
     * @return 商品列表
     */
    @PreAuthorize("hasAuthority('admin:theme:info')")
    @ApiOperation(value = "商品列表")
    @RequestMapping(value = "/product/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", example = "1"),
            @ApiImplicitParam(name = "limit", value = "每页数量", example = "10"),
            @ApiImplicitParam(name = "priceOrder", value = "价格排序 asc/desc", example = ""),
            @ApiImplicitParam(name = "salesOrder", value = "销量排序 asc/desc", example = ""),
            @ApiImplicitParam(name = "cate_id", value = "分类id", example = ""),
            @ApiImplicitParam(name = "store_name", value = "商品名称", example = "")
    })
    public CommonResult<List<ThemeProductResponse>> productList(@Validated PageParamRequest pageParamRequest,
                                                                @RequestParam(value = "priceOrder", required = false) String priceOrder,
                                                                @RequestParam(value = "salesOrder", required = false) String salesOrder,
                                                                @RequestParam(value = "cate_id", required = false) String cateId,
                                                                @RequestParam(value = "store_name", required = false) String storeName) {
        return CommonResult.success(themeService.getProductList(priceOrder, salesOrder, cateId, storeName, pageParamRequest));
    }
}
