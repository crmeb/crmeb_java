package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.CouponConstants;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.constants.UploadConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.bargain.StoreBargain;
import com.zbkj.common.model.combination.StoreCombination;
import com.zbkj.common.model.coupon.StoreCoupon;
import com.zbkj.common.model.seckill.StoreSeckill;
import com.zbkj.common.model.seckill.StoreSeckillManger;
import com.zbkj.common.model.system.SystemAttachment;
import com.zbkj.common.response.ArticleResponse;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.model.system.SystemStoreStaff;
import com.zbkj.common.model.theme.Theme;
import com.zbkj.common.model.theme.ThemeDownload;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductRequest;
import com.zbkj.common.request.theme.ThemeImageRequest;
import com.zbkj.common.request.theme.ThemeImportRequest;
import com.zbkj.common.request.theme.ThemeSaveRequest;
import com.zbkj.common.request.theme.ThemeSearchRequest;
import com.zbkj.common.request.theme.ThemeTitleInfoRequest;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.RequestUtil;
import com.zbkj.common.response.theme.ThemeArticleListResponse;
import com.zbkj.common.response.theme.ThemeArticleResponse;
import com.zbkj.common.response.theme.ThemeProductResponse;
import com.zbkj.common.response.theme.ThemeResponse;
import com.zbkj.common.response.theme.ThemeTextFieldItemResponse;
import com.zbkj.common.response.theme.ThemeTextFieldResponse;
import com.zbkj.service.dao.ThemeDao;
import com.zbkj.service.service.ArticleService;
import com.zbkj.service.service.StoreBargainService;
import com.zbkj.service.service.StoreProductService;
import com.zbkj.service.service.StoreCombinationService;
import com.zbkj.service.service.StoreCouponService;
import com.zbkj.service.service.StoreOrderService;
import com.zbkj.service.service.StoreSeckillService;
import com.zbkj.service.service.StoreSeckillMangerService;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.SystemStoreStaffService;
import com.zbkj.service.service.ThemeDownloadService;
import com.zbkj.service.service.ThemeService;
import com.zbkj.service.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

/**
 * 主题服务实现
 */
@Service
public class ThemeServiceImpl extends ServiceImpl<ThemeDao, Theme> implements ThemeService {

    private static final String DEFAULT_THEME_DATA = "{\"theme_color\":\"#E93323\",\"gradient_color\":\"#FF7931\",\"sub_color\":\"#FE960F\",\"light_color\":\"rgba(233, 51, 35, 0.1)\"}";

    private static final String THEME_DOWNLOAD_WEB_PATH = "theme/download/";

    private static final String THEME_DOWNLOAD_FILE_PATH = UploadConstants.UPLOAD_FILE_KEYWORD + "/" + THEME_DOWNLOAD_WEB_PATH;

    private static final String THEME_IMPORT_WEB_PATH = "theme/import/";

    private static final String THEME_IMPORT_FILE_PATH = UploadConstants.UPLOAD_FILE_KEYWORD + "/" + THEME_IMPORT_WEB_PATH;

    private static final String THEME_SAVE_IMAGE_WEB_PATH = UploadConstants.UPLOAD_FILE_KEYWORD + "/public/theme/";

    private static final String TYPE_HOME = "home";

    private static final String TYPE_DETAIL = "detail";

    private static final String TYPE_USER = "user";

    private static final String CONFIG_KEY_ROUTINE_CONTACT_TYPE = "routine_contact_type";

    private static final String CONFIG_KEY_MEMBER_FUNC_STATUS = "member_func_status";

    private static final String CONFIG_KEY_MEMBER_CARD_STATUS = "member_card_status";

    private static final String CONFIG_KEY_BALANCE_FUNC_STATUS = "balance_func_status";

    private static final String CONFIG_KEY_INVOICE_FUNC_STATUS = "invoice_func_status";

    private static final String CONFIG_KEY_VIP_OPEN = "vip_open";

    private static final String MOBILE_ADMIN_ORDER_LINK = "/pages/admin/order/index";

    private static final String MOBILE_ADMIN_MANAGE_LINK = "/pages/admin/manage/index";

    private static final String MOBILE_ADMIN_WRITE_OFF_LINK = "/pages/admin/order_cancellation/index";

    private static final List<String> THEME_MAIN_IMAGES = Arrays.asList(
            "home_image",
            "category_image",
            "detail_image",
            "user_image"
    );

    private static final List<String> THEME_JSON_IMAGE_FIELDS = Arrays.asList(
            "home_data",
            "category_data",
            "detail_data",
            "user_data",
            "theme_data"
    );

    private static final List<String> THEME_IMPORT_DATA_FIELDS = Arrays.asList(
            "home_data",
            "category_data",
            "detail_data",
            "user_data",
            "theme_data",
            "home_default_data",
            "category_default_data",
            "detail_default_data",
            "user_default_data",
            "theme_default_data"
    );

    private static final List<String> THEME_IMPORT_IMAGE_FIELDS = Arrays.asList(
            "home_image",
            "category_image",
            "detail_image",
            "user_image",
            "home_default_image",
            "category_default_image",
            "detail_default_image",
            "user_default_image"
    );

    private static final List<String> THEME_IMAGE_EXTENSIONS = Arrays.asList(
            "jpg",
            "jpeg",
            "png",
            "gif",
            "webp",
            "svg",
            "bmp"
    );

    private static final List<String> THEME_INFO_EXCLUDED_COMPONENT_NAMES = Arrays.asList(
            "signIn",
            "liveBroadcast",
            "pointsMall"
    );

    private static final int THEME_ATTACHMENT_PID = 6;

    private static final int ATTACHMENT_QUERY_BATCH_SIZE = 500;

    private static final List<String> LIST_EXCLUDED_COLUMNS = Arrays.asList(
            "home_data",
            "category_data",
            "detail_data",
            "user_data",
            "theme_data",
            "home_default_data",
            "category_default_data",
            "detail_default_data",
            "user_default_data",
            "theme_default_data"
    );

    @Resource
    private ThemeDao dao;

    @Resource
    private ThemeDownloadService themeDownloadService;

    @Resource
    private CrmebConfig crmebConfig;

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private SystemAttachmentService systemAttachmentService;

    @Resource
    private StoreProductService storeProductService;

    @Resource
    private StoreBargainService storeBargainService;

    @Resource
    private StoreCombinationService storeCombinationService;

    @Resource
    private ArticleService articleService;

    @Resource
    private StoreCouponService storeCouponService;

    @Resource
    private StoreSeckillMangerService storeSeckillMangerService;

    @Resource
    private StoreSeckillService storeSeckillService;

    @Resource
    private StoreOrderService storeOrderService;

    @Resource
    private SystemStoreStaffService systemStoreStaffService;

    @Resource
    private UserService userService;

