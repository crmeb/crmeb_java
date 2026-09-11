package com.zbkj.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.CartNumRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.theme.ThemeArticleListResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.StoreCartService;
import com.zbkj.service.service.StoreOrderService;
import com.zbkj.service.service.StoreProductRelationService;
import com.zbkj.service.service.ThemeService;
import com.zbkj.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 移动端主题信息
 */
@RestController
@RequestMapping("api/front")
@Api(tags = "主题信息")
public class ThemeInfoController {

    @Autowired
    private ThemeService themeService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private StoreCartService storeCartService;

    @Autowired
    private StoreOrderService storeOrderService;

    @ApiOperation(value = "主题详情")
    @RequestMapping(value = "/theme_info/{type}", method = RequestMethod.GET)
    public CommonResult<JSONObject> themeInfo(@PathVariable(value = "type") String type,
                                              @RequestParam(value = "theme_id", required = false, defaultValue = "0") Integer themeId) {
        return CommonResult.success(themeService.getThemeInfo(themeId, type));
    }

    @ApiOperation(value = "主题用户信息")
    @RequestMapping(value = "/theme/user", method = RequestMethod.GET)
    public CommonResult<JSONObject> themeUser() {
        User user = userService.getInfoException();

        CartNumRequest cartNumRequest = new CartNumRequest();
        cartNumRequest.setType("total");
        cartNumRequest.setNumType(true);

        JSONObject result = new JSONObject();
        result.put("nickname", user.getNickname());
        result.put("uid", user.getUid());
        result.put("image", user.getAvatar());
        result.put("collection_num", storeProductRelationService.getCollectCountByUid(user.getUid()));
        result.put("cart_num", storeCartService.getUserCount(cartNumRequest).get("count"));
        result.put("order_num", storeOrderService.getOrderCountByUid(user.getUid()));
        result.put("integral", user.getIntegral());
        result.put("now_money", user.getNowMoney());
        result.put("brokerage_price", user.getBrokeragePrice());
        result.put("unread_msg_num", 0);
        return CommonResult.success(result);
    }

    @ApiOperation(value = "主题商品列表")
    @RequestMapping(value = "/theme/product", method = RequestMethod.GET)
    public CommonResult<List<JSONObject>> themeProduct(@RequestParam(value = "ids", required = false, defaultValue = "") String ids,
                                                       @RequestParam(value = "cate_ids", required = false, defaultValue = "") String cateIds,
                                                       @RequestParam(value = "order", required = false, defaultValue = "0") Integer order,
                                                       @RequestParam(value = "sort", required = false, defaultValue = "0") Integer sort,
                                                       @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        return CommonResult.success(themeService.getThemeProduct(ids, cateIds, order, sort, limit));
    }

    @ApiOperation(value = "主题优惠券列表")
    @RequestMapping(value = "/theme/coupon", method = RequestMethod.GET)
    public CommonResult<JSONObject> themeCoupon(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        return CommonResult.success(themeService.getThemeCoupon(limit));
    }

    @ApiOperation(value = "主题秒杀商品列表")
    @RequestMapping(value = "/theme/seckill", method = RequestMethod.GET)
    public CommonResult<List<JSONObject>> themeSeckill(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        return CommonResult.success(themeService.getThemeSeckill(limit));
    }

    @ApiOperation(value = "主题拼团商品列表")
    @RequestMapping(value = "/theme/combination", method = RequestMethod.GET)
    public CommonResult<List<JSONObject>> themeCombination(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        return CommonResult.success(themeService.getThemeCombination(limit));
    }

    @ApiOperation(value = "主题砍价商品列表")
    @RequestMapping(value = "/theme/bargain", method = RequestMethod.GET)
    public CommonResult<List<JSONObject>> themeBargain(@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        return CommonResult.success(themeService.getThemeBargain(limit));
    }

    @ApiOperation(value = "主题文章列表")
    @RequestMapping(value = "/theme/article", method = RequestMethod.GET)
    public CommonResult<ThemeArticleListResponse> themeArticle(@RequestParam(value = "cid", required = false, defaultValue = "") String cid,
                                                               @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(themeService.getArticleList(cid, pageParamRequest));
    }
}
