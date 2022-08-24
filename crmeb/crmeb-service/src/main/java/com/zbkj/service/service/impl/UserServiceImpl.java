package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.coupon.StoreCoupon;
import com.zbkj.common.model.coupon.StoreCouponUser;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.record.UserVisitRecord;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.*;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.token.FrontTokenComponent;
import com.zbkj.common.utils.CommonUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.dateLimitUtilVo;
import com.zbkj.service.dao.UserDao;
import com.zbkj.service.service.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户表 服务实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserDao userDao;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FrontTokenComponent tokenComponet;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private UserTagService userTagService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserSignService userSignService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private UserExperienceRecordService experienceRecordService;

    @Autowired
    private UserVisitRecordService userVisitRecordService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 分页显示用户表
     *
     * @param request          搜索条件
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<UserResponse> getList(UserSearchRequest request, PageParamRequest pageParamRequest) {
        Page<User> pageUser = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Map<String, Object> map = CollUtil.newHashMap();

        if (request.getIsPromoter() != null) {
            map.put("isPromoter", request.getIsPromoter() ? 1 : 0);
        }

        if (!StringUtils.isBlank(request.getGroupId())) {
            map.put("groupId", request.getGroupId());
        }

        if (!StringUtils.isBlank(request.getLabelId())) {
            String tagIdSql = CrmebUtil.getFindInSetSql("u.tag_id", request.getLabelId());
            map.put("tagIdSql", tagIdSql);
        }

        if (!StringUtils.isBlank(request.getLevel())) {
            map.put("level", request.getLevel());
        }

        if (StringUtils.isNotBlank(request.getUserType())) {
            map.put("userType", request.getUserType());
        }

        if (StringUtils.isNotBlank(request.getSex())) {
            lambdaQueryWrapper.eq(User::getSex, request.getSex());
            map.put("sex", Integer.valueOf(request.getSex()));
        }

        if (StringUtils.isNotBlank(request.getCountry())) {
            map.put("country", request.getCountry());
            // 根据省市查询
            if (StrUtil.isNotBlank(request.getCity())) {
                request.setProvince(request.getProvince().replace("省", ""));
                request.setCity(request.getCity().replace("市", ""));
                map.put("addres", request.getProvince() + "," + request.getCity());
            }
        }

        if (StrUtil.isNotBlank(request.getPayCount())) {
            map.put("payCount", Integer.valueOf(request.getPayCount()));
        }

        if (request.getStatus() != null) {
            map.put("status", request.getStatus() ? 1 : 0);
        }

        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());

        if (!StringUtils.isBlank(dateLimit.getStartTime())) {
            map.put("startTime", dateLimit.getStartTime());
            map.put("endTime", dateLimit.getEndTime());
            map.put("accessType", request.getAccessType());
        }
        if (request.getKeywords() != null) {
            map.put("keywords", request.getKeywords());
        }
        List<User> userList = userDao.findAdminList(map);
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : userList) {
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);
            // 获取分组信息
            if (!StringUtils.isBlank(user.getGroupId())) {
                userResponse.setGroupName(userGroupService.getGroupNameInId(user.getGroupId()));
                userResponse.setGroupId(user.getGroupId());
            }

            // 获取标签信息
            if (!StringUtils.isBlank(user.getTagId())) {
                userResponse.setTagName(userTagService.getGroupNameInId(user.getTagId()));
                userResponse.setTagId(user.getTagId());
            }

            //获取推广人信息
            if (null == user.getSpreadUid() || user.getSpreadUid() == 0) {
                userResponse.setSpreadNickname("无");
            } else {
                userResponse.setSpreadNickname(userDao.selectById(user.getSpreadUid()).getNickname());
            }
            userResponse.setPhone(CrmebUtil.maskMobile(userResponse.getPhone()));
            userResponses.add(userResponse);
        }
        return CommonPage.copyPageInfo(pageUser, userResponses);
    }

    /**
     * 操作积分、余额
     */
    @Override
    public Boolean updateIntegralMoney(UserOperateIntegralMoneyRequest request) {
        if (ObjectUtil.isNull(request.getMoneyValue()) || ObjectUtil.isNull(request.getIntegralValue())) {
            throw new CrmebException("至少输入一个金额");
        }
        if (request.getMoneyValue().compareTo(BigDecimal.ZERO) < 1 && request.getIntegralValue() <= 0) {
            throw new CrmebException("修改值不能等小于等于0");
        }

        User user = getById(request.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }
        // 减少时要判断小于0的情况,添加时判断是否超过数据限制
        if (request.getMoneyType().equals(2) && request.getMoneyValue().compareTo(BigDecimal.ZERO) != 0) {
            if (user.getNowMoney().subtract(request.getMoneyValue()).compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("余额扣减后不能小于0");
            }
        }
        if (request.getMoneyType().equals(1) && request.getMoneyValue().compareTo(BigDecimal.ZERO) != 0) {
            if (user.getNowMoney().add(request.getMoneyValue()).compareTo(new BigDecimal("99999999.99")) > 0) {
                throw new CrmebException("余额添加后后不能大于99999999.99");
            }
        }

        if (request.getIntegralType().equals(2) && request.getIntegralValue() != 0) {
            if (user.getIntegral() - request.getIntegralValue() < 0) {
                throw new CrmebException("积分扣减后不能小于0");
            }
        }
        if (request.getIntegralType().equals(1) && request.getIntegralValue() != 0) {
            if ((user.getIntegral() + request.getIntegralValue()) > 99999999) {
                throw new CrmebException("积分添加后不能大于99999999");
            }
        }

        Boolean execute = transactionTemplate.execute(e -> {
            // 处理余额
            if (request.getMoneyValue().compareTo(BigDecimal.ZERO) > 0) {
                // 生成UserBill
                UserBill userBill = new UserBill();
                userBill.setUid(user.getUid());
                userBill.setLinkId("0");
                userBill.setTitle("后台操作");
                userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
                userBill.setNumber(request.getMoneyValue());
                userBill.setStatus(1);
                userBill.setCreateTime(DateUtil.nowDateTime());

                if (request.getMoneyType() == 1) {// 增加
                    userBill.setPm(1);
                    userBill.setType(Constants.USER_BILL_TYPE_SYSTEM_ADD);
                    userBill.setBalance(user.getNowMoney().add(request.getMoneyValue()));
                    userBill.setMark(StrUtil.format("后台操作增加了{}余额", request.getMoneyValue()));

                    userBillService.save(userBill);
                    operationNowMoney(user.getUid(), request.getMoneyValue(), user.getNowMoney(), "add");
                } else {
                    userBill.setPm(0);
                    userBill.setType(Constants.USER_BILL_TYPE_SYSTEM_SUB);
                    userBill.setBalance(user.getNowMoney().subtract(request.getMoneyValue()));
                    userBill.setMark(StrUtil.format("后台操作减少了{}余额", request.getMoneyValue()));

                    userBillService.save(userBill);
                    operationNowMoney(user.getUid(), request.getMoneyValue(), user.getNowMoney(), "sub");
                }
            }

            // 处理积分
            if (request.getIntegralValue() > 0) {
                // 生成记录
                UserIntegralRecord integralRecord = new UserIntegralRecord();
                integralRecord.setUid(user.getUid());
                integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SIGN);
                integralRecord.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_SYSTEM);
                integralRecord.setIntegral(request.getIntegralValue());
                integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
                if (request.getIntegralType() == 1) {// 增加
                    integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                    integralRecord.setBalance(user.getIntegral() + request.getIntegralValue());
                    integralRecord.setMark(StrUtil.format("后台操作增加了{}积分", request.getIntegralValue()));

                    operationIntegral(user.getUid(), request.getIntegralValue(), user.getIntegral(), "add");
                } else {
                    integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
                    integralRecord.setBalance(user.getIntegral() - request.getIntegralValue());
                    integralRecord.setMark(StrUtil.format("后台操作减少了{}积分", request.getIntegralValue()));
                    operationIntegral(user.getUid(), request.getIntegralValue(), user.getIntegral(), "sub");
                }
                userIntegralRecordService.save(integralRecord);
            }
            return Boolean.TRUE;
        });

        if (!execute) {
            throw new CrmebException("修改积分/余额失败");
        }
        return execute;
    }

    /**
     * 更新用户金额
     *
     * @param user  用户
     * @param price 金额
     * @param type  增加add、扣减sub
     * @return 更新后的用户对象
     */
    @Override
    public Boolean updateNowMoney(User user, BigDecimal price, String type) {
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        if (type.equals("add")) {
            lambdaUpdateWrapper.set(User::getNowMoney, user.getNowMoney().add(price));
        } else {
            lambdaUpdateWrapper.set(User::getNowMoney, user.getNowMoney().subtract(price));
        }
        lambdaUpdateWrapper.eq(User::getUid, user.getUid());
        if (type.equals("sub")) {
            lambdaUpdateWrapper.apply(StrUtil.format(" now_money - {} >= 0", price));
        }
        return update(lambdaUpdateWrapper);
    }

    /**
     * 会员分组
     *
     * @param id           String id
     * @param groupIdValue Integer 分组Id
     */
    @Override
    public Boolean group(String id, String groupIdValue) {
        if (StrUtil.isBlank(id)) throw new CrmebException("会员编号不能为空");
        if (StrUtil.isBlank(groupIdValue)) throw new CrmebException("分组id不能为空");

        //循环id处理
        List<Integer> idList = CrmebUtil.stringToArray(id);
        idList = idList.stream().distinct().collect(Collectors.toList());
        List<User> list = getListInUid(idList);
        if (CollUtil.isEmpty(list)) throw new CrmebException("没有找到用户信息");
        if (list.size() < idList.size()) {
            throw new CrmebException("没有找到用户信息");
        }
        for (User user : list) {
            user.setGroupId(groupIdValue);
        }
        return updateBatchById(list);
    }

    /**
     * 用户id in list
     *
     * @param uidList List<Integer> id
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    private List<User> getListInUid(List<Integer> uidList) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(User::getUid, uidList);
        return userDao.selectList(lambdaQueryWrapper);
    }

    /**
     * 修改密码
     *
     * @param request PasswordRequest 密码
     * @return boolean
     */
    @Override
    public Boolean password(PasswordRequest request) {
        //检测验证码
        checkValidateCode(request.getPhone(), request.getValidateCode());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getAccount, request.getPhone());
        User user = userDao.selectOne(lambdaQueryWrapper);

        //密码
        user.setPwd(CrmebUtil.encryptPassword(request.getPassword(), user.getAccount()));
        return update(user, lambdaQueryWrapper);
    }

    /**
     * 获取个人资料
     *
     * @return User
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public User getInfo() {
        if (getUserId() == 0) {
            return null;
        }
        return getById(getUserId());
    }

    /**
     * 获取个人资料
     *
     * @return User
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public User getInfoException() {
        User user = getInfo();
        if (user == null) {
            throw new CrmebException("用户信息不存在！");
        }

        if (!user.getStatus()) {
            throw new CrmebException("用户已经被禁用！");
        }
        return user;
    }

    /**
     * 获取当前用户id
     *
     * @return Integer
     */
    @Override
    public Integer getUserIdException() {
        Integer id = tokenComponet.getUserId();
        if (null == id) {
            throw new CrmebException("登录信息已过期，请重新登录！");
        }
        return id;
    }

    /**
     * 获取当前用户id
     *
     * @return Integer
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public Integer getUserId() {
        Integer id = tokenComponet.getUserId();
        if (null == id) {
            return 0;
        }
        return id;
    }


    /**
     * 按开始结束时间查询每日新增用户数量
     *
     * @param date String 时间范围
     * @return HashMap<String, Object>
     */
    @Override
    public Map<Object, Object> getAddUserCountGroupDate(String date) {
        Map<Object, Object> map = new HashMap<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("count(uid) as uid", "left(create_time, 10) as create_time");
        if (StringUtils.isNotBlank(date)) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        queryWrapper.groupBy("left(create_time, 10)").orderByAsc("create_time");
        List<User> list = userDao.selectList(queryWrapper);
        if (list.size() < 1) {
            return map;
        }

        for (User user : list) {
            map.put(DateUtil.dateToStr(user.getCreateTime(), Constants.DATE_FORMAT_DATE), user.getUid());
        }
        return map;
    }

    /**
     * 换绑手机号校验
     */
    @Override
    public Boolean updatePhoneVerify(UserBindingPhoneUpdateRequest request) {
        //检测验证码
        checkValidateCode(request.getPhone(), request.getCaptcha());

        //删除验证码
        redisUtil.delete(getValidateCodeRedisKey(request.getPhone()));

        User user = getInfoException();

        if (!user.getPhone().equals(request.getPhone())) {
            throw new CrmebException("手机号不是当前用户手机号");
        }

        return Boolean.TRUE;
    }

    /**
     * 换绑手机号
     */
    @Override
    public Boolean updatePhone(UserBindingPhoneUpdateRequest request) {
        //检测验证码
        checkValidateCode(request.getPhone(), request.getCaptcha());

        //删除验证码
        redisUtil.delete(getValidateCodeRedisKey(request.getPhone()));

        //检测当前手机号是否已经是账号
        User user = getByPhone(request.getPhone());
        if (null != user) {
            throw new CrmebException("此手机号码已被注册");
        }

        //查询手机号信息
        User bindUser = getInfoException();
        bindUser.setAccount(request.getPhone());
        bindUser.setPhone(request.getPhone());
        return updateById(bindUser);
    }

    /**
     * 用户中心
     * @return UserCenterResponse
     */
    @Override
    public UserCenterResponse getUserCenter() {
        User currentUser = getInfo();
        if (ObjectUtil.isNull(currentUser)) {
            throw new CrmebException("您的登录已过期，请先登录");
        }
        UserCenterResponse userCenterResponse = new UserCenterResponse();
        BeanUtils.copyProperties(currentUser, userCenterResponse);
        // 优惠券数量
        userCenterResponse.setCouponCount(storeCouponUserService.getUseCount(currentUser.getUid()));
        // 收藏数量
        userCenterResponse.setCollectCount(storeProductRelationService.getCollectCountByUid(currentUser.getUid()));

        userCenterResponse.setVip(false);
        if (userCenterResponse.getLevel() > 0) {
            SystemUserLevel systemUserLevel = systemUserLevelService.getByLevelId(currentUser.getLevel());
            if (ObjectUtil.isNotNull(systemUserLevel)) {
                userCenterResponse.setVip(true);
                userCenterResponse.setVipIcon(systemUserLevel.getIcon());
                userCenterResponse.setVipName(systemUserLevel.getName());
            }
        }
        // 充值开关
        String rechargeSwitch = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_RECHARGE_SWITCH);
        if (StrUtil.isNotBlank(rechargeSwitch)) {
            userCenterResponse.setRechargeSwitch(Boolean.valueOf(rechargeSwitch));
        }

        // 判断是否展示我的推广，1.分销模式是否开启
        String funcStatus = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_BROKERAGE_FUNC_STATUS);
        if (!funcStatus.equals("1")) {
            userCenterResponse.setIsPromoter(false);
        }

        // 保存用户访问记录
        UserVisitRecord visitRecord = new UserVisitRecord();
        visitRecord.setDate(cn.hutool.core.date.DateUtil.date().toString("yyyy-MM-dd"));
        visitRecord.setUid(getUserId());
        visitRecord.setVisitType(4);
        userVisitRecordService.save(visitRecord);
        return userCenterResponse;
    }

    /**
     * 根据用户id获取用户列表 map模式
     *
     * @return HashMap<Integer, User>
     */
    @Override
    public HashMap<Integer, User> getMapListInUid(List<Integer> uidList) {
        List<User> userList = getListInUid(uidList);
        return getMapByList(userList);
    }

    /**
     * 根据用户id获取用户列表 map模式
     *
     * @return HashMap<Integer, User>
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    private HashMap<Integer, User> getMapByList(List<User> list) {
        HashMap<Integer, User> map = new HashMap<>();
        if (null == list || list.size() < 1) {
            return map;
        }

        for (User user : list) {
            map.put(user.getUid(), user);
        }

        return map;
    }

    /**
     * 重置连续签到天数
     *
     * @param userId Integer 用户id
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @Override
    public void repeatSignNum(Integer userId) {
        User user = new User();
        user.setUid(userId);
        user.setSignNum(0);
        updateById(user);
    }

    /**
     * 会员标签
     *
     * @param id         String id
     * @param tagIdValue Integer 标签Id
     */
    @Override
    public Boolean tag(String id, String tagIdValue) {
        if (StrUtil.isBlank(id)) throw new CrmebException("会员编号不能为空");
        if (StrUtil.isBlank(tagIdValue)) throw new CrmebException("标签id不能为空");

        //循环id处理
        List<Integer> idList = CrmebUtil.stringToArray(id);
        idList = idList.stream().distinct().collect(Collectors.toList());
        List<User> list = getListInUid(idList);
        if (CollUtil.isEmpty(list)) throw new CrmebException("没有找到用户信息");
        if (list.size() < 1) {
            throw new CrmebException("没有找到用户信息");
        }
        for (User user : list) {
            user.setTagId(tagIdValue);
        }
        return updateBatchById(list);
    }

    /**
     * 根据用户id获取自己本身的推广用户
     *
     * @param userIdList List<Integer> 用户id集合
     * @return List<User>
     */
    @Override
    public List<Integer> getSpreadPeopleIdList(List<Integer> userIdList) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(User::getUid); //查询用户id
        lambdaQueryWrapper.in(User::getSpreadUid, userIdList); //xx的下线集合
        List<User> list = userDao.selectList(lambdaQueryWrapper);

        if (null == list || list.size() < 1) {
            return new ArrayList<>();
        }
        return list.stream().map(User::getUid).distinct().collect(Collectors.toList());
    }

    /**
     * 根据用户id获取自己本身的推广用户
     */
    @Override
    public List<UserSpreadPeopleItemResponse> getSpreadPeopleList(
            List<Integer> userIdList, String keywords, String sortKey, String isAsc, PageParamRequest pageParamRequest) {

        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        Map<String, Object> map = new HashMap<>();
        map.put("userIdList", userIdList.stream().map(String::valueOf).distinct().collect(Collectors.joining(",")));
        if (StringUtils.isNotBlank(keywords)) {
            map.put("keywords", "%" + keywords + "%");
        }
        map.put("sortKey", "create_time");
        if (StringUtils.isNotBlank(sortKey)) {
            map.put("sortKey", sortKey);
        }
        map.put("sortValue", Constants.SORT_DESC);
        if (isAsc.toLowerCase().equals(Constants.SORT_ASC)) {
            map.put("sortValue", Constants.SORT_ASC);
        }

        return userDao.getSpreadPeopleList(map);
    }

    /**
     * 检测手机验证码
     */
    private void checkValidateCode(String phone, String value) {
        Object validateCode = redisUtil.get(getValidateCodeRedisKey(phone));
        if (validateCode == null) {
            throw new CrmebException("验证码已过期");
        }

        if (!validateCode.toString().equals(value)) {
            throw new CrmebException("验证码错误");
        }
    }

    /**
     * 检测手机验证码key
     *
     * @param phone String 手机号
     * @return String
     */
    @Override
    public String getValidateCodeRedisKey(String phone) {
        return SmsConstants.SMS_VALIDATE_PHONE + phone;
    }

    /**
     * 手机号注册用户
     *
     * @param phone     手机号
     * @param spreadUid 推广人编号
     * @return User
     */
    @Override
    public User registerPhone(String phone, Integer spreadUid) {
        User user = new User();
        user.setAccount(phone);
        user.setPwd(CommonUtil.createPwd(phone));
        user.setPhone(phone);
        user.setUserType(Constants.USER_LOGIN_TYPE_H5);
        user.setNickname(CommonUtil.createNickName(phone));
        user.setAvatar(systemConfigService.getValueByKey(Constants.USER_DEFAULT_AVATAR_CONFIG_KEY));
        Date nowDate = DateUtil.nowDateTime();
        user.setCreateTime(nowDate);
        user.setLastLoginTime(nowDate);

        // 推广人
        user.setSpreadUid(0);
        Boolean check = checkBingSpread(user, spreadUid, "new");
        if (check) {
            user.setSpreadUid(spreadUid);
            user.setSpreadTime(nowDate);
        }

        // 查询是否有新人注册赠送优惠券
        List<StoreCouponUser> couponUserList = CollUtil.newArrayList();
        List<StoreCoupon> couponList = storeCouponService.findRegisterList();
        if (CollUtil.isNotEmpty(couponList)) {
            couponList.forEach(storeCoupon -> {
                //是否有固定的使用时间
                if (!storeCoupon.getIsFixedTime()) {
                    String endTime = DateUtil.addDay(DateUtil.nowDate(Constants.DATE_FORMAT), storeCoupon.getDay(), Constants.DATE_FORMAT);
                    storeCoupon.setUseEndTime(DateUtil.strToDate(endTime, Constants.DATE_FORMAT));
                    storeCoupon.setUseStartTime(DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT));
                }

                StoreCouponUser storeCouponUser = new StoreCouponUser();
                storeCouponUser.setCouponId(storeCoupon.getId());
                storeCouponUser.setName(storeCoupon.getName());
                storeCouponUser.setMoney(storeCoupon.getMoney());
                storeCouponUser.setMinPrice(storeCoupon.getMinPrice());
                storeCouponUser.setStartTime(storeCoupon.getUseStartTime());
                storeCouponUser.setEndTime(storeCoupon.getUseEndTime());
                storeCouponUser.setUseType(storeCoupon.getUseType());
                storeCouponUser.setType(CouponConstants.STORE_COUPON_USER_TYPE_REGISTER);
                if (storeCoupon.getUseType() > 1) {
                    storeCouponUser.setPrimaryKey(storeCoupon.getPrimaryKey());
                }
                couponUserList.add(storeCouponUser);
            });
        }

        Boolean execute = transactionTemplate.execute(e -> {
            save(user);
            // 推广人处理
            if (check) {
                updateSpreadCountByUid(spreadUid, "add");
            }
            // 赠送客户优惠券
            if (CollUtil.isNotEmpty(couponUserList)) {
                couponUserList.forEach(couponUser -> couponUser.setUid(user.getUid()));
                storeCouponUserService.saveBatch(couponUserList);
                couponList.forEach(coupon -> storeCouponService.deduction(coupon.getId(), 1, coupon.getIsLimited()));
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("创建用户失败!");
        }
        return user;
    }

    /**
     * 更新推广员推广数
     *
     * @param uid uid
     * @param type add or sub
     */
    public Boolean updateSpreadCountByUid(Integer uid, String type) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (type.equals("add")) {
            updateWrapper.setSql("spread_count = spread_count + 1");
        } else {
            updateWrapper.setSql("spread_count = spread_count - 1");
        }
        updateWrapper.eq("uid", uid);
        return update(updateWrapper);
    }

    /**
     * 添加/扣减佣金
     *
     * @param uid            用户id
     * @param price          金额
     * @param brokeragePrice 历史金额
     * @param type           类型：add—添加，sub—扣减
     * @return Boolean
     */
    @Override
    public Boolean operationBrokerage(Integer uid, BigDecimal price, BigDecimal brokeragePrice, String type) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (type.equals("add")) {
            updateWrapper.setSql(StrUtil.format("brokerage_price = brokerage_price + {}", price));
        } else {
            updateWrapper.setSql(StrUtil.format("brokerage_price = brokerage_price - {}", price));
            updateWrapper.last(StrUtil.format(" and (brokerage_price - {} >= 0)", price));
        }
        updateWrapper.eq("uid", uid);
        updateWrapper.eq("brokerage_price", brokeragePrice);
        return update(updateWrapper);
    }

    /**
     * 添加/扣减余额
     *
     * @param uid      用户id
     * @param price    金额
     * @param nowMoney 历史金额
     * @param type     类型：add—添加，sub—扣减
     */
    @Override
    public Boolean operationNowMoney(Integer uid, BigDecimal price, BigDecimal nowMoney, String type) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (type.equals("add")) {
            updateWrapper.setSql(StrUtil.format("now_money = now_money + {}", price));
        } else {
            updateWrapper.setSql(StrUtil.format("now_money = now_money - {}", price));
            updateWrapper.last(StrUtil.format(" and (now_money - {} >= 0)", price));
        }
        updateWrapper.eq("uid", uid);
        updateWrapper.eq("now_money", nowMoney);
        return update(updateWrapper);
    }

    /**
     * 添加/扣减积分
     *
     * @param uid         用户id
     * @param integral    积分
     * @param nowIntegral 历史积分
     * @param type        类型：add—添加，sub—扣减
     * @return Boolean
     */
    @Override
    public Boolean operationIntegral(Integer uid, Integer integral, Integer nowIntegral, String type) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (type.equals("add")) {
            updateWrapper.setSql(StrUtil.format("integral = integral + {}", integral));
        } else {
            updateWrapper.setSql(StrUtil.format("integral = integral - {}", integral));
            updateWrapper.last(StrUtil.format(" and (integral - {} >= 0)", integral));
        }
        updateWrapper.eq("uid", uid);
        updateWrapper.eq("integral", nowIntegral);
        return update(updateWrapper);
    }

    /**
     * PC后台分销员列表
     *
     * @param keywords             搜索参数
     * @param dateLimit            时间参数
     * @param pageRequest          分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<User> getAdminSpreadPeopleList(String keywords, String dateLimit, PageParamRequest pageRequest) {
        Page<User> pageUser = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        // id,头像，昵称，姓名，电话，推广用户数，推广订单数，推广订单额，佣金总金额，已提现金额，提现次数，未提现金额，上级推广人
        lqw.select(User::getUid, User::getNickname, User::getRealName, User::getPhone, User::getAvatar,
                User::getSpreadCount, User::getBrokeragePrice, User::getSpreadUid, User::getPromoterTime);
        lqw.eq(User::getIsPromoter, true);
        if (StrUtil.isNotBlank(keywords)) {
            lqw.and(i -> i.eq(User::getUid, keywords) //用户账号
                    .or().like(User::getNickname, keywords) //昵称
                    .or().like(User::getPhone, keywords)); //手机号码
        }
        if (StrUtil.isNotBlank(dateLimit)) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(dateLimit);
            lqw.between(User::getPromoterTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        lqw.orderByDesc(User::getUid);
        List<User> userList = userDao.selectList(lqw);
        return CommonPage.copyPageInfo(pageUser, userList);
    }

    /**
     * 检测能否绑定关系
     *
     * @param user      当前用户
     * @param spreadUid 推广员Uid
     * @param type      用户类型:new-新用户，old—老用户
     * @return Boolean
     * 1.判断分销功能是否启用
     * 2.判断分销模式
     * 3.根据不同的分销模式校验
     * 4.指定分销，只有分销员才可以分销，需要spreadUid是推广员才可以绑定
     * 5.人人分销，可以直接绑定
     * *推广关系绑定，下级不能绑定自己的上级为下级，A->B->A(❌)
     */
    public Boolean checkBingSpread(User user, Integer spreadUid, String type) {
        if (ObjectUtil.isNull(spreadUid)) {
            return false;
        }
        if (spreadUid <= 0 || user.getSpreadUid() > 0) {
            return false;
        }
        if (ObjectUtil.isNotNull(user.getUid()) && user.getUid().equals(spreadUid)) {
            return false;
        }
        // 判断分销功能是否启用
        String isOpen = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_IS_OPEN);
        if (StrUtil.isBlank(isOpen) || isOpen.equals("0")) {
            return false;
        }
        if (type.equals("old")) {
            // 判断分销关系绑定类型（所有、新用户）
            String bindType = systemConfigService.getValueByKey(Constants.CONFIG_KEY_DISTRIBUTION_TYPE);
            if (StrUtil.isBlank(bindType) || bindType.equals("1")) {
                return false;
            }
            if (user.getSpreadUid().equals(spreadUid)) {
                return false;
            }
        }
        // 查询推广员
        User spreadUser = getById(spreadUid);
        if (ObjectUtil.isNull(spreadUser) || !spreadUser.getStatus()) {
            return false;
        }
        // 指定分销不是推广员不绑定
        if (!spreadUser.getIsPromoter()) {
            return false;
        }
        // 下级不能绑定自己的上级为自己的下级
        if (ObjectUtil.isNotNull(user.getUid()) && spreadUser.getSpreadUid().equals(user.getUid())) {
            return false;
        }
        return true;
    }

    /**
     * 获取用户好友关系，spread_uid往下两级的用户信息
     *
     * @return List<User>
     */
    private List<User> getUserRelation(Integer userId) {
        List<User> userList = new ArrayList<>();
        User currUser = userDao.selectById(userId);
        if (currUser.getSpreadUid() > 0) {
            User spUser1 = userDao.selectById(currUser.getSpreadUid());
            if (null != spUser1) {
                userList.add(spUser1);
                if (spUser1.getSpreadUid() > 0) {
                    User spUser2 = userDao.selectById(spUser1.getSpreadUid());
                    if (null != spUser2) {
                        userList.add(spUser2);
                    }
                }
            }
        }
        return userList;
    }

    /**
     * 根据条件获取会员对应信息列表
     *
     * @param userId 用户id
     * @param type  0=消费记录，1=积分明细，2=签到记录，3=持有优惠券，4=余额变动，5=好友关系
     * @param pageParamRequest 分页参数
     * @return Object
     */
    @Override
    public Object getInfoByCondition(Integer userId, Integer type, PageParamRequest pageParamRequest) {
        switch (type) {
            case 0:
                return storeOrderService.findPaidListByUid(userId, pageParamRequest);
            case 1:
                AdminIntegralSearchRequest fmsq = new AdminIntegralSearchRequest();
                fmsq.setUid(userId);
                return userIntegralRecordService.findAdminList(fmsq, pageParamRequest);
            case 2:
                UserSign userSign = new UserSign();
                userSign.setUid(userId);
                return userSignService.getListByCondition(userSign, pageParamRequest);
            case 3:
                StoreCouponUserSearchRequest scur = new StoreCouponUserSearchRequest();
                scur.setUid(userId);
                return storeCouponUserService.findListByUid(userId, pageParamRequest);
            case 4:
                FundsMonitorSearchRequest fmsqq = new FundsMonitorSearchRequest();
                fmsqq.setUid(userId);
                fmsqq.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
                return userBillService.getList(fmsqq, pageParamRequest);
            case 5:
                return getUserRelation(userId);
        }

        return new ArrayList<>();
    }

    /**
     * 会员详情顶部数据
     *
     * @param userId Integer 用户id
     * @return Object
     */
    @Override
    public TopDetail getTopDetail(Integer userId) {
        TopDetail topDetail = new TopDetail();
        User currentUser = userDao.selectById(userId);
        topDetail.setUser(currentUser);
        topDetail.setBalance(currentUser.getNowMoney());
        topDetail.setIntegralCount(currentUser.getIntegral());
        topDetail.setMothConsumeCount(storeOrderService.getSumPayPriceByUidAndDate(userId, Constants.SEARCH_DATE_MONTH));
        topDetail.setAllConsumeCount(storeOrderService.getSumPayPriceByUid(userId));
        topDetail.setMothOrderCount(storeOrderService.getOrderCountByUidAndDate(userId, Constants.SEARCH_DATE_MONTH));
        topDetail.setAllOrderCount(storeOrderService.getOrderCountByUid(userId));
        return topDetail;
    }

    /**
     * 通过微信信息注册用户
     *
     * @param thirdUserRequest RegisterThirdUser 三方用户登录注册信息
     * @return User
     */
    @Override
    public User registerByThird(RegisterThirdUserRequest thirdUserRequest) {
        User user = new User();
        user.setAccount(DigestUtils.md5Hex(CrmebUtil.getUuid() + DateUtil.getNowTime()));
        user.setUserType(thirdUserRequest.getType());
        user.setNickname(thirdUserRequest.getNickName());
        String avatar = null;
        switch (thirdUserRequest.getType()) {
            case Constants.USER_LOGIN_TYPE_PUBLIC:
                avatar = thirdUserRequest.getHeadimgurl();
                break;
            case Constants.USER_LOGIN_TYPE_PROGRAM:
            case Constants.USER_LOGIN_TYPE_H5:
            case Constants.USER_LOGIN_TYPE_IOS_WX:
            case Constants.USER_LOGIN_TYPE_ANDROID_WX:
                avatar = thirdUserRequest.getAvatar();
                break;
        }
        user.setAvatar(avatar);
        user.setSpreadTime(DateUtil.nowDateTime());
        user.setSex(Integer.parseInt(thirdUserRequest.getSex()));
        user.setAddres(thirdUserRequest.getCountry() + "," + thirdUserRequest.getProvince() + "," + thirdUserRequest.getCity());
        return user;
    }

    /**
     * 根据推广级别和其他参数当前用户下的推广列表
     *
     * @param request 推广列表参数
     * @return 当前用户的推广人列表
     */
    @Override
    public PageInfo<User> getUserListBySpreadLevel(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        if (request.getType().equals(1)) {// 一级推广人
            return getFirstSpreadUserListPage(request, pageParamRequest);
        }
        if (request.getType().equals(2)) {// 二级推广人
            return getSecondSpreadUserListPage(request, pageParamRequest);
        }
        return getAllSpreadUserListPage(request, pageParamRequest);
    }

    // 分页获取一级推广员
    private PageInfo<User> getFirstSpreadUserListPage(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        Page<User> userPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getUid, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        queryWrapper.eq(User::getSpreadUid, request.getUid());
        if (StrUtil.isNotBlank(request.getNickName())) {
            queryWrapper.and(e -> e.like(User::getNickname, request.getNickName()).or().eq(User::getUid, request.getNickName())
                    .or().eq(User::getPhone, request.getNickName()));
        }
        List<User> userList = userDao.selectList(queryWrapper);
        return CommonPage.copyPageInfo(userPage, userList);
    }

    // 分页获取二级推广员
    private PageInfo<User> getSecondSpreadUserListPage(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        // 先获取一级推广员
        List<User> firstUserList = getSpreadListBySpreadIdAndType(request.getUid(), 1);
        if (CollUtil.isEmpty(firstUserList)) {
            return new PageInfo<>(CollUtil.newArrayList());
        }
        List<Integer> userIds = firstUserList.stream().map(User::getUid).distinct().collect(Collectors.toList());
        Page<User> userPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getUid, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        queryWrapper.in(User::getSpreadUid, userIds);
        if (StrUtil.isNotBlank(request.getNickName())) {
            queryWrapper.and(e -> e.like(User::getNickname, request.getNickName()).or().eq(User::getUid, request.getNickName())
                    .or().eq(User::getPhone, request.getNickName()));
        }
        List<User> userList = userDao.selectList(queryWrapper);
        return CommonPage.copyPageInfo(userPage, userList);
    }

    // 分页获取所有推广员
    private PageInfo<User> getAllSpreadUserListPage(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        // 先所有一级推广员
        List<User> firstUserList = getSpreadListBySpreadIdAndType(request.getUid(), 0);
        if (CollUtil.isEmpty(firstUserList)) {
            return new PageInfo<>(CollUtil.newArrayList());
        }
        List<Integer> userIds = firstUserList.stream().map(User::getUid).distinct().collect(Collectors.toList());
        Page<User> userPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getUid, User::getAvatar, User::getNickname, User::getIsPromoter, User::getSpreadCount, User::getPayCount);
        queryWrapper.in(User::getUid, userIds);
        if (StrUtil.isNotBlank(request.getNickName())) {
            queryWrapper.and(e -> e.like(User::getNickname, request.getNickName()).or().eq(User::getUid, request.getNickName())
                    .or().eq(User::getPhone, request.getNickName()));
        }
        List<User> userList = userDao.selectList(queryWrapper);
        return CommonPage.copyPageInfo(userPage, userList);
    }

    /**
     * 根据推广级别和其他参数获取推广列表
     *
     * @param request 推广层级和推广时间参数
     * @return 推广订单列表
     */
    @Override
    public PageInfo<SpreadOrderResponse> getOrderListBySpreadLevel(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        // 获取推广人列表
        if (ObjectUtil.isNull(request.getType())) {
            request.setType(0);
        }
        PageInfo<UserBrokerageRecord> recordPageInfo = userBrokerageRecordService.findAdminSpreadListByUid(request, pageParamRequest);
        List<SpreadOrderResponse> responseList = recordPageInfo.getList().stream().map(e -> {
            SpreadOrderResponse response = new SpreadOrderResponse();
            StoreOrder storeOrder = storeOrderService.getByOderId(e.getLinkId());
            response.setId(storeOrder.getId());
            response.setOrderId(storeOrder.getOrderId());
            response.setRealName(storeOrder.getRealName());
            response.setUserPhone(storeOrder.getUserPhone());
            response.setPrice(e.getPrice());
            response.setUpdateTime(e.getUpdateTime());
            return response;
        }).collect(Collectors.toList());

        return CommonPage.copyPageInfo(recordPageInfo, responseList);
    }