    /**
     * 分页列表
     *
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return 主题列表
     */
    @Override
    public PageInfo<ThemeResponse> getList(ThemeSearchRequest request, PageParamRequest pageParamRequest) {
        Page<Theme> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Theme> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(Theme.class, field -> !LIST_EXCLUDED_COLUMNS.contains(field.getColumn()));

        if (StrUtil.isNotBlank(request.getTitle())) {
            String title = URLUtil.decode(request.getTitle());
            queryWrapper.and(wrapper -> wrapper
                    .like(Theme::getTitle, title)
                    .or()
                    .like(Theme::getInfo, title));
        }
        if (request.getType() != null) {
            queryWrapper.eq(Theme::getType, request.getType());
        }
        if (StrUtil.isNotBlank(request.getPageType())) {
            queryWrapper.eq(Theme::getPageType, URLUtil.decode(request.getPageType()));
        }
        if (request.getIsDel() != null) {
            queryWrapper.eq(Theme::getIsDel, request.getIsDel());
        }
        if (request.getIsUse() != null) {
            queryWrapper.eq(Theme::getIsUse, request.getIsUse());
        }

        queryWrapper.orderByDesc(Theme::getId);
        List<Theme> themeList = dao.selectList(queryWrapper);
        if (CollUtil.isEmpty(themeList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        Map<String, String> imageDomainMap = buildThemeImageDomainMap(collectThemeListImagePaths(themeList));
        List<ThemeResponse> responseList = themeList.stream()
                .map(theme -> buildThemeResponse(theme, imageDomainMap))
                .collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 获取主题文本字段配置
     *
     * @return 文本字段配置
     */
    @Override
    public ThemeTextFieldResponse getTextField() {
        return new ThemeTextFieldResponse()
                .setUser(Arrays.asList(
                        textFieldItem("用户名称", "nickname"),
                        textFieldItem("用户id", "uid"),
                        textFieldItem("用户头像", "image"),
                        textFieldItem("商品收藏", "collection_num"),
                        textFieldItem("商品加购", "cart_num"),
                        textFieldItem("订单总数", "order_num"),
                        textFieldItem("我的积分", "integral"),
                        textFieldItem("我的余额", "now_money"),
                        textFieldItem("我的佣金", "brokerage_price"),
                        textFieldItem("未读消息", "unread_msg_num")
                ))
                .setArticle(Arrays.asList(
                        textFieldItem("文章标题", "title"),
                        textFieldItem("文章id", "id"),
                        textFieldItem("文章封面", "image"),
                        textFieldItem("文章分类", "cid_name"),
                        textFieldItem("文章简介", "synopsis"),
                        textFieldItem("文章浏览量", "visit"),
                        textFieldItem("添加时间", "add_time")
                ))
                .setCoupon(Arrays.asList(
                        textFieldItem("优惠券名称", "coupon_title"),
                        textFieldItem("优惠券id", "id"),
                        textFieldItem("优惠券类型", "type"),
                        textFieldItem("优惠券面值", "coupon_price"),
                        textFieldItem("优惠券状态", "status"),
                        textFieldItem("领取时间", "receive_time"),
                        textFieldItem("使用时间", "use_time"),
                        textFieldItem("使用门槛", "use_min_price"),
                        textFieldItem("发放数量", "receive_count"),
                        textFieldItem("添加时间", "add_time")
                ))
                .setProduct(Arrays.asList(
                        textFieldItem("商品名称", "store_name"),
                        textFieldItem("商品id", "id"),
                        textFieldItem("商品图片", "image"),
                        textFieldItem("商品简介", "store_info"),
                        textFieldItem("商品单位", "unit_name"),
                        textFieldItem("商品分类", "cate_name"),
                        textFieldItem("商品库存", "stock"),
                        textFieldItem("商品售价", "price"),
                        textFieldItem("商品最高售价", "max_price"),
                        textFieldItem("商品最低售价", "min_price"),
                        textFieldItem("商品原价", "ot_price"),
                        textFieldItem("商品最高原价", "max_ot_price"),
                        textFieldItem("商品最低原价", "min_ot_price"),
                        textFieldItem("商品起购数量", "min_qty"),
                        textFieldItem("商品销量", "sales"),
                        textFieldItem("商品访问量", "browse"),
                        textFieldItem("商品添加时间", "add_time")
                ));
    }

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
    @Override
    public List<ThemeProductResponse> getProductList(String priceOrder, String salesOrder, String cateId, String storeName, PageParamRequest pageParamRequest) {
        ProductRequest request = new ProductRequest();
        request.setPriceOrder(priceOrder);
        request.setSalesOrder(salesOrder);
        request.setCid(cateId);
        request.setStoreName(storeName);

        List<StoreProduct> productList = storeProductService.findH5List(request, pageParamRequest);
        if (CollUtil.isEmpty(productList)) {
            return new ArrayList<>();
        }
        List<ThemeProductResponse> responseList = productList.stream()
                .map(this::buildThemeProductResponse)
                .collect(Collectors.toList());
        Set<String> imagePaths = new LinkedHashSet<>();
        responseList.forEach(item -> collectThemeRelativeImagePaths(item.getImage(), imagePaths));
        Map<String, String> imageDomainMap = buildThemeImageDomainMap(imagePaths);
        responseList.forEach(item -> item.setImage(resolveThemeResponseImage(item.getImage(), imageDomainMap)));
        return responseList;
    }

    private ThemeProductResponse buildThemeProductResponse(StoreProduct storeProduct) {
        ThemeProductResponse response = new ThemeProductResponse();
        response.setId(storeProduct.getId());
        response.setStoreName(storeProduct.getStoreName());
        response.setCateId(storeProduct.getCateId());
        response.setImage(storeProduct.getImage());
        response.setSales(String.valueOf(storeProduct.getSales() == null ? 0 : storeProduct.getSales()));
        response.setPrice(storeProduct.getPrice());
        response.setStock(storeProduct.getStock());
        response.setActivity(storeProduct.getActivity());
        response.setOtPrice(storeProduct.getOtPrice());
        response.setSpecType(Objects.equals(storeProduct.getSpecType(), Boolean.TRUE) ? 1 : 0);
        response.setUnitName(storeProduct.getUnitName());
        return response;
    }

    /**
     * 获取主题文章列表
     *
     * @param cid 文章分类ID
     * @param pageParamRequest 分页参数
     * @return 文章列表
     */
    @Override
    public ThemeArticleListResponse getArticleList(String cid, PageParamRequest pageParamRequest) {
        PageInfo<ArticleResponse> pageInfo = articleService.getList(cid, pageParamRequest);
        ThemeArticleListResponse response = new ThemeArticleListResponse();
        response.setCount(pageInfo.getTotal());
        if (CollUtil.isEmpty(pageInfo.getList())) {
            return response;
        }
        List<ThemeArticleResponse> articleList = pageInfo.getList().stream()
                .map(this::buildThemeArticleResponse)
                .collect(Collectors.toList());
        Set<String> imagePaths = new LinkedHashSet<>();
        articleList.forEach(item -> collectThemeRelativeImagePaths(item.getImageInput(), imagePaths));
        Map<String, String> imageDomainMap = buildThemeImageDomainMap(imagePaths);
        articleList.forEach(item -> item.setImageInput(item.getImageInput().stream()
                .map(image -> resolveThemeResponseImage(image, imageDomainMap))
                .collect(Collectors.toList())));
        response.setList(articleList);
        return response;
    }

    private ThemeArticleResponse buildThemeArticleResponse(ArticleResponse article) {
        ThemeArticleResponse response = new ThemeArticleResponse();
        response.setId(article.getId());
        response.setCid(article.getCid());
        response.setTitle(article.getTitle());
        response.setImageInput(buildArticleImageList(article.getImageInput()));
        response.setVisit(article.getVisit() == null ? "0" : article.getVisit());
        response.setAddTime(CrmebDateUtil.dateToStr(article.getCreateTime(), DateConstants.DATE_FORMAT));
        return response;
    }

    private List<String> buildArticleImageList(String imageInput) {
        if (StrUtil.isBlank(imageInput)) {
            return new ArrayList<>();
        }
        return Stream.of(imageInput.split(","))
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .map(this::normalizeArticleImageUrl)
                .collect(Collectors.toList());
    }

    private String normalizeArticleImageUrl(String imageUrl) {
        String normalizedUrl = imageUrl == null ? "" : imageUrl.trim();
        while ((normalizedUrl.startsWith("[") && normalizedUrl.endsWith("]"))
                || (normalizedUrl.startsWith("\"") && normalizedUrl.endsWith("\""))
                || (normalizedUrl.startsWith("'") && normalizedUrl.endsWith("'"))) {
            normalizedUrl = normalizedUrl.substring(1, normalizedUrl.length() - 1).trim();
        }
        while (normalizedUrl.startsWith("/") && (normalizedUrl.startsWith("/http://") || normalizedUrl.startsWith("/https://"))) {
            normalizedUrl = normalizedUrl.substring(1);
        }
        normalizedUrl = removeDuplicateRemoteUrlPrefix(normalizedUrl, "/https://");
        normalizedUrl = removeDuplicateRemoteUrlPrefix(normalizedUrl, "/http://");
        return normalizedUrl;
    }

    private String removeDuplicateRemoteUrlPrefix(String url, String duplicatePrefix) {
        int duplicateIndex = url.indexOf(duplicatePrefix);
        if (duplicateIndex <= 0) {
            return url;
        }
        return url.substring(duplicateIndex + 1);
    }

    /**
     * 获取移动端主题信息
     *
     * @param themeId 主题ID，0表示当前启用主题
     * @param type 数据类型：home/category/detail/user/theme/base
     * @return 主题信息
     */
    @Override
    public JSONObject getThemeInfo(Integer themeId, String type) {
        String dataType = StrUtil.isBlank(type) ? "" : type.trim().toLowerCase();
        JSONObject themeInfo = getInfoData(themeId == null ? 0 : themeId, dataType);
        if (themeInfo == null || themeInfo.isEmpty()) {
            return themeInfo;
        }

        if (TYPE_HOME.equals(dataType) || TYPE_DETAIL.equals(dataType) || TYPE_USER.equals(dataType)) {
            appendCustomerServiceConfig(themeInfo);
        }
        if (TYPE_USER.equals(dataType)) {
            handleUserMenus(themeInfo);
        }
        appendImageDomainPrefix(themeInfo);
        return themeInfo;
    }

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
    @Override
    public List<JSONObject> getThemeProduct(String ids, String cateIds, Integer order, Integer sort, Integer limit) {
        List<StoreProduct> productList;
        boolean appendFicti;
        if (StrUtil.isNotBlank(ids)) {
            List<Integer> productIdList = com.zbkj.common.utils.CrmebUtil.stringToArray(ids);
            productList = storeProductService.findByIds(productIdList, "front");
            appendFicti = true;
        } else {
            ProductRequest request = new ProductRequest();
            request.setCid(cateIds);
            setProductOrder(request, order, sort);

            PageParamRequest pageParamRequest = new PageParamRequest();
            pageParamRequest.setLimit(getThemeListLimit(limit));
            productList = storeProductService.findH5List(request, pageParamRequest);
            appendFicti = false;
        }
        if (CollUtil.isEmpty(productList)) {
            return new ArrayList<>();
        }
        List<JSONObject> result = productList.stream()
                .map(product -> buildThemeProductJson(product, appendFicti))
                .collect(Collectors.toList());
        appendImageDomainPrefixToList(result);
        return result;
    }

    /**
     * 获取移动端主题优惠券列表
     *
     * @param limit 返回数量
     * @return 优惠券列表与类型统计
     */
    @Override
    public JSONObject getThemeCoupon(Integer limit) {
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(getThemeListLimit(limit));
        PageInfo<StoreCoupon> pageInfo = storeCouponService.getThemeList(0, pageParamRequest);

        JSONArray list = new JSONArray();
        if (pageInfo != null && CollUtil.isNotEmpty(pageInfo.getList())) {
            pageInfo.getList().forEach(coupon -> list.add(buildThemeCouponJson(coupon)));
        }

        JSONArray count = new JSONArray();
        for (int useType = CouponConstants.COUPON_USE_TYPE_COMMON; useType <= CouponConstants.COUPON_USE_TYPE_CATEGORY; useType++) {
            PageParamRequest countRequest = new PageParamRequest();
            countRequest.setLimit(1);
            PageInfo<StoreCoupon> countPageInfo = storeCouponService.getThemeList(useType, countRequest);
            count.add(countPageInfo == null ? 0 : countPageInfo.getTotal());
        }

        JSONObject result = new JSONObject(true);
        result.put("list", list);
        result.put("count", count);
        return result;
    }

    /**
     * 获取移动端主题秒杀商品列表
     *
     * @param limit 返回数量
     * @return 当前正在进行的秒杀商品列表
     */
    @Override
    public List<JSONObject> getThemeSeckill(Integer limit) {
        List<StoreSeckillManger> currentManagerList = storeSeckillMangerService.getCurrentSeckillManager();
        if (CollUtil.isEmpty(currentManagerList)) {
            return new ArrayList<>();
        }

        StoreSeckillManger currentManager = currentManagerList.stream()
                .filter(manager -> Boolean.TRUE.equals(manager.getStatus()))
                .findFirst()
                .orElse(null);
        if (currentManager == null) {
            return new ArrayList<>();
        }

        String currentDate = CrmebDateUtil.nowDate(Constants.DATE_FORMAT_DATE);
        LambdaQueryWrapper<StoreSeckill> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(StoreSeckill::getStatus, 1);
        queryWrapper.eq(StoreSeckill::getIsDel, false);
        queryWrapper.eq(StoreSeckill::getIsShow, true);
        queryWrapper.eq(StoreSeckill::getTimeId, currentManager.getId());
        queryWrapper.le(StoreSeckill::getStartTime, currentDate);
        queryWrapper.ge(StoreSeckill::getStopTime, currentDate);
        queryWrapper.orderByDesc(StoreSeckill::getId);
        queryWrapper.last(" limit " + getThemeListLimit(limit));
        List<StoreSeckill> seckillList = storeSeckillService.list(queryWrapper);
        if (CollUtil.isEmpty(seckillList)) {
            return new ArrayList<>();
        }

        String timeSwap = getSeckillTimeSwap(currentManager);
        List<JSONObject> result = seckillList.stream()
                .map(seckill -> buildThemeSeckillJson(seckill, timeSwap))
                .collect(Collectors.toList());
        appendImageDomainPrefixToList(result);
        return result;
    }

    /**
     * 获取移动端主题拼团商品列表
     *
     * @param limit 返回数量
     * @return 当前正在进行的拼团商品列表
     */
    @Override
    public List<JSONObject> getThemeCombination(Integer limit) {
        LambdaQueryWrapper<StoreCombination> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(StoreCombination::getIsDel, false);
        queryWrapper.eq(StoreCombination::getIsShow, true);
        queryWrapper.ge(StoreCombination::getStock, 0);
        long millis = System.currentTimeMillis();
        queryWrapper.le(StoreCombination::getStartTime, millis);
        queryWrapper.ge(StoreCombination::getStopTime, millis);
        queryWrapper.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        queryWrapper.last(" limit " + getThemeListLimit(limit));
        List<StoreCombination> combinationList = storeCombinationService.list(queryWrapper);
        if (CollUtil.isEmpty(combinationList)) {
            return new ArrayList<>();
        }
        List<JSONObject> result = combinationList.stream()
                .map(this::buildThemeCombinationJson)
                .collect(Collectors.toList());
        appendImageDomainPrefixToList(result);
        return result;
    }

    /**
     * 获取移动端主题砍价商品列表
     *
     * @param limit 返回数量
     * @return 当前正在进行的砍价商品列表
     */
    @Override
    public List<JSONObject> getThemeBargain(Integer limit) {
        LambdaQueryWrapper<StoreBargain> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(StoreBargain::getStatus, true);
        queryWrapper.eq(StoreBargain::getIsDel, false);
        queryWrapper.gt(StoreBargain::getStock, 0);
        long millis = System.currentTimeMillis();
        queryWrapper.le(StoreBargain::getStartTime, millis);
        queryWrapper.ge(StoreBargain::getStopTime, millis);
        queryWrapper.orderByDesc(StoreBargain::getSort, StoreBargain::getId);
        queryWrapper.last(" limit " + getThemeListLimit(limit));
        List<StoreBargain> bargainList = storeBargainService.list(queryWrapper);
        if (CollUtil.isEmpty(bargainList)) {
            return new ArrayList<>();
        }
        List<JSONObject> result = bargainList.stream()
                .map(this::buildThemeBargainJson)
                .collect(Collectors.toList());
        appendImageDomainPrefixToList(result);
        return result;
    }

    private void setProductOrder(ProductRequest request, Integer order, Integer sort) {
        String sortType = Objects.equals(sort, 0) ? Constants.SORT_DESC : Constants.SORT_ASC;
        if (Objects.equals(order, 0)) {
            request.setSalesOrder(sortType);
            return;
        }
        if (Objects.equals(order, 1)) {
            request.setPriceOrder(sortType);
        }
    }

    private Integer getThemeListLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return 10;
        }
        return limit;
    }

    private JSONObject buildThemeProductJson(StoreProduct product, boolean appendFicti) {
        JSONObject item = new JSONObject(true);
        item.put("id", product.getId());
        item.put("store_name", getDefaultString(product.getStoreName()));
        item.put("cate_id", getDefaultString(product.getCateId()));
        item.put("image", getDefaultString(product.getImage()));
        item.put("sales", getProductSales(product, appendFicti));
        item.put("price", product.getPrice());
        item.put("stock", product.getStock() == null ? 0 : product.getStock());
        item.put("ot_price", product.getOtPrice());
        item.put("spec_type", Boolean.TRUE.equals(product.getSpecType()) ? 1 : 0);
        item.put("unit_name", getDefaultString(product.getUnitName()));
        return item;
    }

    private JSONObject buildThemeCouponJson(StoreCoupon coupon) {
        JSONObject item = new JSONObject(true);
        item.put("id", coupon.getId());
        item.put("cid", 0);
        item.put("coupon_title", getDefaultString(coupon.getName()));
        item.put("start_time", getTimestamp(coupon.getReceiveStartTime()));
        item.put("end_time", getTimestamp(coupon.getReceiveEndTime()));
        item.put("total_count", coupon.getTotal() == null ? 0 : coupon.getTotal());
        item.put("remain_count", coupon.getLastTotal() == null ? 0 : coupon.getLastTotal());
        item.put("receive_limit", 1);
        item.put("is_permanent", Boolean.TRUE.equals(coupon.getIsFixedTime()) ? 0 : 1);
        item.put("status", Boolean.TRUE.equals(coupon.getStatus()) ? 1 : 0);
        item.put("is_give_subscribe", 0);
        item.put("is_full_give", 0);
        item.put("full_reduction", "0.00");
        item.put("is_del", Boolean.TRUE.equals(coupon.getIsDel()) ? 1 : 0);
        item.put("add_time", getTimestamp(coupon.getCreateTime()));
        item.put("title", getDefaultString(coupon.getName()));
        item.put("integral", 0);
        item.put("coupon_price", coupon.getMoney());
        item.put("use_min_price", coupon.getMinPrice());
        item.put("coupon_time", coupon.getDay() == null ? 0 : coupon.getDay());
        item.put("product_id", Objects.equals(coupon.getUseType(), CouponConstants.COUPON_USE_TYPE_PRODUCT) ? getDefaultString(coupon.getPrimaryKey()) : "");
        item.put("category_id", Objects.equals(coupon.getUseType(), CouponConstants.COUPON_USE_TYPE_CATEGORY) ? getDefaultString(coupon.getPrimaryKey()) : "");
        item.put("type", coupon.getUseType());
        item.put("receive_type", coupon.getType());
        item.put("start_use_time", getFixedTimeStart(coupon));
        item.put("end_use_time", getFixedTimeEnd(coupon));
        item.put("sort", 0);
        item.put("used", new JSONArray());
        item.put("is_use", Boolean.TRUE.equals(coupon.getIsGet()) ? 1 : 0);
        return item;
    }

    private JSONObject buildThemeSeckillJson(StoreSeckill seckill, String timeSwap) {
        JSONObject item = new JSONObject(true);
        item.put("id", seckill.getId());
        item.put("activity_id", seckill.getId());
        item.put("product_id", seckill.getProductId());
        item.put("image", getDefaultString(seckill.getImage()));
        item.put("images", buildThemeImageList(seckill.getImages(), seckill.getImage()));
        item.put("title", getDefaultString(seckill.getTitle()));
        item.put("info", getDefaultString(seckill.getInfo()));
        item.put("price", seckill.getPrice());
        item.put("cost", seckill.getCost());
        item.put("ot_price", seckill.getOtPrice());
        item.put("give_integral", seckill.getGiveIntegral());
        item.put("sort", seckill.getSort());
        item.put("stock", seckill.getStock());
        item.put("sales", seckill.getSales());
        item.put("unit_name", getDefaultString(seckill.getUnitName()));
        item.put("postage", seckill.getPostage());
        item.put("start_time", String.valueOf(getTimestamp(seckill.getStartTime())));
        item.put("stop_time", String.valueOf(getTimestamp(seckill.getStopTime())));
        item.put("add_time", seckill.getCreateTime() == null ? null : CrmebDateUtil.dateToStr(seckill.getCreateTime(), DateConstants.DATE_FORMAT));
        item.put("status", seckill.getStatus());
        item.put("is_postage", boolToInt(seckill.getIsPostage()));
        item.put("is_hot", null);
        item.put("is_del", boolToInt(seckill.getIsDel()));
        item.put("num", seckill.getNum());
        item.put("is_show", boolToInt(seckill.getIsShow()));
        item.put("time_id", seckill.getTimeId() == null ? null : String.valueOf(seckill.getTimeId()));
        item.put("temp_id", seckill.getTempId());
        item.put("weight", seckill.getWeight());
        item.put("volume", seckill.getVolume());
        item.put("quota", seckill.getQuota());
        item.put("quota_show", seckill.getQuotaShow());
        item.put("once_num", null);
        item.put("logistics", null);
        item.put("freight", null);
        item.put("custom_form", null);
        item.put("virtual_type", null);
        item.put("is_commission", null);
        item.put("cate_id", null);
        item.put("product_price", null);
        item.put("percent", buildSeckillPercent(seckill));
        item.put("time_swap", timeSwap);
        return item;
    }

    private JSONObject buildThemeCombinationJson(StoreCombination combination) {
        JSONObject item = new JSONObject(true);
        item.put("id", combination.getId());
        item.put("product_id", combination.getProductId());
        item.put("mer_id", combination.getMerId());
        item.put("image", combination.getImage());
        item.put("images", buildThemeImageList(combination.getImages(), combination.getImage()));
        item.put("title", combination.getTitle());
        item.put("attr", combination.getAttr());
        item.put("people", combination.getPeople());
        item.put("info", combination.getInfo());
        item.put("price", combination.getPrice());
        item.put("sort", combination.getSort());
        item.put("sales", combination.getSales());
        item.put("stock", combination.getStock());
        item.put("add_time", formatMillis(combination.getAddTime()));
        item.put("is_host", boolToInt(combination.getIsHost()));
        item.put("is_show", boolToInt(combination.getIsShow()));
        item.put("is_del", boolToInt(combination.getIsDel()));
        item.put("combination", boolToInt(combination.getCombination()));
        item.put("mer_use", boolToInt(combination.getMerUse()));
        item.put("is_postage", boolToInt(combination.getIsPostage()));
        item.put("postage", formatBigDecimal(combination.getPostage()));
        item.put("start_time", getSecondTimestampByMillis(combination.getStartTime()));
        item.put("stop_time", getSecondTimestampByMillis(combination.getStopTime()));
        item.put("effective_time", combination.getEffectiveTime());
        item.put("cost", combination.getCost());
        item.put("browse", combination.getBrowse());
        item.put("unit_name", combination.getUnitName());
        item.put("temp_id", combination.getTempId());
        item.put("weight", formatBigDecimal(combination.getWeight()));
        item.put("volume", formatBigDecimal(combination.getVolume()));
        item.put("num", combination.getNum());
        item.put("once_num", combination.getOnceNum());
        item.put("quota", combination.getQuota());
        item.put("quota_show", combination.getQuotaShow());
        item.put("virtual", combination.getVirtualRation());
        item.put("logistics", null);
        item.put("freight", null);
        item.put("custom_form", null);
        item.put("virtual_type", null);
        item.put("is_commission", null);
        item.put("head_commission", null);
        item.put("ot_price", formatBigDecimal(combination.getOtPrice()));
        item.put("product_price", combination.getPrice());
        return item;
    }

    private JSONObject buildThemeBargainJson(StoreBargain bargain) {
        JSONObject item = new JSONObject(true);
        item.put("id", bargain.getId());
        item.put("product_id", bargain.getProductId());
        item.put("title", bargain.getTitle());
        item.put("min_price", formatBigDecimal(bargain.getMinPrice()));
        item.put("image", bargain.getImage());
        item.put("price", bargain.getPrice());
        item.put("ot_price", null);
        item.put("product_price", bargain.getPrice());
        item.put("cate_id", null);
        item.put("people", bargain.getPeopleNum());
        return item;
    }

    private List<String> buildThemeImageList(String images, String image) {
        if (StrUtil.isNotBlank(images)) {
            return Stream.of(images.split(","))
                    .filter(StrUtil::isNotBlank)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        if (StrUtil.isNotBlank(image)) {
            return CollUtil.newArrayList(image);
        }
        return new ArrayList<>();
    }

    private Integer buildSeckillPercent(StoreSeckill seckill) {
        if (seckill.getQuotaShow() == null || seckill.getQuotaShow() <= 0 || seckill.getQuota() == null) {
            return 0;
        }
        return com.zbkj.common.utils.CrmebUtil.percentInstanceIntVal(seckill.getQuotaShow() - seckill.getQuota(), seckill.getQuotaShow());
    }

    private String getSeckillTimeSwap(StoreSeckillManger currentManager) {
        if (currentManager == null || currentManager.getEndTime() == null) {
            return null;
        }
        int secKillEndSecondTimestamp = CrmebDateUtil.getSecondTimestamp(CrmebDateUtil.nowDateTime("yyyy-MM-dd " + currentManager.getEndTime() + ":00:00"));
        return String.valueOf(secKillEndSecondTimestamp);
    }

    private Integer boolToInt(Boolean value) {
        if (value == null) {
            return null;
        }
        return Boolean.TRUE.equals(value) ? 1 : 0;
    }

    private String formatBigDecimal(BigDecimal value) {
        return value == null ? null : value.toPlainString();
    }

    private String formatMillis(Long timeMillis) {
        return timeMillis == null ? null : CrmebDateUtil.timestamp2DateStr(timeMillis, DateConstants.DATE_FORMAT);
    }

    private Integer getSecondTimestampByMillis(Long timeMillis) {
        return timeMillis == null ? null : CrmebDateUtil.getSecondTimestamp(timeMillis);
    }

    private Integer getTimestamp(java.util.Date date) {
        return CrmebDateUtil.getSecondTimestamp(date);
    }

    private Integer getFixedTimeStart(StoreCoupon coupon) {
        if (!Boolean.TRUE.equals(coupon.getIsFixedTime())) {
            return 0;
        }
        return getTimestamp(coupon.getUseStartTime());
    }

    private Integer getFixedTimeEnd(StoreCoupon coupon) {
        if (!Boolean.TRUE.equals(coupon.getIsFixedTime())) {
            return 0;
        }
        return getTimestamp(coupon.getUseEndTime());
    }

    private Integer getProductSales(StoreProduct product, boolean appendFicti) {
        int sales = product.getSales() == null ? 0 : product.getSales();
        if (!appendFicti || product.getFicti() == null) {
            return sales;
        }
        return sales + product.getFicti();
    }

    private void appendCustomerServiceConfig(JSONObject themeInfo) {
        JSONObject value = themeInfo.getJSONObject("value");
        if (value == null || value.isEmpty()) {
            return;
        }
        for (Object component : value.values()) {
            if (!(component instanceof JSONObject)) {
                continue;
            }
            JSONObject componentJson = (JSONObject) component;
            if ("customerService".equals(componentJson.getString("name"))) {
                componentJson.put("routine_contact_type", systemConfigService.getValueByKey(CONFIG_KEY_ROUTINE_CONTACT_TYPE));
            }
        }
    }

    private void handleUserMenus(JSONObject themeInfo) {
        JSONObject value = themeInfo.getJSONObject("value");
        if (value == null || value.isEmpty()) {
            return;
        }

        User user = userService.getInfo();
        Integer uid = user == null ? 0 : user.getUid();
        boolean isLogin = uid != null && uid > 0;
        boolean isPromoter = isLogin && Boolean.TRUE.equals(user.getIsPromoter());
        boolean brokerageEnabled = isConfigEnabled(Constants.CONFIG_KEY_STORE_BROKERAGE_IS_OPEN, false);
        boolean enabledStaff = isLogin && isEnabledStoreStaff(uid);
        boolean writeOffStaff = isLogin && isWriteOffStaff(uid);

        Map<String, Integer> orderNumMap = isLogin ? buildOrderNumMap(uid) : new HashMap<>();
        for (Object component : value.values()) {
            if (!(component instanceof JSONObject)) {
                continue;
            }
            JSONObject componentJson = (JSONObject) component;
            JSONObject menuConfig = componentJson.getJSONObject("menuConfig");
            if (menuConfig == null) {
                continue;
            }
            JSONArray menuList = menuConfig.getJSONArray("list");
            if (menuList == null || menuList.isEmpty()) {
                continue;
            }
            for (int i = 0; i < menuList.size(); i++) {
                JSONObject menu = menuList.getJSONObject(i);
                handleMenuItem(menu, brokerageEnabled, isPromoter, enabledStaff, writeOffStaff, orderNumMap);
            }
        }
    }

    private void handleMenuItem(JSONObject menu, boolean brokerageEnabled, boolean isPromoter,
                                boolean enabledStaff, boolean writeOffStaff, Map<String, Integer> orderNumMap) {
        String link = getMenuLink(menu);
        boolean show = !menu.containsKey("show") || menu.getBooleanValue("show");
        show = show && canShowMenu(link, brokerageEnabled, isPromoter, enabledStaff, writeOffStaff);
        menu.put("show", show);

        if (orderNumMap.containsKey(link)) {
            menu.put("num", orderNumMap.get(link));
        } else if (!menu.containsKey("num")) {
            menu.put("num", 0);
        }

        if ("/kefu/mobile_list".equals(link)) {
            String siteUrl = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SITE_URL);
            if (StrUtil.isNotBlank(siteUrl)) {
                setMenuLink(menu, siteUrl + link);
            }
        }
    }

    private boolean canShowMenu(String link, boolean brokerageEnabled, boolean isPromoter,
                                boolean enabledStaff, boolean writeOffStaff) {
        if (StrUtil.isBlank(link)) {
            return true;
        }
        switch (link) {
            case "/pages/users/user_vip/index":
                return isConfigEnabled(CONFIG_KEY_MEMBER_FUNC_STATUS, true);
            case "/pages/users/user_spread_user/index":
                return brokerageEnabled && isPromoter;
            case "/pages/annex/settled/index":
                return brokerageEnabled && !isPromoter;
            case "/pages/users/user_money/index":
                return isConfigEnabled(CONFIG_KEY_BALANCE_FUNC_STATUS, true);
            case MOBILE_ADMIN_ORDER_LINK:
            case MOBILE_ADMIN_MANAGE_LINK:
                return enabledStaff;
            case MOBILE_ADMIN_WRITE_OFF_LINK:
                return writeOffStaff;
            case "/pages/users/user_invoice_list/index":
                return isConfigEnabled(CONFIG_KEY_INVOICE_FUNC_STATUS, true);
            case "/pages/annex/vip_paid/index":
                return isAnyConfigEnabled(true, CONFIG_KEY_MEMBER_CARD_STATUS, CONFIG_KEY_VIP_OPEN);
            case "/kefu/mobile_list":
                return false;
            default:
                return true;
        }
    }

    private Map<String, Integer> buildOrderNumMap(Integer uid) {
        Map<String, Integer> orderNumMap = new HashMap<>();
        orderNumMap.put("/pages/users/order_list/index", safeCount(storeOrderService.getOrderCountByUid(uid)));
        orderNumMap.put("/pages/users/order_list/index?status=0", safeCount(storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_UNPAID, uid)));
        orderNumMap.put("/pages/users/order_list/index?status=1", safeCount(storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_NOT_SHIPPED, uid)));
        orderNumMap.put("/pages/users/order_list/index?status=2", safeCount(storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_SPIKE, uid)));
        orderNumMap.put("/pages/users/order_list/index?status=3", safeCount(storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_JUDGE, uid)));
        orderNumMap.put("/pages/users/order_list/index?status=4", safeCount(storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_COMPLETE, uid)));
        orderNumMap.put("/pages/users/user_return_list/index", safeCount(storeOrderService.getTopDataUtil(Constants.ORDER_STATUS_H5_REFUND, uid)));
        return orderNumMap;
    }

    private Integer safeCount(Integer count) {
        return count == null ? 0 : count;
    }

    private boolean isEnabledStoreStaff(Integer uid) {
        return systemStoreStaffService.count(Wrappers.<SystemStoreStaff>lambdaQuery()
                .eq(SystemStoreStaff::getUid, uid)
                .eq(SystemStoreStaff::getStatus, 1)) > 0;
    }

    private boolean isWriteOffStaff(Integer uid) {
        return systemStoreStaffService.count(Wrappers.<SystemStoreStaff>lambdaQuery()
                .eq(SystemStoreStaff::getUid, uid)
                .eq(SystemStoreStaff::getVerifyStatus, 1)) > 0;
    }

    private boolean isConfigEnabled(String key, boolean defaultValue) {
        String value = systemConfigService.getValueByKey(key);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    private boolean isAnyConfigEnabled(boolean defaultValue, String... keys) {
        for (String key : keys) {
            String value = systemConfigService.getValueByKey(key);
            if (StrUtil.isNotBlank(value)) {
                return "1".equals(value) || "true".equalsIgnoreCase(value);
            }
        }
        return defaultValue;
    }

    private String getMenuLink(JSONObject menu) {
        String link = menu.getString("link");
        if (StrUtil.isNotBlank(link)) {
            return link;
        }
        JSONArray info = menu.getJSONArray("info");
        if (info == null || info.isEmpty()) {
            return "";
        }
        for (int i = 0; i < info.size(); i++) {
            JSONObject item = info.getJSONObject(i);
            if ("链接".equals(item.getString("title"))) {
                return item.getString("value");
            }
        }
        return "";
    }

    private void setMenuLink(JSONObject menu, String link) {
        if (menu.containsKey("link")) {
            menu.put("link", link);
            return;
        }
        JSONArray info = menu.getJSONArray("info");
        if (info == null || info.isEmpty()) {
            return;
        }
        for (int i = 0; i < info.size(); i++) {
            JSONObject item = info.getJSONObject(i);
            if ("链接".equals(item.getString("title"))) {
                item.put("value", link);
                return;
            }
        }
    }

    /**
     * 为主题数据中的相对图片路径拼接对应的存储域名。
     *
     * 素材表中存在记录时，根据 image_type 选择本地、七牛、阿里、腾讯或京东云域名；
     * 素材表中不存在记录时，使用 localUploadUrl。
     *
     * @param themeInfo 主题信息 JSON
     */
    private void appendImageDomainPrefix(Object themeInfo) {
        if (themeInfo == null
                || (themeInfo instanceof JSONObject && ((JSONObject) themeInfo).isEmpty())
                || (themeInfo instanceof JSONArray && ((JSONArray) themeInfo).isEmpty())) {
            return;
        }
        Set<String> imagePaths = new LinkedHashSet<>();
        collectThemeRelativeImagePaths(themeInfo, imagePaths);
        if (imagePaths.isEmpty()) {
            return;
        }
        appendImageDomainPrefixRecursive(themeInfo, buildThemeImageDomainMap(imagePaths));
    }

    /**
     * 处理主题列表数据中的图片域名。
     */
    private void appendImageDomainPrefixToList(List<JSONObject> dataList) {
        if (CollUtil.isEmpty(dataList)) {
            return;
        }
        JSONArray data = new JSONArray();
        data.addAll(dataList);
        appendImageDomainPrefix(data);
    }

    /**
     * 收集主题列表中的图片相对路径。
     */
    private Set<String> collectThemeListImagePaths(List<Theme> themeList) {
        Set<String> imagePaths = new LinkedHashSet<>();
        for (Theme theme : themeList) {
            collectThemeRelativeImagePaths(theme.getHomeImage(), imagePaths);
            collectThemeRelativeImagePaths(theme.getCategoryImage(), imagePaths);
            collectThemeRelativeImagePaths(theme.getDetailImage(), imagePaths);
            collectThemeRelativeImagePaths(theme.getUserImage(), imagePaths);
            collectThemeRelativeImagePaths(theme.getHomeDefaultImage(), imagePaths);
            collectThemeRelativeImagePaths(theme.getCategoryDefaultImage(), imagePaths);
            collectThemeRelativeImagePaths(theme.getDetailDefaultImage(), imagePaths);
            collectThemeRelativeImagePaths(theme.getUserDefaultImage(), imagePaths);
        }
        return imagePaths;
    }

    /**
     * 递归收集主题数据中的图片相对路径。
     */
    private void collectThemeRelativeImagePaths(Object value, Set<String> imagePaths) {
        if (value instanceof JSONObject) {
            for (Object fieldValue : ((JSONObject) value).values()) {
                collectThemeRelativeImagePaths(fieldValue, imagePaths);
            }
            return;
        }
        if (value instanceof JSONArray) {
            for (Object element : (JSONArray) value) {
                collectThemeRelativeImagePaths(element, imagePaths);
            }
            return;
        }
        if (value instanceof Iterable) {
            for (Object element : (Iterable<?>) value) {
                collectThemeRelativeImagePaths(element, imagePaths);
            }
            return;
        }
        if (!(value instanceof String)) {
            return;
        }

        String stringValue = ((String) value).trim();
        Object nestedJson = parseThemeJsonContainer(stringValue);
        if (nestedJson != null) {
            collectThemeRelativeImagePaths(nestedJson, imagePaths);
            return;
        }
        if (hasDomainPrefix(stringValue) || !isLikelyImageUrl(stringValue)) {
            return;
        }
        String imagePath = normalizeThemeImagePath(stringValue);
        if (StrUtil.isNotBlank(imagePath)) {
            imagePaths.add(imagePath);
        }
    }

    /**
     * 批量查询素材存储类型，并生成图片路径对应的域名映射。
     */
    private Map<String, String> buildThemeImageDomainMap(Set<String> imagePaths) {
        Map<String, String> imageDomainMap = new HashMap<>();
        if (imagePaths == null || imagePaths.isEmpty()) {
            return imageDomainMap;
        }

        Map<String, Integer> imageTypeMap = findAttachmentImageTypes(imagePaths);
        Map<Integer, String> domainCache = new HashMap<>();
        String localUploadUrl = getThemeUploadDomain(1, domainCache);
        for (String imagePath : imagePaths) {
            Integer imageType = imageTypeMap.get(imagePath);
            String domain = getThemeUploadDomain(imageType, domainCache);
            imageDomainMap.put(imagePath, StrUtil.isBlank(domain) ? localUploadUrl : domain);
        }
        return imageDomainMap;
    }

    /**
     * 批量查询图片路径对应的素材存储类型。
     */
    private Map<String, Integer> findAttachmentImageTypes(Set<String> imagePaths) {
        Map<String, Integer> imageTypeMap = new HashMap<>();
        List<String> queryPaths = new ArrayList<>();
        for (String imagePath : imagePaths) {
            queryPaths.add(imagePath);
            queryPaths.add("/" + imagePath);
        }

        for (int start = 0; start < queryPaths.size(); start += ATTACHMENT_QUERY_BATCH_SIZE) {
            int end = Math.min(start + ATTACHMENT_QUERY_BATCH_SIZE, queryPaths.size());
            List<String> batchPaths = queryPaths.subList(start, end);
            LambdaQueryWrapper<SystemAttachment> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.select(
                    SystemAttachment::getAttId,
                    SystemAttachment::getAttDir,
                    SystemAttachment::getSattDir,
                    SystemAttachment::getImageType
            );
            queryWrapper.and(wrapper -> wrapper
                    .in(SystemAttachment::getSattDir, batchPaths)
                    .or()
                    .in(SystemAttachment::getAttDir, batchPaths));
            queryWrapper.orderByDesc(SystemAttachment::getAttId);
            List<SystemAttachment> attachments = systemAttachmentService.list(queryWrapper);
            if (CollUtil.isEmpty(attachments)) {
                continue;
            }
            for (SystemAttachment attachment : attachments) {
                putAttachmentImageType(imageTypeMap, attachment.getSattDir(), attachment.getImageType());
                putAttachmentImageType(imageTypeMap, attachment.getAttDir(), attachment.getImageType());
            }
        }
        return imageTypeMap;
    }

    /**
     * 写入素材路径对应的存储类型，重复路径优先使用最新素材记录。
     */
    private void putAttachmentImageType(Map<String, Integer> imageTypeMap, String path, Integer imageType) {
        String normalizedPath = normalizeThemeImagePath(path);
        if (StrUtil.isBlank(normalizedPath)) {
            return;
        }
        imageTypeMap.putIfAbsent(normalizedPath, normalizeUploadType(imageType));
    }

    /**
     * 根据素材存储类型获取对应域名。
     */
    private String getThemeUploadDomain(Integer imageType, Map<Integer, String> domainCache) {
        int uploadType = normalizeUploadType(imageType);
        if (domainCache.containsKey(uploadType)) {
            return domainCache.get(uploadType);
        }

        String configKey;
        switch (uploadType) {
            case 2:
                configKey = SysConfigConstants.CONFIG_QN_UPLOAD_URL;
                break;
            case 3:
                configKey = SysConfigConstants.CONFIG_AL_UPLOAD_URL;
                break;
            case 4:
                configKey = SysConfigConstants.CONFIG_TX_UPLOAD_URL;
                break;
            case 5:
                configKey = SysConfigConstants.CONFIG_JD_UPLOAD_URL;
                break;
            default:
                configKey = SysConfigConstants.CONFIG_LOCAL_UPLOAD_URL;
                break;
        }

        String domain = systemConfigService.getValueByKey(configKey);
        if (StrUtil.isBlank(domain) && uploadType != 1) {
            domain = getThemeUploadDomain(1, domainCache);
        }
        domain = StrUtil.isBlank(domain) ? "" : removeEndSlash(domain.trim());
        domainCache.put(uploadType, domain);
        return domain;
    }

    /**
     * 规范化素材存储类型，未知类型按本地存储处理。
     */
    private int normalizeUploadType(Integer imageType) {
        if (imageType == null || imageType < 2 || imageType > 5) {
            return 1;
        }
        return imageType;
    }

    /**
     * 递归替换主题 JSON 中的图片路径，同时兼容字段值本身为 JSON 字符串的情况。
     */
    private boolean appendImageDomainPrefixRecursive(Object value, Map<String, String> imageDomainMap) {
        boolean changed = false;
        if (value instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) value;
            for (String key : jsonObject.keySet()) {
                Object fieldValue = jsonObject.get(key);
                if (fieldValue instanceof String) {
                    String stringValue = (String) fieldValue;
                    Object nestedJson = parseThemeJsonContainer(stringValue);
                    if (nestedJson != null) {
                        if (appendImageDomainPrefixRecursive(nestedJson, imageDomainMap)) {
                            jsonObject.put(key, JSON.toJSONString(nestedJson));
                            changed = true;
                        }
                        continue;
                    }
                    String modified = resolveThemeImageUrl(stringValue, imageDomainMap);
                    if (modified != null) {
                        jsonObject.put(key, modified);
                        changed = true;
                    }
                } else if (appendImageDomainPrefixRecursive(fieldValue, imageDomainMap)) {
                    changed = true;
                }
            }
        } else if (value instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) value;
            for (int i = 0; i < jsonArray.size(); i++) {
                Object element = jsonArray.get(i);
                if (element instanceof String) {
                    String stringValue = (String) element;
                    Object nestedJson = parseThemeJsonContainer(stringValue);
                    if (nestedJson != null) {
                        if (appendImageDomainPrefixRecursive(nestedJson, imageDomainMap)) {
                            jsonArray.set(i, JSON.toJSONString(nestedJson));
                            changed = true;
                        }
                        continue;
                    }
                    String modified = resolveThemeImageUrl(stringValue, imageDomainMap);
                    if (modified != null) {
                        jsonArray.set(i, modified);
                        changed = true;
                    }
                } else if (appendImageDomainPrefixRecursive(element, imageDomainMap)) {
                    changed = true;
                }
            }
        } else if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) value;
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                if (element instanceof String) {
                    String stringValue = (String) element;
                    Object nestedJson = parseThemeJsonContainer(stringValue);
                    if (nestedJson != null) {
                        if (appendImageDomainPrefixRecursive(nestedJson, imageDomainMap)) {
                            list.set(i, JSON.toJSONString(nestedJson));
                            changed = true;
                        }
                        continue;
                    }
                    String modified = resolveThemeImageUrl(stringValue, imageDomainMap);
                    if (modified != null) {
                        list.set(i, modified);
                        changed = true;
                    }
                } else if (appendImageDomainPrefixRecursive(element, imageDomainMap)) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    /**
     * 为单个图片相对路径拼接解析后的域名。
     */
    private String resolveThemeImageUrl(String value, Map<String, String> imageDomainMap) {
        if (StrUtil.isBlank(value) || hasDomainPrefix(value) || !isLikelyImageUrl(value)) {
            return null;
        }
        String imagePath = normalizeThemeImagePath(value);
        String domain = imageDomainMap.get(imagePath);
        if (StrUtil.isBlank(imagePath) || StrUtil.isBlank(domain)) {
            return null;
        }
        return domain + "/" + removeStartSlash(value.trim());
    }

    /**
     * 解析对象或数组形式的 JSON 字符串。
     */
    private Object parseThemeJsonContainer(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        String json = value.trim();
        boolean objectJson = json.startsWith("{") && json.endsWith("}");
        boolean arrayJson = json.startsWith("[") && json.endsWith("]");
        if (!objectJson && !arrayJson) {
            return null;
        }
        try {
            Object parsed = JSON.parse(json);
            return parsed instanceof JSONObject || parsed instanceof JSONArray ? parsed : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 规范化主题图片路径，用于匹配素材表中的相对路径。
     */
    private String normalizeThemeImagePath(String value) {
        if (StrUtil.isBlank(value) || hasDomainPrefix(value)) {
            return "";
        }
        String path = value.trim().replace("\\/", "/").replace("\\", "/");
        int queryIndex = path.indexOf('?');
        if (queryIndex >= 0) {
            path = path.substring(0, queryIndex);
        }
        int fragmentIndex = path.indexOf('#');
        if (fragmentIndex >= 0) {
            path = path.substring(0, fragmentIndex);
        }
        return removeStartSlash(path);
    }

    /**
     * 判断字符串是否可能是图片路径。
     *
     * 判断依据：
     * 1. 包含图片文件扩展名（png/jpg/jpeg/gif/webp/svg/bmp）
     * 2. 包含已知的上传路径关键字（crmebimage/uploads/attach/uploadf）
     *
     * @param value 字符串值
     * @return 是否可能是图片路径
     */
    private boolean isLikelyImageUrl(String value) {
        String lower = value.toLowerCase();
        for (String ext : THEME_IMAGE_EXTENSIONS) {
            if (lower.endsWith("." + ext)) {
                return true;
            }
            if (lower.contains("." + ext + "?")
                    || lower.contains("." + ext + "&")
                    || lower.contains("." + ext + "#")) {
                return true;
            }
        }
        if (lower.contains("crmebimage")
                || lower.contains("uploads/attach")
                || lower.contains("uploadf")) {
            return true;
        }
        return false;
    }

    /**
     * 保存主题标题信息，id为0时新增。
     *
     * @param id 主题ID
     * @param tid 复制的模板主题ID
     * @param request 标题信息
     * @return 主题ID
     */
    @Override
    public Integer saveTitleInfo(Integer id, Integer tid, ThemeTitleInfoRequest request) {
        if (request == null) {
            throw new CrmebException("主题标题信息不能为空");
        }

        Integer themeId = id == null ? 0 : id;
        Integer templateId = tid == null ? 0 : tid;
        if (themeId < 0) {
            throw new CrmebException("主题ID不正确");
        }
        if (templateId < 0) {
            throw new CrmebException("模板主题ID不正确");
        }

        boolean isCreate = themeId == 0;
        Theme theme = isCreate ? new Theme() : getByIdAndNotDeleted(themeId);
        if (!isCreate && theme == null) {
            throw new CrmebException("主题不存在");
        }

        if (templateId > 0) {
            Theme templateTheme = getByIdAndNotDeleted(templateId);
            if (templateTheme == null) {
                throw new CrmebException("模板主题不存在");
            }
            BeanUtils.copyProperties(templateTheme, theme, "id", "addTime", "upTime", "isUse", "isDel");
        }

        Integer nowTime = CrmebDateUtil.getNowTime();
        theme.setTitle(request.getTitle());
        theme.setInfo(request.getInfo() == null ? "" : request.getInfo());
        theme.setPageType(request.getPageType());
        theme.setVersion(generateThemeVersion());
        theme.setUpTime(nowTime);
        if (isCreate) {
            theme.setAddTime(nowTime);
        }
        initDefaultValue(theme);

        boolean result = isCreate ? save(theme) : updateById(theme);
        if (!result) {
            throw new CrmebException("保存主题标题信息失败");
        }
        return theme.getId();
    }

    /**
     * 保存主题模块数据，id为0时新增。
     *
     * @param id 主题ID
     * @param request 保存参数
     * @return 主题ID
     */
    @Override
    public Integer saveTheme(Integer id, ThemeSaveRequest request) {
        if (request == null) {
            throw new CrmebException("主题保存参数不能为空");
        }

        Integer themeId = id == null ? 0 : id;
        Integer templateId = request.getTid() == null ? 0 : request.getTid();
        if (themeId < 0) {
            throw new CrmebException("主题ID不正确");
        }
        if (templateId < 0) {
            throw new CrmebException("模板主题ID不正确");
        }

        boolean isCreate = themeId == 0;
        Theme theme = isCreate ? new Theme() : getByIdAndNotDeleted(themeId);
        if (!isCreate && theme == null) {
            throw new CrmebException("主题不存在");
        }

        Integer themeType = isCreate ? 0 : (theme.getType() == null ? 0 : theme.getType());
        if (templateId > 0) {
            copyTemplateTheme(templateId, theme);
        }

        Integer nowTime = CrmebDateUtil.getNowTime();
        if (StrUtil.isNotBlank(request.getTitle())) {
            theme.setTitle(request.getTitle());
        }
        if (isCreate) {
            theme.setType(0);
            theme.setCategoryData("1");
            theme.setCategoryDataUpdateTime(nowTime);
            theme.setCategoryImage("/statics/images/cate1.png");
            theme.setAddTime(nowTime);
            theme.setPageType(StrUtil.isBlank(request.getPageType()) ? "theme" : request.getPageType());
        }

        String dataType = URLUtil.decode(request.getType()).trim().toLowerCase();
        String value = encodeThemeValue(request.getValue());
        applyThemeModuleData(theme, dataType, value, request.getValue(), nowTime, themeType);

        theme.setVersion(generateThemeVersion());
        theme.setUpTime(nowTime);
        initDefaultValue(theme);

        boolean result = isCreate ? save(theme) : updateById(theme);
        if (!result) {
            throw new CrmebException("保存主题失败");
        }
        return theme.getId();
    }

    /**
     * 保存主题图片，id为0时新增。
     *
     * @param id 主题ID
     * @param request 图片保存参数
     * @return 主题ID
     */
    @Override
    public Integer saveThemeImage(Integer id, ThemeImageRequest request) {
        if (request == null) {
            throw new CrmebException("主题图片保存参数不能为空");
        }

        Integer themeId = id == null ? 0 : id;
        if (themeId < 0) {
            throw new CrmebException("主题ID不正确");
        }

        boolean isCreate = themeId == 0;
        Theme theme = isCreate ? new Theme() : getByIdAndNotDeleted(themeId);
        if (!isCreate && theme == null) {
            throw new CrmebException("主题不存在");
        }

        Integer themeType = isCreate ? 0 : (theme.getType() == null ? 0 : theme.getType());
        String imageType = URLUtil.decode(request.getType()).trim().toLowerCase();
        String image = saveThemeImageToPublicTheme(request.getImage());
        applyThemeImage(theme, imageType, image, themeType);

        Integer nowTime = CrmebDateUtil.getNowTime();
        theme.setVersion(generateThemeVersion());
        theme.setUpTime(nowTime);
        if (isCreate) {
            theme.setAddTime(nowTime);
            theme.setPageType("theme");
        }
        initDefaultValue(theme);

        boolean result = isCreate ? save(theme) : updateById(theme);
        if (!result) {
            throw new CrmebException("保存主题图片失败");
        }
        return theme.getId();
    }

    /**
     * 使用主题，切换当前启用主题。
     *
     * @param id 主题ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean useTheme(Integer id) {
        if (id == null || id <= 0) {
            throw new CrmebException("主题ID不正确");
        }
        Theme theme = getByIdAndNotDeleted(id);
        if (theme == null) {
            throw new CrmebException("主题不存在");
        }

        Theme disableTheme = new Theme();
        disableTheme.setIsUse(0);
        LambdaQueryWrapper<Theme> disableWrapper = Wrappers.lambdaQuery();
        disableWrapper.eq(Theme::getIsUse, 1);
        disableWrapper.eq(Theme::getIsDel, 0);
        update(disableTheme, disableWrapper);

        Theme useTheme = new Theme();
        useTheme.setId(id);
        useTheme.setIsUse(1);
        return updateById(useTheme);
    }

    /**
     * 使用主题数据，将源主题的指定模块复制到目标主题。
     *
     * @param id 目标主题ID
     * @param themeId 源主题ID
     * @param type 模块类型：home/category/detail/user/theme
     * @return 是否成功
     */
    @Override
    public Boolean useThemeData(Integer id, Integer themeId, String type) {
        if (id == null || id <= 0) {
            throw new CrmebException("目标主题ID不正确");
        }
        if (themeId == null || themeId <= 0) {
            throw new CrmebException("源主题ID不正确");
        }
        Theme targetTheme = getByIdAndNotDeleted(id);
        if (targetTheme == null) {
            throw new CrmebException("主题不存在");
        }
        Theme sourceTheme = getByIdAndNotDeleted(themeId);
        if (sourceTheme == null) {
            throw new CrmebException("主题数据不存在");
        }

        String dataType = StrUtil.isBlank(type) ? "" : URLUtil.decode(type).trim().toLowerCase();
        Theme updateTheme = new Theme();
        updateTheme.setId(id);
        applyUseThemeData(updateTheme, sourceTheme, themeId, dataType, CrmebDateUtil.getNowTime());
        return updateById(updateTheme);
    }

    /**
     * 还原主题默认配置。
     *
     * @param id 主题ID
     * @return 是否成功
     */
    @Override
    public Boolean restoreTheme(Integer id) {
        if (id == null || id <= 0) {
            throw new CrmebException("主题ID不正确");
        }
        Theme theme = getByIdAndNotDeleted(id);
        if (theme == null) {
            throw new CrmebException("主题不存在");
        }

        Integer nowTime = CrmebDateUtil.getNowTime();
        LambdaUpdateWrapper<Theme> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(Theme::getId, id);
        updateWrapper.eq(Theme::getIsDel, 0);
        updateWrapper.set(Theme::getHomeData, theme.getHomeDefaultData());
        updateWrapper.set(Theme::getHomeImage, getDefaultString(theme.getHomeDefaultImage()));
        updateWrapper.set(Theme::getHomeDataId, 0);
        updateWrapper.set(Theme::getHomeDataUpdateTime, nowTime);
        updateWrapper.set(Theme::getCategoryData, theme.getCategoryDefaultData());
        updateWrapper.set(Theme::getCategoryImage, getDefaultString(theme.getCategoryDefaultImage()));
        updateWrapper.set(Theme::getCategoryDataId, 0);
        updateWrapper.set(Theme::getCategoryDataUpdateTime, nowTime);
        updateWrapper.set(Theme::getDetailData, theme.getDetailDefaultData());
        updateWrapper.set(Theme::getDetailImage, getDefaultString(theme.getDetailDefaultImage()));
        updateWrapper.set(Theme::getDetailDataId, 0);
        updateWrapper.set(Theme::getDetailDataUpdateTime, nowTime);
        updateWrapper.set(Theme::getUserData, theme.getUserDefaultData());
        updateWrapper.set(Theme::getUserImage, getDefaultString(theme.getUserDefaultImage()));
        updateWrapper.set(Theme::getUserDataId, 0);
        updateWrapper.set(Theme::getUserDataUpdateTime, nowTime);
        updateWrapper.set(Theme::getThemeData, getDefaultThemeData(theme));
        updateWrapper.set(Theme::getThemeDataId, 0);
        updateWrapper.set(Theme::getThemeDataUpdateTime, nowTime);
        updateWrapper.set(Theme::getVersion, generateThemeVersion());
        updateWrapper.set(Theme::getUpTime, nowTime);
        return update(updateWrapper);
    }

    /**
     * 获取正在使用的主题。
     *
     * @return 正在使用的主题信息
     */
    @Override
    public JSONObject getUsingTheme() {
        Theme theme = getInfo(0);
        if (theme == null) {
            throw new CrmebException("没有正在使用的主题");
        }

        boolean confuse = hasSourceThemeId(theme);
        Map<Integer, String> sourceThemeTitleMap = getSourceThemeTitleMap(theme);
        JSONObject data = new JSONObject();
        data.put("id", theme.getId());
        data.put("title", theme.getTitle());
        data.put("info", theme.getInfo());
        data.put("version", theme.getVersion());
        data.put("confuse", confuse ? 1 : 0);
        data.put("data_info", buildUsingThemeDataInfo(theme, sourceThemeTitleMap));
        data.put("theme_data", parseUsingThemeData(theme.getThemeData()));
        appendImageDomainPrefix(data);
        return data;
    }

    /**
     * 软删除主题
     *
     * @param id 主题ID
     * @return 是否成功
     */
    @Override
    public Boolean delete(Integer id) {
        Theme oldTheme = getByIdAndNotDeleted(id);
        if (oldTheme == null) {
            throw new CrmebException("主题不存在");
        }

        Theme theme = new Theme();
        theme.setId(id);
        theme.setIsDel(1);
        theme.setUpTime(CrmebDateUtil.getNowTime());
        return updateById(theme);
    }

    /**
     * 查询主题详情，id为0时查询当前启用主题
     *
     * @param id 主题ID
     * @return 主题详情
     */
    @Override
    public Theme getInfo(Integer id) {
        if (id == null) {
            id = 0;
        }
        if (id < 0) {
            throw new CrmebException("主题ID不正确");
        }

        if (id > 0) {
            return getByIdAndNotDeleted(id);
        }

        LambdaQueryWrapper<Theme> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Theme::getIsDel, 0);
        queryWrapper.eq(Theme::getIsUse, 1);
        queryWrapper.orderByDesc(Theme::getId);
        queryWrapper.last(" limit 1");
        return dao.selectOne(queryWrapper);
    }

    /**
     * 查询主题详情数据，id为0时查询当前启用主题。
     *
     * @param id 主题ID
     * @param type 数据类型：home/category/detail/user/theme
     * @return 对应JSON对象数据
     */
    @Override
    public JSONObject getInfoData(Integer id, String type) {
        Theme theme = getInfo(id);
        if (theme == null) {
            throw new CrmebException("主题不存在");
        }

        String dataType = StrUtil.isBlank(type) ? "default" : URLUtil.decode(type).trim().toLowerCase();
        String data;
        switch (dataType) {
            case "home":
                data = theme.getHomeData();
                break;
            case "category":
                JSONObject categoryData = new JSONObject();
                categoryData.put("status", theme.getCategoryData());
                return buildThemeInfoResponse(categoryData, theme);
            case "detail":
                data = theme.getDetailData();
                break;
            case "user":
                data = theme.getUserData();
                break;
            case "theme":
                data = theme.getThemeData();
                if (StrUtil.isBlank(data) || "null".equalsIgnoreCase(data.trim())) {
                    data = DEFAULT_THEME_DATA;
                }
                break;
            case "base":
                JSONObject baseData = new JSONObject();
                baseData.put("id", theme.getId());
                baseData.put("title", theme.getTitle());
                baseData.put("info", theme.getInfo());
                baseData.put("type", theme.getType());
                return buildThemeInfoResponse(baseData, theme);
            default:
                return buildThemeInfoResponse(buildDefaultInfoData(theme), theme);
        }

        if (StrUtil.isBlank(data)) {
            return buildThemeInfoResponse(new JSONObject(), theme);
        }

        try {
            return buildThemeInfoResponse(filterThemeInfoComponents(JSON.parseObject(data)), theme);
        } catch (Exception e) {
            throw new CrmebException("主题" + dataType + "数据不是正确的JSON对象格式");
        }
    }

    /**
     * 查询主题版本号，id为0时查询当前启用主题。
     *
     * @param id 主题ID
     * @return 主题版本号
     */
    @Override
    public String getVersion(Integer id) {
        Theme theme = getInfo(id);
        if (theme == null) {
            throw new CrmebException("主题不存在");
        }
        return theme.getVersion();
    }

    /**
     * 过滤主题信息中不需要返回的组件。
     *
     * @param data 主题JSON数据
     * @return 过滤后的主题JSON数据
     */
    private JSONObject filterThemeInfoComponents(JSONObject data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        JSONObject value = data.getJSONObject("value");
        if (value == null || value.isEmpty()) {
            return data;
        }

        List<String> removeKeys = new ArrayList<>();
        for (String key : value.keySet()) {
            JSONObject component = value.getJSONObject(key);
            if (component == null) {
                continue;
            }
            String name = component.getString("name");
            if (THEME_INFO_EXCLUDED_COMPONENT_NAMES.contains(name)) {
                removeKeys.add(key);
            }
        }
        removeKeys.forEach(value::remove);
        return data;
    }

    /**
     * 导出主题数据包。
     *
     * @param id 主题ID
     * @return ZIP下载地址
     */
    @Override
    public String exportTheme(Integer id) {
        if (id == null || id <= 0) {
            throw new CrmebException("主题ID不正确");
        }
        Theme theme = getByIdAndNotDeleted(id);
        if (theme == null) {
            throw new CrmebException("主题不存在");
        }

        String zipFileName = buildThemeZipFileName(theme);
        File exportDir = new File(getThemeDownloadRootPath(), String.valueOf(theme.getId()));
        File imageDir = new File(exportDir, "images");
        File zipFile = new File(exportDir, zipFileName);
        File tempZipFile = new File(getThemeDownloadRootPath(), theme.getId() + "_" + System.currentTimeMillis() + ".zip");

        try {
            FileUtil.del(exportDir);
            if (!imageDir.mkdirs() && !imageDir.exists()) {
                throw new CrmebException("主题导出目录创建失败");
            }

            Map<String, Object> config = buildThemeExportConfig(theme);
            processThemeMainImages(config, exportDir);
            processThemeJsonImages(config, imageDir);
            rewriteThemeDefaultData(config);
            writeThemeConfig(config, new File(exportDir, "config.json"));
            zipThemeDirectory(exportDir.toPath(), tempZipFile.toPath());
            Files.move(tempZipFile.toPath(), zipFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (CrmebException e) {
            throw e;
        } catch (Exception e) {
            throw new CrmebException("主题导出失败：" + e.getMessage());
        } finally {
            FileUtil.del(tempZipFile);
        }

        return getThemeDownloadUrl(theme.getId(), zipFileName);
    }

    /**
     * 创建主题导出记录并异步导出主题数据包。
     *
     * @param id 主题ID
     * @return 下载记录ID
     */
    @Override
    public Integer createExportRecord(Integer id) {
        if (id == null || id <= 0) {
            throw new CrmebException("主题ID不正确");
        }
        Theme theme = getByIdAndNotDeleted(id);
        if (theme == null) {
            throw new CrmebException("主题不存在");
        }

        ThemeDownload record = new ThemeDownload();
        record.setTitle(theme.getTitle());
        record.setTid(theme.getId());
        record.setDownloadTime(CrmebDateUtil.getNowTime());
        record.setDownloadUrl("");
        if (!themeDownloadService.save(record)) {
            throw new CrmebException("主题下载记录创建失败");
        }

        themeDownloadService.exportThemeDownload(record.getId(), theme.getId());
        return record.getId();
    }

    /**
     * 查询主题导出下载地址。
     *
     * @param recordId 下载记录ID
     * @return ZIP下载地址，未生成时为空字符串
     */
    @Override
    public String getExportDownloadUrl(Integer recordId) {
        if (recordId == null || recordId <= 0) {
            throw new CrmebException("下载记录ID不正确");
        }
        ThemeDownload record = themeDownloadService.getById(recordId);
        if (record == null) {
            throw new CrmebException("下载记录不存在");
        }
        return record.getDownloadUrl();
    }

    /**
     * 导入主题数据包。
     *
     * @param request 导入参数
     * @return 导入后的主题ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer importTheme(ThemeImportRequest request) {
        if (request == null || StrUtil.isBlank(request.getUrl())) {
            throw new CrmebException("请上传主题ZIP文件");
        }

        File zipFile = getThemeImportZipFile(request.getUrl());
        if (!zipFile.exists() || !zipFile.isFile()) {
            throw new CrmebException("文件不存在");
        }
        if (!zipFile.getName().toLowerCase().endsWith(".zip")) {
            throw new CrmebException("请上传ZIP文件");
        }

        String importTimestamp = CrmebDateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM);
        File importDir = getThemeImportRootPath();
        File uploadDir = getThemeUploadRootPath(importTimestamp);
        String uploadWebPath = THEME_SAVE_IMAGE_WEB_PATH + importTimestamp + "/";

        try {
            FileUtil.del(importDir);
            FileUtil.del(uploadDir);
            if (!importDir.mkdirs() && !importDir.exists()) {
                throw new CrmebException("主题导入目录创建失败");
            }
            if (!uploadDir.mkdirs() && !uploadDir.exists()) {
                throw new CrmebException("主题图片目录创建失败");
            }

            unzipThemePackage(zipFile, importDir);
            JSONObject config = readThemeImportConfig(importDir);
            String baseUrl = getLocalUploadBaseUrl();
            Map<String, String> imageMap = copyThemeImportImages(importDir, uploadDir, uploadWebPath, baseUrl);
            rewriteThemeImportImageFields(config, imageMap);
            rewriteThemeImportDataFields(config, imageMap);
            return saveThemeImportData(config);
        } catch (CrmebException e) {
            throw e;
        } catch (Exception e) {
            throw new CrmebException("主题导入失败：" + e.getMessage());
        }
    }

    /**
     * 获取导入ZIP本地文件。
     *
     * @param importUrl 上传后的文件路径
     * @return ZIP文件
     */
    private File getThemeImportZipFile(String importUrl) {
        String path = importUrl.trim();
        boolean remoteUrl = isRemoteUrl(path);
        if (remoteUrl) {
            try {
                path = new URL(path).getPath();
            } catch (Exception ignored) {
                path = importUrl.trim();
            }
        }

        int queryIndex = path.indexOf('?');
        if (queryIndex >= 0) {
            path = path.substring(0, queryIndex);
        }
        int fragmentIndex = path.indexOf('#');
        if (fragmentIndex >= 0) {
            path = path.substring(0, fragmentIndex);
        }
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            path = importUrl.trim();
        }

        File file = new File(path);
        if (!remoteUrl && file.isAbsolute()) {
            return file;
        }

        path = path.replace("\\", "/");
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        File rootPathFile = new File(normalizeRootPath(crmebConfig.getImagePath()), path);
        if (rootPathFile.exists()) {
            return rootPathFile;
        }
        if (!path.startsWith(UploadConstants.UPLOAD_FILE_KEYWORD + "/")) {
            return new File(normalizeRootPath(crmebConfig.getImagePath()), UploadConstants.UPLOAD_FILE_KEYWORD + "/" + path);
        }
        return rootPathFile;
    }

    /**
     * 安全解压主题ZIP。
     *
     * @param zipFile ZIP文件
     * @param importDir 导入目录
     */
    private void unzipThemePackage(File zipFile, File importDir) {
        Path importRootPath = importDir.toPath().toAbsolutePath().normalize();
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (StrUtil.isBlank(entryName)) {
                    zipInputStream.closeEntry();
                    continue;
                }

                Path targetPath = importRootPath.resolve(entryName.replace("\\", "/")).normalize();
                if (!targetPath.startsWith(importRootPath)) {
                    throw new CrmebException("主题ZIP文件路径不安全");
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(targetPath);
                } else {
                    Path parentPath = targetPath.getParent();
                    if (parentPath != null) {
                        Files.createDirectories(parentPath);
                    }
                    Files.copy(zipInputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipInputStream.closeEntry();
            }
        } catch (CrmebException e) {
            throw e;
        } catch (IOException e) {
            throw new CrmebException("主题ZIP解压失败");
        }
    }

    /**
     * 读取导入配置。
     *
     * @param importDir 导入目录
     * @return 主题配置
     */
    private JSONObject readThemeImportConfig(File importDir) {
        File configFile = new File(importDir, "config.json");
        if (!configFile.exists() || !configFile.isFile()) {
            throw new CrmebException("文件不存在");
        }
        try {
            String content = new String(Files.readAllBytes(configFile.toPath()), StandardCharsets.UTF_8);
            Object parsedConfig = JSON.parse(content);
            if (!(parsedConfig instanceof JSONObject)) {
                throw new CrmebException("文件内容错误");
            }
            return (JSONObject) parsedConfig;
        } catch (CrmebException e) {
            throw e;
        } catch (Exception e) {
            throw new CrmebException("文件内容错误");
        }
    }

    /**
     * 复制导入包图片并建立路径映射。
     *
     * @param importDir 导入目录
     * @param uploadDir 图片目录
     * @param uploadWebPath 图片Web路径
     * @param baseUrl 本地图片访问基础地址
     * @return 包内路径和新图片绝对地址映射
     */
    private Map<String, String> copyThemeImportImages(File importDir, File uploadDir, String uploadWebPath, String baseUrl) {
        Map<String, String> imageMap = new LinkedHashMap<>();
        Path importRootPath = importDir.toPath().toAbsolutePath().normalize();
        Path uploadRootPath = uploadDir.toPath().toAbsolutePath().normalize();

        try (Stream<Path> paths = Files.walk(importRootPath)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> copyThemeImportImage(importRootPath, uploadRootPath, filePath, uploadWebPath, baseUrl, imageMap));
        } catch (CrmebException e) {
            throw e;
        } catch (IOException e) {
            throw new CrmebException("主题图片复制失败");
        }
        return imageMap;
    }

    /**
     * 复制单张导入图片。
     *
     * @param importRootPath 导入根路径
     * @param uploadRootPath 图片根路径
     * @param filePath 当前文件路径
     * @param uploadWebPath 图片Web路径
     * @param baseUrl 本地图片访问基础地址
     * @param imageMap 图片映射
     */
    private void copyThemeImportImage(Path importRootPath, Path uploadRootPath, Path filePath,
                                      String uploadWebPath, String baseUrl, Map<String, String> imageMap) {
        String relativePath = importRootPath.relativize(filePath).toString().replace(File.separatorChar, '/');
        if ("config.json".equalsIgnoreCase(relativePath) || !isThemeImageFile(relativePath)) {
            return;
        }

        Path targetPath = uploadRootPath.resolve(relativePath).normalize();
        if (!targetPath.startsWith(uploadRootPath)) {
            throw new CrmebException("主题图片路径不安全");
        }

        try {
            Path parentPath = targetPath.getParent();
            if (parentPath != null) {
                Files.createDirectories(parentPath);
            }
            Files.copy(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            String imageWebPath = uploadWebPath + relativePath;
            saveThemeImportAttachment(targetPath, imageWebPath);
            putThemeImportImageMap(imageMap, relativePath, buildThemeImportImageUrl(imageWebPath, baseUrl));
        } catch (IOException e) {
            return;
        }
    }

    /**
     * 保存导入主题图片到素材表。
     *
     * @param imagePath 图片文件路径
     * @param imageWebPath 图片Web路径
     */
    private void saveThemeImportAttachment(Path imagePath, String imageWebPath) {
        try {
            SystemAttachment attachment = new SystemAttachment();
            String fileName = getUrlFileName(imageWebPath);
            attachment.setName(StrUtil.isBlank(fileName) ? imageWebPath : fileName);
            attachment.setAttDir(imageWebPath);
            attachment.setSattDir(imageWebPath);
            attachment.setAttSize(String.valueOf(Files.size(imagePath)));
            attachment.setAttType(getImageExtension(imageWebPath, ""));
            attachment.setImageType(1);
            attachment.setPid(THEME_ATTACHMENT_PID);
            Date now = new Date();
            attachment.setCreateTime(now);
            attachment.setUpdateTime(now);
            if (!systemAttachmentService.save(attachment)) {
                throw new CrmebException("主题图片素材保存失败");
            }
        } catch (IOException e) {
            throw new CrmebException("主题图片素材保存失败");
        }
    }

    /**
     * 建立导入包图片路径和新素材地址的映射。
     *
     * @param imageMap 图片映射
     * @param relativePath 包内相对路径
     * @param imageUrl 新图片绝对地址
     */
    private void putThemeImportImageMap(Map<String, String> imageMap, String relativePath, String imageUrl) {
        if (StrUtil.isBlank(relativePath) || StrUtil.isBlank(imageUrl)) {
            return;
        }
        String packagePath = normalizeThemeImportPackagePath(relativePath);
        if (StrUtil.isBlank(packagePath)) {
            return;
        }
        imageMap.put(packagePath, imageUrl);
        imageMap.put(THEME_DOWNLOAD_WEB_PATH + packagePath, imageUrl);
        String fileName = getUrlFileName(packagePath);
        if (StrUtil.isNotBlank(fileName)) {
            imageMap.putIfAbsent(fileName, imageUrl);
        }
    }

    /**
     * 重写导入主题主图字段里的图片路径。
     *
     * @param config 主题配置
     * @param imageMap 图片映射
     */
    private void rewriteThemeImportImageFields(Map<String, Object> config, Map<String, String> imageMap) {
        for (String field : THEME_IMPORT_IMAGE_FIELDS) {
            Object value = config.get(field);
            if (value == null) {
                continue;
            }
            String imageUrl = findThemeImportImageUrl(getThemeConfigStringValue(value), imageMap);
            if (StrUtil.isNotBlank(imageUrl)) {
                config.put(field, imageUrl);
            }
        }
    }

    /**
     * 重写导入主题数据字段里的图片路径。
     *
     * @param config 主题配置
     * @param imageMap 图片映射
     */
    private void rewriteThemeImportDataFields(Map<String, Object> config, Map<String, String> imageMap) {
        for (String field : THEME_IMPORT_DATA_FIELDS) {
            Object value = config.get(field);
            if (value == null) {
                continue;
            }
            config.put(field, rewriteThemeImportDataField(value, imageMap));
        }
    }

    /**
     * 重写单个主题数据字段。
     *
     * @param value 字段值
     * @param imageMap 图片映射
     * @return 重写后的字段字符串
     */
    private String rewriteThemeImportDataField(Object value, Map<String, String> imageMap) {
        String stringValue = getThemeConfigStringValue(value);
        if (StrUtil.isBlank(stringValue) || "null".equalsIgnoreCase(stringValue.trim())) {
            return stringValue;
        }

        Object jsonData = parseThemeImportDataJson(stringValue);
        if (jsonData instanceof Map || jsonData instanceof List) {
            Object rewrittenValue = rewriteThemeImportValue(jsonData, imageMap);
            return JSON.toJSONString(rewrittenValue);
        }
        return stringValue;
    }

    /**
     * 解析主题数据JSON，兼容JSON字符串被二次编码的情况。
     *
     * @param data 主题数据
     * @return JSON对象
     */
    private Object parseThemeImportDataJson(String data) {
        String currentValue = data.trim();
        for (int i = 0; i < 3; i++) {
            if (!currentValue.startsWith("{") && !currentValue.startsWith("[") && !currentValue.startsWith("\"")) {
                return null;
            }
            try {
                Object parsedValue = JSON.parse(currentValue);
                if (parsedValue instanceof Map || parsedValue instanceof List) {
                    return parsedValue;
                }
                if (parsedValue instanceof String) {
                    currentValue = ((String) parsedValue).trim();
                    continue;
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 递归重写导入主题数据JSON。
     *
     * @param value 配置值
     * @param imageMap 图片映射
     * @return 重写后的配置值
     */
    @SuppressWarnings("unchecked")
    private Object rewriteThemeImportValue(Object value, Map<String, String> imageMap) {
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                entry.setValue(rewriteThemeImportValue(entry.getValue(), imageMap));
            }
            return map;
        }
        if (value instanceof List) {
            List<Object> list = (List<Object>) value;
            for (int i = 0; i < list.size(); i++) {
                list.set(i, rewriteThemeImportValue(list.get(i), imageMap));
            }
            return list;
        }
        if (!(value instanceof String)) {
            return value;
        }
        return rewriteThemeImportString((String) value, imageMap);
    }

    /**
     * 重写导入主题数据JSON中的字符串。
     *
     * @param value 字符串值
     * @param imageMap 图片映射
     * @return 重写后的字符串
     */
    private String rewriteThemeImportString(String value, Map<String, String> imageMap) {
        if (StrUtil.isBlank(value)) {
            return value;
        }

        String imageUrl = findThemeImportImageUrl(value, imageMap);
        if (StrUtil.isNotBlank(imageUrl)) {
            return imageUrl;
        }
        return value;
    }

    /**
     * 查找导入图片新路径。
     *
     * @param value 原路径
     * @param imageMap 图片映射
     * @return 新图片绝对地址
     */
    private String findThemeImportImageUrl(String value, Map<String, String> imageMap) {
        String packagePath = normalizeThemeImportPackagePath(value);
        if (StrUtil.isBlank(packagePath)) {
            return "";
        }

        String imageUrl = imageMap.get(packagePath);
        if (StrUtil.isNotBlank(imageUrl)) {
            return imageUrl;
        }

        if (packagePath.startsWith(THEME_DOWNLOAD_WEB_PATH)) {
            String relativePath = packagePath.substring(THEME_DOWNLOAD_WEB_PATH.length());
            imageUrl = imageMap.get(relativePath);
            if (StrUtil.isNotBlank(imageUrl)) {
                return imageUrl;
            }
            int slashIndex = relativePath.indexOf('/');
            if (slashIndex >= 0 && slashIndex < relativePath.length() - 1) {
                String downloadRelativePath = relativePath.substring(slashIndex + 1);
                imageUrl = imageMap.get(downloadRelativePath);
                if (StrUtil.isNotBlank(imageUrl)) {
                    return imageUrl;
                }
            }
            String fileName = getUrlFileName(relativePath);
            imageUrl = imageMap.get(fileName);
            if (StrUtil.isNotBlank(imageUrl)) {
                return imageUrl;
            }
        }
        return "";
    }

    /**
     * 规范化导入包内路径。
     *
     * @param value 原路径
     * @return 包内路径
     */
    private String normalizeThemeImportPackagePath(String value) {
        String path = value.trim();
        if (isRemoteUrl(path)) {
            try {
                path = new URL(path).getPath();
            } catch (Exception ignored) {
                return "";
            }
        } else {
            int queryIndex = path.indexOf('?');
            if (queryIndex >= 0) {
                path = path.substring(0, queryIndex);
            }
            int fragmentIndex = path.indexOf('#');
            if (fragmentIndex >= 0) {
                path = path.substring(0, fragmentIndex);
            }
        }

        path = path.replace("\\/", "/").replace("\\", "/");
        while (path.contains("//")) {
            path = path.replace("//", "/");
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * 拼装导入图片访问地址。
     *
     * @param imageWebPath 图片Web路径
     * @param baseUrl 本地图片访问基础地址
     * @return 图片访问地址
     */
    private String buildThemeImportImageUrl(String imageWebPath, String baseUrl) {
        if (StrUtil.isBlank(imageWebPath)) {
            return "";
        }
        if (hasDomainPrefix(imageWebPath)) {
            return imageWebPath;
        }
        if (StrUtil.isBlank(baseUrl)) {
            return imageWebPath;
        }
        return removeEndSlash(baseUrl) + "/" + removeStartSlash(imageWebPath);
    }

    /**
     * 保存导入主题数据。
     *
     * @param config 主题配置
     * @return 新主题ID
     */
    private Integer saveThemeImportData(Map<String, Object> config) {
        Integer nowTime = CrmebDateUtil.getNowTime();
        Theme theme = new Theme();
        theme.setVersion(generateThemeVersion());
        theme.setTitle(getConfigString(config, "title"));
        theme.setInfo(getConfigString(config, "info"));
        theme.setType(1);
        theme.setHomeData(getThemeConfigString(config, "home_data"));
        theme.setHomeImage(getConfigString(config, "home_image"));
        theme.setHomeDataId(getConfigInteger(config, "home_data_id", 0));
        theme.setHomeDataUpdateTime(nowTime);
        theme.setCategoryData(getThemeConfigString(config, "category_data"));
        theme.setCategoryImage(getConfigString(config, "category_image"));
        theme.setCategoryDataId(getConfigInteger(config, "category_data_id", 0));
        theme.setCategoryDataUpdateTime(nowTime);
        theme.setDetailData(getThemeConfigString(config, "detail_data"));
        theme.setDetailImage(getConfigString(config, "detail_image"));
        theme.setDetailDataId(getConfigInteger(config, "detail_data_id", 0));
        theme.setDetailDataUpdateTime(nowTime);
        theme.setUserData(getThemeConfigString(config, "user_data"));
        theme.setUserImage(getConfigString(config, "user_image"));
        theme.setUserDataId(getConfigInteger(config, "user_data_id", 0));
        theme.setUserDataUpdateTime(nowTime);
        theme.setThemeData(getThemeConfigStringWithDefault(config.get("theme_data"), DEFAULT_THEME_DATA));
        theme.setThemeDataId(getConfigInteger(config, "theme_data_id", 0));
        theme.setThemeDataUpdateTime(nowTime);
        theme.setHomeDefaultData(getThemeConfigStringWithDefault(config.get("home_default_data"), theme.getHomeData()));
        theme.setHomeDefaultImage(getThemeConfigStringWithDefault(config.get("home_default_image"), theme.getHomeImage()));
        theme.setCategoryDefaultData(getThemeConfigStringWithDefault(config.get("category_default_data"), theme.getCategoryData()));
        theme.setCategoryDefaultImage(getThemeConfigStringWithDefault(config.get("category_default_image"), theme.getCategoryImage()));
        theme.setDetailDefaultData(getThemeConfigStringWithDefault(config.get("detail_default_data"), theme.getDetailData()));
        theme.setDetailDefaultImage(getThemeConfigStringWithDefault(config.get("detail_default_image"), theme.getDetailImage()));
        theme.setUserDefaultData(getThemeConfigStringWithDefault(config.get("user_default_data"), theme.getUserData()));
        theme.setUserDefaultImage(getThemeConfigStringWithDefault(config.get("user_default_image"), theme.getUserImage()));
        theme.setThemeDefaultData(getThemeConfigStringWithDefault(config.get("theme_default_data"), theme.getThemeData()));
        theme.setPageType("theme");
        theme.setIsUse(0);
        theme.setIsDel(0);
        theme.setAddTime(nowTime);
        theme.setUpTime(nowTime);
        initDefaultValue(theme);

        if (!save(theme)) {
            throw new CrmebException("主题导入失败");
        }
        return theme.getId();
    }

    /**
     * 获取配置字符串，非字符串类型会编码成JSON。
     *
     * @param config 配置
     * @param key 键名
     * @return 字符串值
     */
    private String getThemeConfigString(Map<String, Object> config, String key) {
        return getThemeConfigStringValue(config.get(key));
    }

    /**
     * 获取配置字符串，空值使用默认值。
     *
     * @param value 配置值
     * @param defaultValue 默认值
     * @return 字符串值
     */
    private String getThemeConfigStringWithDefault(Object value, String defaultValue) {
        String stringValue = getThemeConfigStringValue(value);
        if (StrUtil.isBlank(stringValue) || "null".equalsIgnoreCase(stringValue.trim())) {
            return defaultValue == null ? "" : defaultValue;
        }
        return stringValue;
    }

    /**
     * 转换配置值为字符串。
     *
     * @param value 配置值
     * @return 字符串值
     */
    private String getThemeConfigStringValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String) value;
        }
        return JSON.toJSONString(value);
    }

    /**
     * 获取配置整数。
     *
     * @param config 配置
     * @param key 键名
     * @param defaultValue 默认值
     * @return 整数值
     */
    private Integer getConfigInteger(Map<String, Object> config, String key, Integer defaultValue) {
        Object value = config.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        String stringValue = String.valueOf(value);
        if (StrUtil.isBlank(stringValue) || "null".equalsIgnoreCase(stringValue.trim())) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(stringValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 判断是否图片文件。
     *
     * @param filePath 文件路径
     * @return 是否图片
     */
    private boolean isThemeImageFile(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filePath.length() - 1) {
            return false;
        }
        String extension = filePath.substring(dotIndex + 1).toLowerCase();
        return THEME_IMAGE_EXTENSIONS.contains(extension);
    }

    /**
     * 获取导入临时目录。
     *
     * @return 导入临时目录
     */
    private File getThemeImportRootPath() {
        return new File(normalizeRootPath(crmebConfig.getImagePath()), THEME_IMPORT_FILE_PATH);
    }

    /**
     * 获取导入主题图片目录。
     *
     * @return 图片目录
     */
    private File getThemeUploadRootPath(String timestamp) {
        return new File(normalizeRootPath(crmebConfig.getImagePath()), THEME_SAVE_IMAGE_WEB_PATH + timestamp);
    }

    /**
     * 获取本地图片访问基础地址。
     *
     * @return 本地图片访问基础地址
     */
    private String getLocalUploadBaseUrl() {
        String localUploadUrl = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_LOCAL_UPLOAD_URL);
        return StrUtil.isBlank(localUploadUrl) ? "" : removeEndSlash(localUploadUrl.trim());
    }

    /**
     * 移除开头斜杠。
     *
     * @param value 字符串
     * @return 处理后的字符串
     */
    private String removeStartSlash(String value) {
        if (StrUtil.isBlank(value)) {
            return value;
        }
        while (value.startsWith("/")) {
            value = value.substring(1);
        }
        return value;
    }

    /**
     * 按ID查询未删除主题。
     *
     * @param id 主题ID
     * @return 主题详情
     */
    private Theme getByIdAndNotDeleted(Integer id) {
        LambdaQueryWrapper<Theme> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Theme::getId, id);
        queryWrapper.eq(Theme::getIsDel, 0);
        queryWrapper.last(" limit 1");
        return dao.selectOne(queryWrapper);
    }

    /**
     * 构建主题导出配置。
     *
     * @param theme 主题实体
     * @return 导出配置
     */
    private Map<String, Object> buildThemeExportConfig(Theme theme) {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("id", theme.getId());
        config.put("version", theme.getVersion());
        config.put("title", theme.getTitle());
        config.put("info", theme.getInfo());
        config.put("type", theme.getType());
        config.put("home_data", theme.getHomeData());
        config.put("home_image", theme.getHomeImage());
        config.put("home_data_id", theme.getHomeDataId());
        config.put("home_data_id_title", getThemeTitleById(theme.getHomeDataId()));
        config.put("home_data_update_time", formatTimestamp(theme.getHomeDataUpdateTime()));
        config.put("category_data", theme.getCategoryData());
        config.put("category_image", theme.getCategoryImage());
        config.put("category_data_id", theme.getCategoryDataId());
        config.put("category_data_id_title", getThemeTitleById(theme.getCategoryDataId()));
        config.put("category_data_update_time", formatTimestamp(theme.getCategoryDataUpdateTime()));
        config.put("detail_data", theme.getDetailData());
        config.put("detail_image", theme.getDetailImage());
        config.put("detail_data_id", theme.getDetailDataId());
        config.put("detail_data_id_title", getThemeTitleById(theme.getDetailDataId()));
        config.put("detail_data_update_time", formatTimestamp(theme.getDetailDataUpdateTime()));
        config.put("user_data", theme.getUserData());
        config.put("user_image", theme.getUserImage());
        config.put("user_data_id", theme.getUserDataId());
        config.put("user_data_id_title", getThemeTitleById(theme.getUserDataId()));
        config.put("user_data_update_time", formatTimestamp(theme.getUserDataUpdateTime()));
        String themeData = StrUtil.isBlank(theme.getThemeData()) || "null".equalsIgnoreCase(theme.getThemeData().trim())
                ? DEFAULT_THEME_DATA : theme.getThemeData();
        config.put("theme_data", parseJsonObjectOrString(themeData));
        config.put("theme_data_id", theme.getThemeDataId());
        config.put("theme_data_id_title", getThemeTitleById(theme.getThemeDataId()));
        config.put("theme_data_update_time", formatTimestamp(theme.getThemeDataUpdateTime()));
        config.put("home_default_data", theme.getHomeDefaultData());
        config.put("home_default_image", theme.getHomeDefaultImage());
        config.put("category_default_data", theme.getCategoryDefaultData());
        config.put("category_default_image", theme.getCategoryDefaultImage());
        config.put("detail_default_data", theme.getDetailDefaultData());
        config.put("detail_default_image", theme.getDetailDefaultImage());
        config.put("user_default_data", theme.getUserDefaultData());
        config.put("user_default_image", theme.getUserDefaultImage());
        config.put("theme_default_data", parseJsonObjectOrString(themeData));
        config.put("page_type", theme.getPageType());
        config.put("is_use", theme.getIsUse());
        config.put("is_del", theme.getIsDel());
        config.put("add_time", formatTimestamp(theme.getAddTime()));
        config.put("up_time", formatTimestamp(theme.getUpTime()));
        return config;
    }

    /**
     * 处理主题主图。
     *
     * @param config 导出配置
     * @param exportDir 导出目录
     */
    private void processThemeMainImages(Map<String, Object> config, File exportDir) {
        for (int i = 0; i < THEME_MAIN_IMAGES.size(); i++) {
            String field = THEME_MAIN_IMAGES.get(i);
            String originalUrl = getConfigString(config, field);
            if (StrUtil.isBlank(originalUrl)) {
                continue;
            }

            String extension = getImageExtension(originalUrl, "png");
            String fileName = (i + 1) + "_" + field + "." + extension;
            File targetFile = new File(exportDir, fileName);
            if (!copyThemeImage(originalUrl, targetFile)) {
                continue;
            }

            String packagePath = THEME_DOWNLOAD_WEB_PATH + fileName;
            config.put(field, packagePath);
            config.put(toDefaultImageField(field), packagePath);
        }
    }

    /**
     * 处理主题模块JSON里的图片。
     *
     * @param config 导出配置
     * @param imageDir 图片目录
     */
    private void processThemeJsonImages(Map<String, Object> config, File imageDir) {
        Map<String, String> imageMap = new HashMap<>();
        for (String field : THEME_JSON_IMAGE_FIELDS) {
            Object originalValue = config.get(field);
            Object jsonData = parseJsonData(getConfigString(config, field));
            if (jsonData == null) {
                continue;
            }
            Object processedData = normalizeThemeExportDataValue(replaceJsonImageValue(jsonData, imageDir, imageMap));
            if (originalValue instanceof Map || originalValue instanceof List) {
                config.put(field, processedData);
            } else {
                config.put(field, JSON.toJSONString(processedData, SerializerFeature.WriteMapNullValue));
            }
        }
    }

    /**
     * 同步默认配置数据。
     *
     * @param config 导出配置
     */
    private void rewriteThemeDefaultData(Map<String, Object> config) {
        if (config.containsKey("home_data")) {
            config.put("home_default_data", config.get("home_data"));
        }
        if (config.containsKey("category_data")) {
            config.put("category_default_data", config.get("category_data"));
        }
        if (config.containsKey("detail_data")) {
            config.put("detail_default_data", config.get("detail_data"));
        }
        if (config.containsKey("user_data")) {
            config.put("user_default_data", config.get("user_data"));
        }
        if (config.containsKey("theme_data")) {
            config.put("theme_default_data", config.get("theme_data"));
        }
    }

    /**
     * 规范化导出主题数据结构。
     *
     * @param value JSON值
     * @return 规范化后的JSON值
     */
    @SuppressWarnings("unchecked")
    private Object normalizeThemeExportDataValue(Object value) {
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            fillGoodsListDefaultFields(map);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                entry.setValue(normalizeThemeExportDataValue(entry.getValue()));
            }
            return map;
        }
        if (value instanceof List) {
            List<Object> list = (List<Object>) value;
            for (int i = 0; i < list.size(); i++) {
                list.set(i, normalizeThemeExportDataValue(list.get(i)));
            }
            return list;
        }
        return value;
    }

    /**
     * 补齐商品列表项默认字段。
     *
     * @param map JSON对象
     */
    @SuppressWarnings("unchecked")
    private void fillGoodsListDefaultFields(Map<String, Object> map) {
        Object goodsList = map.get("goodsList");
        if (!(goodsList instanceof Map)) {
            return;
        }
        Object listValue = ((Map<String, Object>) goodsList).get("list");
        if (!(listValue instanceof List)) {
            return;
        }
        for (Object item : (List<Object>) listValue) {
            if (item instanceof Map) {
                ((Map<String, Object>) item).putIfAbsent("is_bargain", null);
            }
        }
    }

    /**
     * 递归替换JSON中的图片路径。
     *
     * @param value JSON值
     * @param imageDir 图片目录
     * @param imageMap 图片映射
     * @return 替换后的JSON值
     */
    @SuppressWarnings("unchecked")
    private Object replaceJsonImageValue(Object value, File imageDir, Map<String, String> imageMap) {
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                entry.setValue(replaceJsonImageValue(entry.getValue(), imageDir, imageMap));
            }
            return map;
        }
        if (value instanceof List) {
            List<Object> list = (List<Object>) value;
            for (int i = 0; i < list.size(); i++) {
                list.set(i, replaceJsonImageValue(list.get(i), imageDir, imageMap));
            }
            return list;
        }
        if (!(value instanceof String)) {
            return value;
        }

        String imageUrl = (String) value;
        if (!isThemeAttachImage(imageUrl)) {
            return value;
        }
        if (imageMap.containsKey(imageUrl)) {
            return imageMap.get(imageUrl);
        }

        String fileName = buildJsonImageFileName(imageUrl);
        File targetFile = new File(imageDir, fileName);
        if (!copyThemeImage(imageUrl, targetFile)) {
            return value;
        }

        String packagePath = "images/" + fileName;
        imageMap.put(imageUrl, packagePath);
        return packagePath;
    }

    /**
     * 写入配置文件。
     *
     * @param config 导出配置
     * @param configFile 配置文件
     */
    private void writeThemeConfig(Map<String, Object> config, File configFile) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8)) {
            writer.write(JSON.toJSONString(config, SerializerFeature.PrettyFormat));
        } catch (IOException e) {
            throw new CrmebException("主题配置文件生成失败");
        }
    }

    /**
     * 压缩导出目录。
     *
     * @param sourceDir 导出目录
     * @param zipFile ZIP文件
     */
    private void zipThemeDirectory(Path sourceDir, Path zipFile) {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile.toFile())));
             Stream<Path> paths = Files.walk(sourceDir)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> addThemeZipEntry(sourceDir, path, zipOutputStream));
        } catch (IOException e) {
            throw new CrmebException("主题ZIP打包失败");
        }
    }

    /**
     * 添加ZIP文件条目。
     *
     * @param sourceDir 导出目录
     * @param filePath 文件路径
     * @param zipOutputStream ZIP输出流
     */
    private void addThemeZipEntry(Path sourceDir, Path filePath, ZipOutputStream zipOutputStream) {
        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(filePath))) {
            String relativePath = sourceDir.relativize(filePath).toString().replace(File.separatorChar, '/');
            zipOutputStream.putNextEntry(new ZipEntry(relativePath));
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, length);
            }
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            throw new CrmebException("主题ZIP文件写入失败");
        }
    }

    /**
     * 复制或下载主题图片。
     *
     * @param originalUrl 原图片地址
     * @param targetFile 目标文件
     * @return 是否成功
     */
    private boolean copyThemeImage(String originalUrl, File targetFile) {
        if (StrUtil.isBlank(originalUrl)) {
            return false;
        }
        try {
            File parentFile = targetFile.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                return false;
            }
            File sourceFile = new File(getLocalThemeImagePath(originalUrl));
            if (sourceFile.exists() && sourceFile.isFile()) {
                Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
            if (isRemoteUrl(originalUrl)) {
                return downloadThemeRemoteImage(originalUrl, targetFile);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 下载远程图片。
     *
     * @param imageUrl 图片地址
     * @param targetFile 目标文件
     * @return 是否成功
     */
    private boolean downloadThemeRemoteImage(String imageUrl, File targetFile) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(imageUrl).openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Referer", getSiteUrl());
            int responseCode = connection.getResponseCode();
            if (responseCode < HttpURLConnection.HTTP_OK || responseCode >= HttpURLConnection.HTTP_MULT_CHOICE) {
                return false;
            }
            try (BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
                 BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetFile))) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 解析JSON配置数据。
     *
     * @param data JSON字符串
     * @return JSON对象
     */
    private Object parseJsonData(String data) {
        if (StrUtil.isBlank(data) || "null".equalsIgnoreCase(data.trim())) {
            return null;
        }
        try {
            return JSON.parse(data);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析主题风格JSON。
     *
     * @param data JSON字符串
     * @return JSON对象或原字符串
     */
    private Object parseJsonObjectOrString(String data) {
        if (StrUtil.isBlank(data) || "null".equalsIgnoreCase(data.trim())) {
            return new JSONObject();
        }
        try {
            return JSON.parse(data);
        } catch (Exception e) {
            return data;
        }
    }

    /**
     * 判断是否主题资源图片。
     *
     * @param value 字符串值
     * @return 是否图片
     */
    private boolean isThemeAttachImage(String value) {
        if (StrUtil.isBlank(value)) {
            return false;
        }
        return value.contains("uploads/attach")
                || value.contains(UploadConstants.UPLOAD_FILE_KEYWORD + "/")
                || value.contains(UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/")
                || isRemoteUrl(value);
    }

    /**
     * 判断是否远程URL。
     *
     * @param value 字符串值
     * @return 是否远程URL
     */
    private boolean isRemoteUrl(String value) {
        if (StrUtil.isBlank(value)) {
            return false;
        }
        String url = value.trim().toLowerCase();
        return url.startsWith("http://") || url.startsWith("https://");
    }

    /**
     * 判断图片地址是否已经带有域名前缀。
     *
     * @param value 图片地址
     * @return 是否带有域名前缀
     */
    private boolean hasDomainPrefix(String value) {
        if (StrUtil.isBlank(value)) {
            return false;
        }
        String url = value.trim();
        return isRemoteUrl(url) || url.startsWith("//");
    }

    /**
     * 获取主题下载根目录。
     *
     * @return 下载根目录
     */
    private File getThemeDownloadRootPath() {
        return new File(normalizeRootPath(crmebConfig.getImagePath()), THEME_DOWNLOAD_FILE_PATH);
    }

    /**
     * 生成主题下载地址。
     *
     * @param id 主题ID
     * @param zipFileName ZIP文件名
     * @return 下载地址
     */
    private String getThemeDownloadUrl(Integer id, String zipFileName) {
        return removeEndSlash(getThemeDownloadBaseUrl()) + "/" + THEME_DOWNLOAD_WEB_PATH + id + "/" + zipFileName;
    }

    /**
     * 获取主题导出下载基础地址。
     *
     * @return 下载基础地址
     */
    private String getThemeDownloadBaseUrl() {
        String apiUrl = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
        if (StrUtil.isNotBlank(apiUrl)) {
            return normalizeSiteUrl(apiUrl);
        }
        return "";
    }

    /**
     * 获取站点地址。
     *
     * @return 站点地址
     */
    private String getSiteUrl() {
        String siteUrl = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SITE_URL);
        if (StrUtil.isNotBlank(siteUrl)) {
            return siteUrl.trim();
        }
        if (StrUtil.isNotBlank(crmebConfig.getDomain())) {
            return normalizeSiteUrl(crmebConfig.getDomain());
        }
        String requestDomain = RequestUtil.getDomain();
        if (StrUtil.isNotBlank(requestDomain)) {
            return "http://" + requestDomain;
        }
        return "";
    }

    /**
     * 规范化站点地址。
     *
     * @param siteUrl 站点地址
     * @return 规范化后的站点地址
     */
    private String normalizeSiteUrl(String siteUrl) {
        String url = siteUrl.trim();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        return "http://" + url;
    }

    /**
     * 获取本地图片路径。
     *
     * @param imageUrl 图片地址
     * @return 本地路径
     */
    private String getLocalThemeImagePath(String imageUrl) {
        String path = imageUrl;
        if (isRemoteUrl(path)) {
            try {
                path = new URL(path).getPath();
            } catch (Exception ignored) {
                path = imageUrl;
            }
        }
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            path = imageUrl;
        }
        path = path.replace("\\", "/");
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        return new File(normalizeRootPath(crmebConfig.getImagePath()), path).getPath();
    }

    /**
     * 构建主题ZIP文件名。
     *
     * @param theme 主题实体
     * @return 文件名
     */
    private String buildThemeZipFileName(Theme theme) {
        String title = StrUtil.isBlank(theme.getTitle()) ? "theme_" + theme.getId() : theme.getTitle();
        String fileName = title.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        if (StrUtil.isBlank(fileName)) {
            fileName = "theme_" + theme.getId();
        }
        return fileName + ".zip";
    }

    /**
     * 构建JSON图片文件名。
     *
     * @param imageUrl 图片地址
     * @return 文件名
     */
    private String buildJsonImageFileName(String imageUrl) {
        String extension = getImageExtension(imageUrl, "");
        String fileName = getUrlFileName(imageUrl);
        if (StrUtil.isNotBlank(extension) && StrUtil.isNotBlank(fileName)) {
            return md5(imageUrl) + "_" + fileName;
        }
        return md5(imageUrl) + ".jpg";
    }

    /**
     * 获取URL文件名。
     *
     * @param imageUrl 图片地址
     * @return 文件名
     */
    private String getUrlFileName(String imageUrl) {
        String path = imageUrl;
        try {
            if (isRemoteUrl(imageUrl)) {
                path = new URL(imageUrl).getPath();
            }
            path = URLDecoder.decode(path, "UTF-8");
        } catch (Exception ignored) {
            path = imageUrl;
        }
        int slashIndex = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return slashIndex >= 0 ? path.substring(slashIndex + 1) : path;
    }

    /**
     * 获取图片扩展名。
     *
     * @param imageUrl 图片地址
     * @param defaultExtension 默认扩展名
     * @return 扩展名
     */
    private String getImageExtension(String imageUrl, String defaultExtension) {
        String fileName = getUrlFileName(imageUrl);
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return defaultExtension;
        }
        String extension = fileName.substring(dotIndex + 1).toLowerCase();
        return THEME_IMAGE_EXTENSIONS.contains(extension) ? extension : defaultExtension;
    }

    /**
     * 获取配置字符串。
     *
     * @param config 配置
     * @param key 键名
     * @return 字符串
     */
    private String getConfigString(Map<String, Object> config, String key) {
        Object value = config.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    /**
     * 转换默认图片字段。
     *
     * @param field 当前图片字段
     * @return 默认图片字段
     */
    private String toDefaultImageField(String field) {
        return field.replace("_image", "_default_image");
    }

    /**
     * 规范化根路径。
     *
     * @param rootPath 根路径
     * @return 根路径
     */
    private String normalizeRootPath(String rootPath) {
        if (StrUtil.isBlank(rootPath)) {
            return System.getProperty("user.dir");
        }
        return rootPath;
    }

    /**
     * 移除结尾斜杠。
     *
     * @param value 字符串
     * @return 处理后字符串
     */
    private String removeEndSlash(String value) {
        if (StrUtil.isBlank(value)) {
            return value;
        }
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    /**
     * MD5编码。
     *
     * @param value 字符串
     * @return MD5字符串
     */
    private String md5(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(Math.abs(value.hashCode()));
        }
    }

    /**
     * 复制模板主题数据。
     *
     * @param templateId 模板主题ID
     * @param theme 待保存主题
     */
    private void copyTemplateTheme(Integer templateId, Theme theme) {
        Theme templateTheme = getByIdAndNotDeleted(templateId);
        if (templateTheme == null) {
            throw new CrmebException("主题不存在");
        }
        BeanUtils.copyProperties(templateTheme, theme, "id", "addTime", "upTime");
        theme.setIsUse(0);
    }

    /**
     * 编码主题模块值。
     *
     * @param value 原始模块值
     * @return JSON字符串
     */
    private String encodeThemeValue(Object value) {
        try {
            return JSON.toJSONString(value);
        } catch (Exception e) {
            throw new CrmebException("主题模块数据JSON编码失败");
        }
    }

    /**
     * 写入主题模块数据。
     *
     * @param theme 主题实体
     * @param type 模块类型
     * @param value JSON字符串
     * @param rawValue 原始模块值
     * @param nowTime 当前时间
     * @param themeType 主题类型
     */
    private void applyThemeModuleData(Theme theme, String type, String value, Object rawValue, Integer nowTime, Integer themeType) {
        boolean selfBuiltTheme = themeType == 0;
        switch (type) {
            case "home":
                theme.setHomeData(value);
                theme.setHomeDataUpdateTime(nowTime);
                if (selfBuiltTheme) {
                    theme.setHomeDefaultData(value);
                }
                break;
            case "category":
                String categoryImage = "/crmebimage/theme-cate/cate" + getCategoryImageValue(rawValue) + ".png";
                theme.setCategoryData(value);
                theme.setCategoryDataUpdateTime(nowTime);
                theme.setCategoryImage(categoryImage);
                if (selfBuiltTheme) {
                    theme.setCategoryDefaultData(value);
                    theme.setCategoryDefaultImage(categoryImage);
                }
                break;
            case "detail":
                theme.setDetailData(value);
                theme.setDetailDataUpdateTime(nowTime);
                if (selfBuiltTheme) {
                    theme.setDetailDefaultData(value);
                }
                break;
            case "user":
                theme.setUserData(value);
                theme.setUserDataUpdateTime(nowTime);
                if (selfBuiltTheme) {
                    theme.setUserDefaultData(value);
                }
                break;
            case "theme":
                theme.setThemeData(value);
                theme.setThemeDataUpdateTime(nowTime);
                if (selfBuiltTheme) {
                    theme.setThemeDefaultData(value);
                }
                break;
            default:
                throw new CrmebException("主题模块类型不正确");
        }
    }

    /**
     * 写入主题图片。
     *
     * @param theme 主题实体
     * @param type 图片类型
     * @param image 图片地址
     * @param themeType 主题类型
     */
    private void applyThemeImage(Theme theme, String type, String image, Integer themeType) {
        boolean selfBuiltTheme = themeType == 0;
        switch (type) {
            case "home":
                theme.setHomeImage(image);
                if (selfBuiltTheme) {
                    theme.setHomeDefaultImage(image);
                }
                break;
            case "detail":
                theme.setDetailImage(image);
                if (selfBuiltTheme) {
                    theme.setDetailDefaultImage(image);
                }
                break;
            case "user":
                theme.setUserImage(image);
                if (selfBuiltTheme) {
                    theme.setUserDefaultImage(image);
                }
                break;
            default:
                throw new CrmebException("主题图片类型不正确");
        }
    }

    /**
     * 保存主题图片到public/theme目录。
     *
     * @param image 原图片地址
     * @return 新图片地址
     */
    private String saveThemeImageToPublicTheme(String image) {
        if (StrUtil.isBlank(image)) {
            throw new CrmebException("请上传图片");
        }

        String normalizedImage = removeStartSlash(image.trim());
        if (normalizedImage.startsWith(THEME_SAVE_IMAGE_WEB_PATH)) {
            return normalizedImage;
        }

        String extension = getImageExtension(image, "png");
        String fileName = System.currentTimeMillis() + "_" + md5(image) + "." + extension;
        String webPath = THEME_SAVE_IMAGE_WEB_PATH + CrmebDateUtil.nowDate("yyyy/MM/dd") + "/" + fileName;
        File targetFile = new File(normalizeRootPath(crmebConfig.getImagePath()), webPath);
        if (!copyThemeImage(image, targetFile)) {
            throw new CrmebException("主题图片保存失败");
        }
        return webPath;
    }

    /**
     * 复制源主题指定模块数据到目标主题。
     *
     * @param targetTheme 目标主题更新对象
     * @param sourceTheme 源主题
     * @param sourceThemeId 源主题ID
     * @param type 模块类型
     * @param nowTime 当前时间戳
     */
    private void applyUseThemeData(Theme targetTheme, Theme sourceTheme, Integer sourceThemeId, String type, Integer nowTime) {
        switch (type) {
            case "home":
                targetTheme.setHomeData(sourceTheme.getHomeData());
                targetTheme.setHomeImage(sourceTheme.getHomeImage());
                targetTheme.setHomeDataId(sourceThemeId);
                targetTheme.setHomeDataUpdateTime(nowTime);
                break;
            case "category":
                targetTheme.setCategoryData(sourceTheme.getCategoryData());
                targetTheme.setCategoryImage(sourceTheme.getCategoryImage());
                targetTheme.setCategoryDataId(sourceThemeId);
                targetTheme.setCategoryDataUpdateTime(nowTime);
                break;
            case "detail":
                targetTheme.setDetailData(sourceTheme.getDetailData());
                targetTheme.setDetailImage(sourceTheme.getDetailImage());
                targetTheme.setDetailDataId(sourceThemeId);
                targetTheme.setDetailDataUpdateTime(nowTime);
                break;
            case "user":
                targetTheme.setUserData(sourceTheme.getUserData());
                targetTheme.setUserImage(sourceTheme.getUserImage());
                targetTheme.setUserDataId(sourceThemeId);
                targetTheme.setUserDataUpdateTime(nowTime);
                break;
            case "theme":
                targetTheme.setThemeData(sourceTheme.getThemeData());
                targetTheme.setThemeDataId(sourceThemeId);
                targetTheme.setThemeDataUpdateTime(nowTime);
                break;
            default:
                throw new CrmebException("主题模块类型不正确");
        }
    }

    /**
     * 获取主题默认风格数据。
     *
     * @param theme 主题实体
     * @return 默认风格数据
     */
    private String getDefaultThemeData(Theme theme) {
        String themeDefaultData = theme.getThemeDefaultData();
        if (StrUtil.isBlank(themeDefaultData) || "null".equalsIgnoreCase(themeDefaultData.trim())) {
            return DEFAULT_THEME_DATA;
        }
        return themeDefaultData;
    }

    /**
     * 获取默认字符串。
     *
     * @param value 字符串值
     * @return 非空字符串
     */
    private String getDefaultString(String value) {
        return value == null ? "" : value;
    }

    /**
     * 判断当前主题是否存在混搭来源。
     *
     * @param theme 当前使用主题
     * @return 是否混搭
     */
    private boolean hasSourceThemeId(Theme theme) {
        return (theme.getHomeDataId() != null && theme.getHomeDataId() > 0)
                || (theme.getCategoryDataId() != null && theme.getCategoryDataId() > 0)
                || (theme.getDetailDataId() != null && theme.getDetailDataId() > 0)
                || (theme.getUserDataId() != null && theme.getUserDataId() > 0)
                || (theme.getThemeDataId() != null && theme.getThemeDataId() > 0);
    }

    /**
     * 查询混搭来源主题标题。
     *
     * @param theme 当前使用主题
     * @return 来源主题ID和标题映射
     */
    private Map<Integer, String> getSourceThemeTitleMap(Theme theme) {
        List<Integer> sourceIds = Arrays.asList(
                        theme.getHomeDataId(),
                        theme.getCategoryDataId(),
                        theme.getDetailDataId(),
                        theme.getUserDataId(),
                        theme.getThemeDataId()
                ).stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(sourceIds)) {
            return new HashMap<>();
        }

        LambdaQueryWrapper<Theme> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(Theme::getId, Theme::getTitle);
        queryWrapper.in(Theme::getId, sourceIds);
        queryWrapper.eq(Theme::getIsDel, 0);
        List<Theme> sourceThemes = dao.selectList(queryWrapper);
        if (CollUtil.isEmpty(sourceThemes)) {
            return new HashMap<>();
        }
        return sourceThemes.stream()
                .collect(Collectors.toMap(Theme::getId, item -> item.getTitle() == null ? "" : item.getTitle(), (oldValue, newValue) -> oldValue));
    }

    /**
     * 构建当前使用主题模块信息。
     *
     * @param theme 当前使用主题
     * @param sourceThemeTitleMap 来源主题标题映射
     * @return 模块信息
     */
    private JSONArray buildUsingThemeDataInfo(Theme theme, Map<Integer, String> sourceThemeTitleMap) {
        JSONArray dataInfo = new JSONArray();
        dataInfo.add(buildUsingThemeDataInfoItem("home", theme.getTitle(), theme.getHomeDataId(),
                theme.getHomeImage(), theme.getHomeDataUpdateTime(), sourceThemeTitleMap));
        dataInfo.add(buildUsingThemeDataInfoItem("category", theme.getTitle(), theme.getCategoryDataId(),
                theme.getCategoryImage(), theme.getCategoryDataUpdateTime(), sourceThemeTitleMap));
        dataInfo.add(buildUsingThemeDataInfoItem("detail", theme.getTitle(), theme.getDetailDataId(),
                theme.getDetailImage(), theme.getDetailDataUpdateTime(), sourceThemeTitleMap));
        dataInfo.add(buildUsingThemeDataInfoItem("user", theme.getTitle(), theme.getUserDataId(),
                theme.getUserImage(), theme.getUserDataUpdateTime(), sourceThemeTitleMap));
        return dataInfo;
    }

    /**
     * 构建当前使用主题单个模块信息。
     *
     * @param key 模块标识
     * @param defaultTitle 默认标题
     * @param sourceThemeId 来源主题ID
     * @param image 图片
     * @param updateTime 更新时间
     * @param sourceThemeTitleMap 来源主题标题映射
     * @return 模块信息
     */
    private JSONObject buildUsingThemeDataInfoItem(String key, String defaultTitle, Integer sourceThemeId,
                                                   String image, Integer updateTime, Map<Integer, String> sourceThemeTitleMap) {
        JSONObject item = new JSONObject();
        item.put("key", key);
        item.put("title", getUsingThemeModuleTitle(defaultTitle, sourceThemeId, sourceThemeTitleMap));
        item.put("image", image);
        item.put("update_time", formatTimestamp(updateTime));
        return item;
    }

    /**
     * 获取当前使用主题模块标题。
     *
     * @param defaultTitle 默认标题
     * @param sourceThemeId 来源主题ID
     * @param sourceThemeTitleMap 来源主题标题映射
     * @return 标题
     */
    private String getUsingThemeModuleTitle(String defaultTitle, Integer sourceThemeId, Map<Integer, String> sourceThemeTitleMap) {
        if (sourceThemeId != null && sourceThemeId > 0 && sourceThemeTitleMap.containsKey(sourceThemeId)) {
            return sourceThemeTitleMap.get(sourceThemeId);
        }
        return defaultTitle == null ? "" : defaultTitle;
    }

    /**
     * 解析当前使用主题风格数据。
     *
     * @param data 主题风格JSON
     * @return 主题风格对象
     */
    private Object parseUsingThemeData(String data) {
        String themeData = StrUtil.isBlank(data) || "null".equalsIgnoreCase(data.trim()) ? DEFAULT_THEME_DATA : data;
        Object parsedData = parseJsonData(themeData);
        return parsedData == null ? JSON.parseObject(DEFAULT_THEME_DATA) : parsedData;
    }

    /**
     * 获取分类页图片编号。
     *
     * @param rawValue 原始分类值
     * @return 分类图片编号
     */
    private String getCategoryImageValue(Object rawValue) {
        if (rawValue == null) {
            return "";
        }
        if (rawValue instanceof Number || rawValue instanceof CharSequence || rawValue instanceof Boolean) {
            return String.valueOf(rawValue).trim();
        }
        return JSON.toJSONString(rawValue);
    }

    /**
     * 补齐表默认值，避免请求缺省值破坏数据库非空约束。
     *
     * @param theme 主题实体
     */
    private void initDefaultValue(Theme theme) {
        if (theme.getVersion() == null) {
            theme.setVersion("");
        }
        if (theme.getTitle() == null) {
            theme.setTitle("");
        }
        if (theme.getInfo() == null) {
            theme.setInfo("");
        }
        if (theme.getType() == null) {
            theme.setType(0);
        }
        if (theme.getHomeImage() == null) {
            theme.setHomeImage("");
        }
        if (theme.getHomeDataId() == null) {
            theme.setHomeDataId(0);
        }
        if (theme.getHomeDataUpdateTime() == null) {
            theme.setHomeDataUpdateTime(0);
        }
        if (theme.getCategoryImage() == null) {
            theme.setCategoryImage("");
        }
        if (theme.getCategoryDataId() == null) {
            theme.setCategoryDataId(0);
        }
        if (theme.getCategoryDataUpdateTime() == null) {
            theme.setCategoryDataUpdateTime(0);
        }
        if (theme.getDetailImage() == null) {
            theme.setDetailImage("");
        }
        if (theme.getDetailDataId() == null) {
            theme.setDetailDataId(0);
        }
        if (theme.getDetailDataUpdateTime() == null) {
            theme.setDetailDataUpdateTime(0);
        }
        if (theme.getUserImage() == null) {
            theme.setUserImage("");
        }
        if (theme.getUserDataId() == null) {
            theme.setUserDataId(0);
        }
        if (theme.getUserDataUpdateTime() == null) {
            theme.setUserDataUpdateTime(0);
        }
        if (theme.getThemeDataId() == null) {
            theme.setThemeDataId(0);
        }
        if (theme.getThemeDataUpdateTime() == null) {
            theme.setThemeDataUpdateTime(0);
        }
        if (theme.getHomeDefaultImage() == null) {
            theme.setHomeDefaultImage("");
        }
        if (theme.getCategoryDefaultImage() == null) {
            theme.setCategoryDefaultImage("");
        }
        if (theme.getDetailDefaultImage() == null) {
            theme.setDetailDefaultImage("");
        }
        if (theme.getUserDefaultImage() == null) {
            theme.setUserDefaultImage("");
        }
        if (StrUtil.isBlank(theme.getPageType())) {
            theme.setPageType("theme");
        }
        if (theme.getIsUse() == null) {
            theme.setIsUse(0);
        }
        theme.setIsDel(0);
    }

    /**
     * 构建主题默认详情数据。
     *
     * @param theme 主题实体
     * @return 默认详情数据
     */
    private JSONObject buildDefaultInfoData(Theme theme) {
        JSONObject data = new JSONObject();
        data.put("id", theme.getId());
        data.put("showUrl", buildThemeShowUrl(theme.getId()));
        data.put("version", theme.getVersion());
        data.put("title", theme.getTitle());
        data.put("info", theme.getInfo());
        data.put("type", theme.getType());
        data.put("home_data", theme.getHomeData());
        data.put("home_image", theme.getHomeImage());
        data.put("home_data_id", theme.getHomeDataId());
        data.put("home_data_id_title", getThemeTitleById(theme.getHomeDataId()));
        data.put("home_data_update_time", formatTimestamp(theme.getHomeDataUpdateTime()));
        data.put("category_data", theme.getCategoryData());
        data.put("category_image", theme.getCategoryImage());
        data.put("category_data_id", theme.getCategoryDataId());
        data.put("category_data_id_title", getThemeTitleById(theme.getCategoryDataId()));
        data.put("category_data_update_time", formatTimestamp(theme.getCategoryDataUpdateTime()));
        data.put("detail_data", theme.getDetailData());
        data.put("detail_image", theme.getDetailImage());
        data.put("detail_data_id", theme.getDetailDataId());
        data.put("detail_data_id_title", getThemeTitleById(theme.getDetailDataId()));
        data.put("detail_data_update_time", formatTimestamp(theme.getDetailDataUpdateTime()));
        data.put("user_data", theme.getUserData());
        data.put("user_image", theme.getUserImage());
        data.put("user_data_id", theme.getUserDataId());
        data.put("user_data_id_title", getThemeTitleById(theme.getUserDataId()));
        data.put("user_data_update_time", formatTimestamp(theme.getUserDataUpdateTime()));
        data.put("theme_data", theme.getThemeData());
        data.put("theme_data_id", theme.getThemeDataId());
        data.put("theme_data_id_title", getThemeTitleById(theme.getThemeDataId()));
        data.put("theme_data_update_time", formatTimestamp(theme.getThemeDataUpdateTime()));
        data.put("home_default_data", theme.getHomeDefaultData());
        data.put("home_default_image", theme.getHomeDefaultImage());
        data.put("category_default_data", theme.getCategoryDefaultData());
        data.put("category_default_image", theme.getCategoryDefaultImage());
        data.put("detail_default_data", theme.getDetailDefaultData());
        data.put("detail_default_image", theme.getDetailDefaultImage());
        data.put("user_default_data", theme.getUserDefaultData());
        data.put("user_default_image", theme.getUserDefaultImage());
        data.put("theme_default_data", theme.getThemeDefaultData());
        data.put("page_type", theme.getPageType());
        data.put("is_use", theme.getIsUse());
        data.put("is_del", theme.getIsDel());
        data.put("add_time", formatTimestamp(theme.getAddTime()));
        data.put("up_time", formatTimestamp(theme.getUpTime()));
        return data;
    }

    /**
     * 根据主题ID查询主题名称。
     *
     * @param id 主题ID
     * @return 主题名称
     */
    private String getThemeTitleById(Integer id) {
        if (id == null || id <= 0) {
            return "";
        }
        LambdaQueryWrapper<Theme> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(Theme::getTitle);
        queryWrapper.eq(Theme::getId, id);
        queryWrapper.last(" limit 1");
        Theme theme = dao.selectOne(queryWrapper);
        if (theme == null || theme.getTitle() == null) {
            return "";
        }
        return theme.getTitle();
    }

    /**
     * 格式化秒级时间戳。
     *
     * @param timestamp 秒级时间戳
     * @return 日期字符串
     */
    private String formatTimestamp(Integer timestamp) {
        if (timestamp == null) {
            return null;
        }
        return CrmebDateUtil.dateToStr(CrmebDateUtil.timeStamp11ToDate(timestamp), DateConstants.DATE_FORMAT);
    }

    /**
     * 生成主题版本号。
     *
     * @return 主题版本号
     */
    private String generateThemeVersion() {
        return CrmebDateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM);
    }

    /**
     * 构建主题文本字段项。
     *
     * @param label 展示名称
     * @param value 字段标识
     * @return 字段项
     */
    private ThemeTextFieldItemResponse textFieldItem(String label, String value) {
        return new ThemeTextFieldItemResponse(label, value);
    }

    /**
     * 构建主题列表响应对象。
     *
     * @param theme 主题实体
     * @return 列表响应对象
     */
    private ThemeResponse buildThemeResponse(Theme theme, Map<String, String> imageDomainMap) {
        ThemeResponse response = new ThemeResponse();
        BeanUtils.copyProperties(theme, response);
        response.setShowUrl(buildThemeShowUrl(theme.getId()));
        response.setType(resolveThemeTypeName(theme.getType()));
        response.setHomeImage(resolveThemeResponseImage(theme.getHomeImage(), imageDomainMap));
        response.setCategoryImage(resolveThemeResponseImage(theme.getCategoryImage(), imageDomainMap));
        response.setDetailImage(resolveThemeResponseImage(theme.getDetailImage(), imageDomainMap));
        response.setUserImage(resolveThemeResponseImage(theme.getUserImage(), imageDomainMap));
        response.setHomeDefaultImage(resolveThemeResponseImage(theme.getHomeDefaultImage(), imageDomainMap));
        response.setCategoryDefaultImage(resolveThemeResponseImage(theme.getCategoryDefaultImage(), imageDomainMap));
        response.setDetailDefaultImage(resolveThemeResponseImage(theme.getDetailDefaultImage(), imageDomainMap));
        response.setUserDefaultImage(resolveThemeResponseImage(theme.getUserDefaultImage(), imageDomainMap));
        response.setHomeDataUpdateTime(CrmebDateUtil.timeStamp11ToDate(theme.getHomeDataUpdateTime()));
        response.setCategoryDataUpdateTime(CrmebDateUtil.timeStamp11ToDate(theme.getCategoryDataUpdateTime()));
        response.setDetailDataUpdateTime(CrmebDateUtil.timeStamp11ToDate(theme.getDetailDataUpdateTime()));
        response.setUserDataUpdateTime(CrmebDateUtil.timeStamp11ToDate(theme.getUserDataUpdateTime()));
        response.setThemeDataUpdateTime(CrmebDateUtil.timeStamp11ToDate(theme.getThemeDataUpdateTime()));
        response.setAddTime(CrmebDateUtil.timeStamp11ToDate(theme.getAddTime()));
        response.setUpTime(CrmebDateUtil.timeStamp11ToDate(theme.getUpTime()));
        return response;
    }

    /**
     * 处理主题响应中的单张图片。
     */
    private String resolveThemeResponseImage(String image, Map<String, String> imageDomainMap) {
        if (StrUtil.isBlank(image)) {
            return "";
        }
        String resolvedImage = resolveThemeImageUrl(image, imageDomainMap);
        return resolvedImage == null ? image.trim() : resolvedImage;
    }

    /**
     * 追加主题预览地址并处理其中所有图片域名。
     */
    private JSONObject buildThemeInfoResponse(JSONObject data, Theme theme) {
        JSONObject response = appendThemeShowUrl(data, theme);
        appendImageDomainPrefix(response);
        return response;
    }

    /**
     * 追加主题预览地址。
     *
     * @param data 响应数据
     * @param theme 主题实体
     * @return 响应数据
     */
    private JSONObject appendThemeShowUrl(JSONObject data, Theme theme) {
        data.put("showUrl", buildThemeShowUrl(theme.getId()));
        return data;
    }

    /**
     * 构建主题预览地址。
     *
     * @param themeId 主题ID
     * @return 主题预览地址
     */
    private String buildThemeShowUrl(Integer themeId) {
        String siteUrl = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SITE_URL);
        if (themeId == null || StrUtil.isBlank(siteUrl)) {
            return "";
        }
        return removeEndSlash(siteUrl.trim()) + "/pages/index/index?theme_id=" + themeId;
    }

    /**
     * 解析主题类型名称。
     *
     * @param type 主题类型
     * @return 类型名称
     */
    private String resolveThemeTypeName(Integer type) {
        if (type == null) {
            return "";
        }
        if (type == 1) {
            return "广场主题";
        }
        return "自建主题";
    }
}
