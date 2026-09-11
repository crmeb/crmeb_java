package com.zbkj.front.controller.admin;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.coupon.StoreCouponUser;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserGroup;
import com.zbkj.common.model.user.UserTag;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.StoreCouponUserRequest;
import com.zbkj.common.request.UpdateUserLevelRequest;
import com.zbkj.common.request.UserOperateIntegralMoneyRequest;
import com.zbkj.common.request.UserSearchRequest;
import com.zbkj.common.request.UserUpdateRequest;
import com.zbkj.common.response.TopDetail;
import com.zbkj.common.response.UserResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.front.service.MobileAdminPermissionService;
import com.zbkj.service.service.SystemUserLevelService;
import com.zbkj.service.service.StoreCouponUserService;
import com.zbkj.service.service.UserGroupService;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.UserTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 移动端商家用户管理
 */
@RestController
@RequestMapping("api/front/manage/user")
@Api(tags = "移动端商家管理-用户")
public class MobileAdminUserController {

    @Autowired
    private MobileAdminPermissionService mobileAdminPermissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private UserTagService userTagService;

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @ApiOperation(value = "用户列表")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserResponse>> getList(UserSearchRequest request,
                                                          @RequestParam Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        normalizeUserSearchRequest(request, params);
        return CommonResult.success(CommonPage.restPage(userService.getList(request)));
    }

    @ApiOperation(value = "用户详情")
    @RequestMapping(value = "/info/{uid}", method = RequestMethod.GET)
    public CommonResult<User> info(@PathVariable Integer uid) {
        mobileAdminPermissionService.requireEnabledStaff();
        return CommonResult.success(userService.getInfoByUid(uid));
    }

    @ApiOperation(value = "用户详情Top数据")
    @RequestMapping(value = "/topdetail", method = RequestMethod.GET)
    public CommonResult<TopDetail> topDetail(@RequestParam Integer userId) {
        mobileAdminPermissionService.requireEnabledStaff();
        return CommonResult.success(userService.getTopDetail(userId));
    }

    @ApiOperation(value = "用户详情明细")
    @RequestMapping(value = "/infobycondition", method = RequestMethod.GET)
    public CommonResult<CommonPage<T>> infoByCondition(@RequestParam Integer userId,
                                                       @RequestParam Integer type,
                                                       PageParamRequest pageParamRequest) {
        mobileAdminPermissionService.requireEnabledStaff();
        return CommonResult.success(CommonPage.restPage((List<T>) userService.getInfoByCondition(userId, type, pageParamRequest)));
    }

    @ApiOperation(value = "用户分组列表")
    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public CommonResult<List<UserGroup>> group() {
        mobileAdminPermissionService.requireEnabledStaff();
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(1);
        pageParamRequest.setLimit(999);
        return CommonResult.success(userGroupService.getList(pageParamRequest));
    }