//    /**
//     * 根据推广级别和其他参数获取推广列表
//     *
//     * @param request 推广层级和推广时间参数
//     * @return 推广订单列表
//     */
//    @Override
//    public PageInfo<SpreadOrderResponse> getOrderListBySpreadLevel(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
//        // 获取推广人列表
//        if (ObjectUtil.isNull(request.getType())) {
//            request.setType(0);
//        }
//        List<User> userList = getSpreadListBySpreadIdAndType(request.getUid(), request.getType());
//        if (CollUtil.isEmpty(userList)) {
//            return new PageInfo<>();
//        }
//
//        List<Integer> userIds = userList.stream().map(User::getUid).distinct().collect(Collectors.toList());
//        // 获取推广人订单号集合
//        List<StoreOrder> orderList = storeOrderService.getOrderListStrByUids(userIds, request);
//        if (CollUtil.isEmpty(orderList)) {
//            return new PageInfo<>();
//        }
//        List<String> orderNoList = CollUtil.newArrayList();
//        Map<String, StoreOrder> orderMap = CollUtil.newHashMap();
//        orderList.forEach(e -> {
//            orderNoList.add(e.getOrderId());
//            orderMap.put(e.getOrderId(), e);
//        });
//        // 获取用户佣金记录
//        PageInfo<UserBrokerageRecord> recordPageInfo = userBrokerageRecordService.findListByLinkIdsAndLinkTypeAndUid(orderNoList, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER, request.getUid(), pageParamRequest);
//        List<SpreadOrderResponse> responseList = recordPageInfo.getList().stream().map(e -> {
//            SpreadOrderResponse response = new SpreadOrderResponse();
//            StoreOrder storeOrder = orderMap.get(e.getLinkId());
//            response.setId(storeOrder.getId());
//            response.setOrderId(storeOrder.getOrderId());
//            response.setRealName(storeOrder.getRealName());
//            response.setUserPhone(storeOrder.getUserPhone());
//            response.setPrice(e.getPrice());
//            response.setUpdateTime(e.getUpdateTime());
//            return response;
//        }).collect(Collectors.toList());
//
//        return CommonPage.copyPageInfo(recordPageInfo, responseList);
//    }

    /**
     * 获取推广人列表
     *
     * @param spreadUid 父Uid
     * @param type      类型 0 = 全部 1=一级推广人 2=二级推广人
     */
    private List<User> getSpreadListBySpreadIdAndType(Integer spreadUid, Integer type) {
        // 获取一级推广人
        List<User> userList = getSpreadListBySpreadId(spreadUid);
        if (CollUtil.isEmpty(userList)) return userList;
        if (type.equals(1)) return userList;
        // 获取二级推广人
        List<User> userSecondList = CollUtil.newArrayList();
        userList.forEach(user -> {
            List<User> childUserList = getSpreadListBySpreadId(user.getUid());
            if (CollUtil.isNotEmpty(childUserList)) {
                userSecondList.addAll(childUserList);
            }
        });
        if (type.equals(2)) {
            return userSecondList;
        }
        userList.addAll(userSecondList);
        return userList;
    }

    /**
     * 获取推广人列表
     *
     * @param spreadUid 父Uid
     */
    private List<User> getSpreadListBySpreadId(Integer spreadUid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getSpreadUid, spreadUid);
        return userDao.selectList(queryWrapper);
    }

    /**
     * 根据用户id清除用户当前推广人
     *
     * @param userId 当前推广人id
     * @return 清除推广结果
     */
    @Override
    public boolean clearSpread(Integer userId) {
        User teamUser = getById(userId);
        User user = new User();
        user.setUid(userId);
        user.setPath("/0/");
        user.setSpreadUid(0);
        user.setSpreadTime(null);
        Boolean execute = transactionTemplate.execute(e -> {
            userDao.updateById(user);
            if (teamUser.getSpreadUid() > 0) {
                updateSpreadCountByUid(teamUser.getSpreadUid(), "sub");
            }
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 推广人排行
     *
     * @param type             String 类型
     * @param pageParamRequest PageParamRequest 分页
     * @return List<User>
     */
    @Override
    public List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("count(spread_count) as spread_count, spread_uid")
                .gt("spread_uid", 0)
                .eq("status", true);
        if (StrUtil.isNotBlank(type)) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(type);
            queryWrapper.between("spread_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        queryWrapper.groupBy("spread_uid").orderByDesc("spread_count");
        List<User> spreadVoList = userDao.selectList(queryWrapper);
        if (spreadVoList.size() < 1) {
            return null;
        }

        List<Integer> spreadIdList = spreadVoList.stream().map(User::getSpreadUid).collect(Collectors.toList());
        if (spreadIdList.size() < 1) {
            return null;
        }

        ArrayList<User> userList = new ArrayList<>();
        //查询用户
        HashMap<Integer, User> userVoList = getMapListInUid(spreadIdList);

        //解决排序问题
        for (User spreadVo : spreadVoList) {
            User user = new User();
            User userVo = userVoList.get(spreadVo.getSpreadUid());
            user.setUid(spreadVo.getSpreadUid());
            user.setAvatar(userVo.getAvatar());
            user.setSpreadCount(spreadVo.getSpreadCount());
            if (StringUtils.isBlank(userVo.getNickname())) {
                user.setNickname(userVo.getPhone().substring(0, 2) + "****" + userVo.getPhone().substring(7));
            } else {
                user.setNickname(userVo.getNickname());
            }
            userList.add(user);
        }

        return userList;
    }

    /**
     * 推广人排行
     *
     * @param minPayCount int 最小消费次数
     * @param maxPayCount int 最大消费次数
     * @return Integer
     */
    @Override
    public Integer getCountByPayCount(int minPayCount, int maxPayCount) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.between(User::getPayCount, minPayCount, maxPayCount);
        return userDao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 绑定推广关系（登录状态）
     * @param spreadUid 推广人id
     */
    @Override
    public void bindSpread(Integer spreadUid) {
        //新用户会在注册的时候单独绑定，此处只处理登录用户
        if (ObjectUtil.isNull(spreadUid) || spreadUid == 0) {
            return;
        }
        User user = getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("当前用户未登录,请先登录");
        }

        bindSpread(user, spreadUid);
    }

    private Boolean bindSpread(User user, Integer spreadUid) {

        Boolean checkBingSpread = checkBingSpread(user, spreadUid, "old");
        if (!checkBingSpread) return false;

        user.setSpreadUid(spreadUid);
        user.setSpreadTime(DateUtil.nowDateTime());

        Boolean execute = transactionTemplate.execute(e -> {
            updateById(user);
            updateSpreadCountByUid(spreadUid, "add");
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("绑定推广人时出错，userUid = {}, spreadUid = {}", user.getUid(), spreadUid));
        }
        return execute;

    }

    /**
     * 更新推广人
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean editSpread(UserUpdateSpreadRequest request) {
        Integer userId = request.getUserId();
        Integer spreadUid = request.getSpreadUid();
        if (userId.equals(spreadUid)) {
            throw new CrmebException("上级推广人不能为自己");
        }
        User user = getById(userId);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }
        if (user.getSpreadUid().equals(spreadUid)) {
            throw new CrmebException("当前推广人已经是所选人");
        }
        Integer oldSprUid = user.getSpreadUid();

        User spreadUser = getById(spreadUid);
        if (ObjectUtil.isNull(spreadUser)) {
            throw new CrmebException("上级用户不存在");
        }
        if (spreadUser.getSpreadUid().equals(userId)) {
            throw new CrmebException("当前用户已是推广人的上级");
        }

        User tempUser = new User();
        tempUser.setUid(userId);
        tempUser.setSpreadUid(spreadUid);
        tempUser.setSpreadTime(DateUtil.nowDateTime());
        Boolean execute = transactionTemplate.execute(e -> {
            updateById(tempUser);
            updateSpreadCountByUid(spreadUid, "add");
            if (oldSprUid > 0) {
                updateSpreadCountByUid(oldSprUid, "sub");
            }
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 更新用户积分
     *
     * @param user     用户
     * @param integral 积分
     * @param type     增加add、扣减sub
     * @return 更新后的用户对象
     */
    @Override
    public Boolean updateIntegral(User user, Integer integral, String type) {
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        if (type.equals("add")) {
            lambdaUpdateWrapper.set(User::getIntegral, user.getIntegral() + integral);
        } else {
            lambdaUpdateWrapper.set(User::getIntegral, user.getIntegral() - integral);
        }
        lambdaUpdateWrapper.eq(User::getUid, user.getUid());
        if (type.equals("sub")) {
            lambdaUpdateWrapper.apply(StrUtil.format(" integral - {} >= 0", integral));
        }
        return update(lambdaUpdateWrapper);
    }

    /**
     * 清除User Group id
     *
     * @param groupId 待清除的GroupId
     */
    @Override
    public void clearGroupByGroupId(String groupId) {
        LambdaUpdateWrapper<User> upw = Wrappers.lambdaUpdate();
        upw.set(User::getGroupId, "").eq(User::getGroupId, groupId);
        update(upw);
    }

    /**
     * 更新用户
     *
     * @param userRequest 用户参数
     * @return Boolean
     */
    @Override
    public Boolean updateUser(UserUpdateRequest userRequest) {
        User tempUser = getById(userRequest.getUid());
        User user = new User();
        BeanUtils.copyProperties(userRequest, user);
        if (!tempUser.getIsPromoter() && user.getIsPromoter()) {
            user.setPromoterTime(cn.hutool.core.date.DateUtil.date());
        }
        return updateById(user);
    }

    /**
     * 根据手机号查询用户
     * @param phone 用户手机号
     * @return 用户信息
     */
    @Override
    public User getByPhone(String phone) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone, phone);
        return userDao.selectOne(lqw);
    }

    /**
     * 后台修改用户手机号
     * @param id 用户uid
     * @param phone 手机号
     * @return Boolean
     */
    @Override
    public Boolean updateUserPhone(Integer id, String phone) {
        boolean matchPhone = ReUtil.isMatch(RegularConstants.PHONE_TWO, phone);
        if (!matchPhone) {
            throw new CrmebException("手机号格式错误，请输入正确得手机号");
        }
        User user = getById(id);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("对应用户不存在");
        }
        if (phone.equals(user.getPhone())) {
            throw new CrmebException("手机号与之前一致");
        }

        //检测当前手机号是否已经是账号
        User tempUser = getByPhone(phone);
        if (ObjectUtil.isNotNull(tempUser)) {
            throw new CrmebException("此手机号码已被注册");
        }

        User newUser = new User();
        newUser.setUid(id);
        newUser.setPhone(phone);
        newUser.setAccount(phone);
        return userDao.updateById(newUser) > 0;
    }

    /**
     * 根据昵称匹配用户，返回id集合
     * @param nikeName 需要匹配得昵称
     * @return List
     */
    @Override
    public List<Integer> findIdListLikeName(String nikeName) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.select(User::getUid);
        lqw.like(User::getNickname, nikeName);
        List<User> userList = userDao.selectList(lqw);
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(User::getUid).collect(Collectors.toList());
    }

    /**
     * 清除对应的用户等级
     * @param levelId 等级id
     */
    @Override
    public Boolean removeLevelByLevelId(Integer levelId) {
        LambdaUpdateWrapper<User> luw = Wrappers.lambdaUpdate();
        luw.set(User::getLevel, 0);
        luw.eq(User::getLevel, levelId);
        return update(luw);
    }

    /**
     * 更新用户会员等级
     * @param request request
     * @return Boolean
     */
    @Override
    public Boolean updateUserLevel(UpdateUserLevelRequest request) {
        User user = getById(request.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }
        if (user.getLevel().equals(request.getLevelId())) {
            throw new CrmebException("用户等级与修改前相同");
        }

        SystemUserLevel systemUserLevel = systemUserLevelService.getByLevelId(request.getLevelId());
        if (ObjectUtil.isNull(systemUserLevel)) {
            throw new CrmebException("系统会员等级不存在，请先配置");
        }
        SystemUserLevel userLevel = new SystemUserLevel();
        if (ObjectUtil.isNull(user.getLevel()) || user.getLevel().equals(0)) {
            userLevel.setGrade(0);
        } else {
            userLevel = systemUserLevelService.getByLevelId(user.getLevel());
        }
        if (ObjectUtil.isNull(userLevel)) {
            throw new CrmebException("用户会员等级不存在，请检查用户数据");
        }
        if (userLevel.getGrade() > systemUserLevel.getGrade()) {
            if (ObjectUtil.isNull(request.getIsSub())) {
                throw new CrmebException("降低用户等级时，请选择是否扣除用户经验值");
            }
        }

        // 升级
        if (userLevel.getGrade() < systemUserLevel.getGrade()) {
            // 创建用户会员等级记录
            UserLevel newLevel = new UserLevel();
            newLevel.setUid(user.getUid());
            newLevel.setLevelId(systemUserLevel.getId());
            newLevel.setGrade(systemUserLevel.getGrade());
            newLevel.setStatus(true);
            newLevel.setMark(StrUtil.format("尊敬的用户 {},在{}管理员调整会员等级成为{}", user.getNickname(), DateUtil.nowDateTimeStr(), systemUserLevel.getName()));
            newLevel.setDiscount(systemUserLevel.getDiscount());
            newLevel.setCreateTime(DateUtil.nowDateTime());
            return transactionTemplate.execute(e -> {
                updateLevel(user.getUid(), request.getLevelId());
                userLevelService.save(newLevel);
                return Boolean.TRUE;
            });
        }
        // 降级
        if (!request.getIsSub()) {// 不扣经验
            // 创建用户会员等级记录
            UserLevel newLevel = new UserLevel();
            newLevel.setUid(user.getUid());
            newLevel.setLevelId(systemUserLevel.getId());
            newLevel.setGrade(systemUserLevel.getGrade());
            newLevel.setStatus(true);
            newLevel.setMark(StrUtil.format("尊敬的用户 {},在{}管理员调整会员等级成为{},不扣除经验", user.getNickname(), DateUtil.nowDateTimeStr(), systemUserLevel.getName()));
            newLevel.setDiscount(systemUserLevel.getDiscount());
            newLevel.setCreateTime(DateUtil.nowDateTime());
            return transactionTemplate.execute(e -> {
                updateLevel(user.getUid(), request.getLevelId());
                userLevelService.save(newLevel);
                return Boolean.TRUE;
            });
        }
        // 扣除经验
        Integer deductionExp = user.getExperience() - systemUserLevel.getExperience();
        user.setExperience(systemUserLevel.getExperience());
        user.setLevel(systemUserLevel.getId());
        // 创建用户经验记录
        UserExperienceRecord experienceRecord = new UserExperienceRecord();
        experienceRecord.setUid(user.getUid());
        experienceRecord.setLinkType(ExperienceRecordConstants.EXPERIENCE_RECORD_LINK_TYPE_SYSTEM);
        experienceRecord.setType(ExperienceRecordConstants.EXPERIENCE_RECORD_TYPE_SUB);
        experienceRecord.setTitle(ExperienceRecordConstants.EXPERIENCE_RECORD_TITLE_ADMIN);
        experienceRecord.setExperience(deductionExp);
        experienceRecord.setBalance(user.getExperience());
        experienceRecord.setMark(StrUtil.format("管理员降级扣减了{}经验", deductionExp));
        experienceRecord.setStatus(ExperienceRecordConstants.EXPERIENCE_RECORD_STATUS_CREATE);

        // 创建用户会员等级记录
        UserLevel newLevel = new UserLevel();
        newLevel.setUid(user.getUid());
        newLevel.setLevelId(systemUserLevel.getId());
        newLevel.setGrade(systemUserLevel.getGrade());
        newLevel.setStatus(true);
        newLevel.setMark(StrUtil.format("尊敬的用户 {},在{}管理员调整会员等级成为{},扣除经验{}", user.getNickname(), DateUtil.nowDateTimeStr(), systemUserLevel.getName(), deductionExp));
        newLevel.setDiscount(systemUserLevel.getDiscount());
        newLevel.setCreateTime(DateUtil.nowDateTime());
        return transactionTemplate.execute(e -> {
            updateById(user);
            userLevelService.save(newLevel);
            experienceRecordService.save(experienceRecord);
            return Boolean.TRUE;
        });
    }

    /**
     * 获取用户总人数
     */
    @Override
    public Integer getTotalNum() {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.select(User::getUid);
        return userDao.selectCount(lqw);
    }

    /**
     * 根据日期段获取注册用户数量
     * @param startDate 日期
     * @param endDate 日期
     * @return UserOverviewResponse
     */
    @Override
    public Integer getRegisterNumByPeriod(String startDate, String endDate) {
        QueryWrapper<User> wrapper = Wrappers.query();
        wrapper.select("uid");
        wrapper.apply("date_format(create_time, '%Y-%m-%d') between {0} and {1}", startDate, endDate);
        return userDao.selectCount(wrapper);
    }

    /**
     * 获取用户性别数据
     * @return List
     */
    @Override
    public List<User> getSexData() {
        QueryWrapper<User> wrapper = Wrappers.query();
        wrapper.select("sex", "count(uid) as pay_count");
        wrapper.groupBy("sex");
        return userDao.selectList(wrapper);
    }

    /**
     * 获取用户渠道数据
     * @return List
     */
    @Override
    public List<User> getChannelData() {
        QueryWrapper<User> wrapper = Wrappers.query();
        wrapper.select("user_type", "count(uid) as pay_count");
        wrapper.groupBy("user_type");
        return userDao.selectList(wrapper);
    }

    /**
     * 获取所有用户的id跟地址
     * @return List
     */
    @Override
    public List<User> findIdAndAddresList() {
        QueryWrapper<User> wrapper = Wrappers.query();
        wrapper.select("uid", "addres");
        return userDao.selectList(wrapper);
    }

    /**
     * 修改个人资料
     * @param request 修改信息
     */
    @Override
    public Boolean editUser(UserEditRequest request) {
        User user = getInfo();
        user.setAvatar(systemAttachmentService.clearPrefix(request.getAvatar()));
        user.setNickname(request.getNickname());
        return updateById(user);
    }

    /**
     * 获取用户详情
     * @param id 用户uid
     */
    @Override
    public User getInfoByUid(Integer id) {
        User user = getById(id);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }
        return user;
    }

    /**
     * 根据日期获取注册用户数量
     * @param date 日期
     * @return Integer
     */
    @Override
    public Integer getRegisterNumByDate(String date) {
        QueryWrapper<User> wrapper = Wrappers.query();
        wrapper.select("uid");
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return userDao.selectCount(wrapper);
    }

    /**
     * 更新用户等级
     * @param uid 用户id
     * @param levelId 会员等级id
     * @return Boolean
     */
    private Boolean updateLevel(Integer uid, Integer levelId) {
        LambdaUpdateWrapper<User> luw = Wrappers.lambdaUpdate();
        luw.set(User::getLevel, levelId);
        luw.eq(User::getUid, uid);
        return update(luw);
    }
}