    @ApiOperation(value = "用户标签列表")
    @RequestMapping(value = "/label/{uid}", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> label(@PathVariable Integer uid) {
        mobileAdminPermissionService.requireEnabledStaff();
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(1);
        pageParamRequest.setLimit(999);
        User user = null;
        Set<Integer> selectedTagIds = new HashSet<>();
        if (uid != null && uid > 0) {
            user = userService.getInfoByUid(uid);
            if (user != null && StrUtil.isNotBlank(user.getTagId())) {
                selectedTagIds.addAll(CrmebUtil.stringToArray(user.getTagId()));
            }
        }
        List<Map<String, Object>> tagList = new ArrayList<>();
        for (UserTag userTag : userTagService.getList(pageParamRequest)) {
            Map<String, Object> tag = new HashMap<>();
            tag.put("id", userTag.getId());
            tag.put("name", userTag.getName());
            tag.put("checked", selectedTagIds.contains(userTag.getId()));
            tag.put("disabled", selectedTagIds.contains(userTag.getId()));
            tagList.add(tag);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("list", tagList);
        return CommonResult.success(data);
    }

    @ApiOperation(value = "用户等级列表")
    @RequestMapping(value = "/level", method = RequestMethod.GET)
    public CommonResult<List<SystemUserLevel>> level() {
        mobileAdminPermissionService.requireEnabledStaff();
        return CommonResult.success(systemUserLevelService.getUsableList());
    }

    @ApiOperation(value = "用户优惠券")
    @RequestMapping(value = "/coupon", method = RequestMethod.GET)
    public CommonResult<List<StoreCouponUser>> coupon(@RequestParam Integer uid, PageParamRequest pageParamRequest) {
        mobileAdminPermissionService.requireEnabledStaff();
        return CommonResult.success(storeCouponUserService.findListByUid(uid, pageParamRequest));
    }

    @ApiOperation(value = "移动端修改用户资产/等级/分组/标签/优惠券")
    @RequestMapping(value = "/update_other/{uid}", method = RequestMethod.POST)
    public CommonResult<String> updateOther(@PathVariable String uid,
                                            @RequestBody(required = false) Map<String, Object> request) {
        mobileAdminPermissionService.requireEnabledStaff();
        Map<String, Object> body = request == null ? new HashMap<>() : request;
        String uidValue = getTargetUid(uid, body);

        if (containsAny(body, "coupon_id", "couponId")) {
            StoreCouponUserRequest couponRequest = new StoreCouponUserRequest();
            couponRequest.setUid(uidValue);
            couponRequest.setCouponId(getRequiredInteger(body, "coupon_id", "couponId"));
            return result(storeCouponUserService.receive(couponRequest));
        }

        if (containsAny(body, "label_id", "labelId", "tag_id", "tagId")) {
            String tagIdValue = getIdString(getFirstValue(body, "label_id", "labelId", "tag_id", "tagId"));
            return result(updateUserTag(uidValue, tagIdValue));
        }

        if (containsAny(body, "group_id", "groupId")) {
            String groupIdValue = getIdString(getFirstValue(body, "group_id", "groupId"));
            return result(userService.group(uidValue, groupIdValue));
        }

        if (containsAny(body, "level", "level_id", "levelId")) {
            UpdateUserLevelRequest levelRequest = new UpdateUserLevelRequest();
            levelRequest.setUid(getSingleUid(uidValue, "用户等级只能单个用户修改"));
            levelRequest.setLevelId(getRequiredInteger(body, "level", "level_id", "levelId"));
            levelRequest.setIsSub(getBoolean(body, false, "is_sub", "isSub"));
            return result(userService.updateUserLevel(levelRequest));
        }

        Integer type = getInteger(body, "type");
        if (type != null && (type.equals(0) || type.equals(1))) {
            return result(updateUserIntegralMoney(uidValue, body, type));
        }
        if (type != null && type.equals(3)) {
            throw new CrmebException("当前项目未配置会员赠送功能");
        }
        throw new CrmebException("请选择要修改的用户数据");
    }

    @ApiOperation(value = "移动端修改用户资料")
    @RequestMapping(value = "/update/{uid}", method = RequestMethod.POST)
    public CommonResult<String> update(@PathVariable Integer uid,
                                       @RequestBody(required = false) Map<String, Object> request) {
        mobileAdminPermissionService.requireEnabledStaff();
        Map<String, Object> body = request == null ? new HashMap<>() : request;
        User user = userService.getInfoByUid(uid);
        UserUpdateRequest userRequest = new UserUpdateRequest();
        userRequest.setUid(uid);
        userRequest.setRealName(getString(body, "real_name", "realName"));
        userRequest.setBirthday(getString(body, "birthday"));
        userRequest.setMark(getString(body, "mark"));
        userRequest.setAddres(getString(body, "addres", "address"));
        userRequest.setGroupId(getIdString(getFirstValue(body, "group_id", "groupId")));
        userRequest.setTagId(getIdString(getFirstValue(body, "label_id", "labelId", "tag_id", "tagId")));
        userRequest.setStatus(getBoolean(body, Boolean.TRUE.equals(user.getStatus()), "status"));
        userRequest.setIsPromoter(getBoolean(body, Boolean.TRUE.equals(user.getIsPromoter()), "is_promoter", "isPromoter"));
        return result(userService.updateUser(userRequest));
    }

    private void normalizeUserSearchRequest(UserSearchRequest request, Map<String, Object> params) {
        String keywords = getFirstString(params, "keywords", "keyword", "content", "nickname");
        if (StrUtil.isNotBlank(keywords)) {
            request.setSearchType("all");
            request.setContent(keywords);
        }
        request.setGroupId(getFilterString(params, "groupId", "group_id"));
        request.setLabelId(getFilterString(params, "labelId", "label_id"));
        request.setLevel(getFilterString(params, "level"));
        String dateLimit = getFirstString(params, "dateLimit", "date", "data");
        if (StrUtil.isNotBlank(dateLimit)) {
            request.setDateLimit(dateLimit);
        }
    }

    private String getFilterString(Map<String, Object> params, String... keys) {
        String value = getFirstString(params, keys);
        return "0".equals(value) ? "" : value;
    }

    private String getFirstString(Map<String, Object> params, String... keys) {
        for (String key : keys) {
            Object value = params.get(key);
            if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
                return String.valueOf(value);
            }
        }
        return "";
    }

    private CommonResult<String> result(Boolean result) {
        return Boolean.TRUE.equals(result) ? CommonResult.success() : CommonResult.failed();
    }

    private Boolean updateUserIntegralMoney(String uidValue, Map<String, Object> body, Integer type) {
        UserOperateIntegralMoneyRequest request = new UserOperateIntegralMoneyRequest();
        request.setUid(getSingleUid(uidValue, "积分余额只能单个用户修改"));
        Integer status = getInteger(body, "status");
        if (status == null || (status != 1 && status != 2)) {
            throw new CrmebException("请选择正确的操作类型");
        }
        BigDecimal number = getBigDecimal(body, "number");
        if (number == null) {
            throw new CrmebException("请输入修改值");
        }
        if (type.equals(0)) {
            request.setMoneyType(status);
            request.setMoneyValue(number);
            request.setIntegralType(1);
            request.setIntegralValue(0);
        } else {
            if (number.stripTrailingZeros().scale() > 0) {
                throw new CrmebException("积分必须为整数");
            }
            request.setMoneyType(1);
            request.setMoneyValue(BigDecimal.ZERO);
            request.setIntegralType(status);
            request.setIntegralValue(number.intValue());
        }
        return userService.updateIntegralMoney(request);
    }

    private Boolean updateUserTag(String uidValue, String tagIdValue) {
        List<Integer> idList = CrmebUtil.stringToArray(uidValue).stream().distinct().collect(Collectors.toList());
        if (idList.isEmpty()) {
            throw new CrmebException("会员编号不能为空");
        }
        List<User> userList = userService.listByIds(idList);
        if (userList == null || userList.size() < idList.size()) {
            throw new CrmebException("没有找到用户信息");
        }
        userList.forEach(user -> {
            user.setTagId(tagIdValue);
            user.setUpdateTime(DateUtil.date());
        });
        return userService.updateBatchById(userList);
    }

    private String getTargetUid(String pathUid, Map<String, Object> body) {
        String uidValue = containsAny(body, "uid") ? getIdString(getFirstValue(body, "uid")) : "";
        if (StrUtil.isBlank(uidValue) || "0".equals(uidValue)) {
            uidValue = getIdString(pathUid);
        }
        if (StrUtil.isBlank(uidValue) || "0".equals(uidValue)) {
            throw new CrmebException("会员编号不能为空");
        }
        return uidValue;
    }

    private Integer getSingleUid(String uidValue, String message) {
        List<Integer> uidList = CrmebUtil.stringToArray(uidValue);
        if (uidList.size() != 1) {
            throw new CrmebException(message);
        }
        return uidList.get(0);
    }

    private boolean containsAny(Map<String, Object> body, String... keys) {
        for (String key : keys) {
            if (body.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    private Object getFirstValue(Map<String, Object> body, String... keys) {
        for (String key : keys) {
            if (body.containsKey(key)) {
                return body.get(key);
            }
        }
        return null;
    }

    private Integer getRequiredInteger(Map<String, Object> body, String... keys) {
        Integer value = getInteger(body, keys);
        if (value == null) {
            throw new CrmebException("参数不能为空");
        }
        return value;
    }

    private Integer getInteger(Map<String, Object> body, String... keys) {
        Object value = getFirstValue(body, keys);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        String stringValue = getScalarString(value);
        return StrUtil.isBlank(stringValue) ? null : Integer.valueOf(stringValue);
    }

    private BigDecimal getBigDecimal(Map<String, Object> body, String... keys) {
        Object value = getFirstValue(body, keys);
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(String.valueOf(value));
        }
        String stringValue = getScalarString(value);
        return StrUtil.isBlank(stringValue) ? null : new BigDecimal(stringValue);
    }

    private Boolean getBoolean(Map<String, Object> body, Boolean defaultValue, String... keys) {
        Object value = getFirstValue(body, keys);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }
        String stringValue = getScalarString(value);
        if (StrUtil.isBlank(stringValue)) {
            return defaultValue;
        }
        return "1".equals(stringValue) || "true".equalsIgnoreCase(stringValue);
    }

    private String getString(Map<String, Object> body, String... keys) {
        return getScalarString(getFirstValue(body, keys));
    }

    private String getIdString(Object value) {
        if (value instanceof Iterable) {
            List<String> values = new ArrayList<>();
            for (Object item : (Iterable<?>) value) {
                String stringValue = getScalarString(item);
                if (StrUtil.isNotBlank(stringValue)) {
                    values.add(stringValue);
                }
            }
            return String.join(",", values);
        }
        return getScalarString(value);
    }

    private String getScalarString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Iterable) {
            for (Object item : (Iterable<?>) value) {
                return getScalarString(item);
            }
            return "";
        }
        String stringValue = String.valueOf(value).trim();
        if ("null".equalsIgnoreCase(stringValue) || "undefined".equalsIgnoreCase(stringValue)) {
            return "";
        }
        return stringValue;
    }
}
