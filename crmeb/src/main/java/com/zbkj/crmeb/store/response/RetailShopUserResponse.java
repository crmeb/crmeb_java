package com.zbkj.crmeb.store.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zbkj.crmeb.finance.response.UserExtractResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销用户列表
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
public class RetailShopUserResponse {

    public RetailShopUserResponse() {
    }

    public RetailShopUserResponse(Integer uid, String account, String pwd, String realName, String birthday, String cardId, String mark, Integer partnerId, Integer groupId, String groupName, String tagName, String nickname, String avatar, String phone, String addIp, String lastIp, BigDecimal nowMoney, BigDecimal brokeragePrice, BigDecimal integral, Integer experience, Integer signNum, Boolean status, Integer level, Integer spreadUid, Date spreadTime, String spreadNickname, String userType, Boolean isPromoter, Integer payCount, Integer spreadCount, String addres, Integer adminid, String loginType, Date updateTime, Date createTime, Date lastLoginTime, Date cleanTime, UserExtractResponse userExtractResponse, RetailShopOrderDataResponse retailShopOrderDataResponse) {
        this.uid = uid;
        this.account = account;
        this.pwd = pwd;
        this.realName = realName;
        this.birthday = birthday;
        this.cardId = cardId;
        this.mark = mark;
        this.partnerId = partnerId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.tagName = tagName;
        this.nickname = nickname;
        this.avatar = avatar;
        this.phone = phone;
        this.addIp = addIp;
        this.lastIp = lastIp;
        this.nowMoney = nowMoney;
        this.brokeragePrice = brokeragePrice;
        this.integral = integral;
        this.experience = experience;
        this.signNum = signNum;
        this.status = status;
        this.level = level;
        this.spreadUid = spreadUid;
        this.spreadTime = spreadTime;
        this.spreadNickname = spreadNickname;
        this.userType = userType;
        this.isPromoter = isPromoter;
        this.payCount = payCount;
        this.spreadCount = spreadCount;
        this.addres = addres;
        this.adminid = adminid;
        this.loginType = loginType;
        this.updateTime = updateTime;
        this.createTime = createTime;
        this.lastLoginTime = lastLoginTime;
        this.cleanTime = cleanTime;
        this.userExtractResponse = userExtractResponse;
        this.retailShopOrderDataResponse = retailShopOrderDataResponse;
    }

    @ApiModelProperty(value = "用户id")
    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;

    @ApiModelProperty(value = "用户账号")
    private String account;

    @ApiModelProperty(value = "用户密码")
    @JsonIgnore
    private String pwd;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "身份证号码")
    private String cardId;

    @ApiModelProperty(value = "用户备注")
    private String mark;

    @ApiModelProperty(value = "合伙人id")
    private Integer partnerId;

    @ApiModelProperty(value = "用户分组id")
    private Integer groupId;

    @ApiModelProperty(value = "分组名称")
    private String groupName;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "添加ip")
    private String addIp;

    @ApiModelProperty(value = "最后一次登录ip")
    private String lastIp;

    @ApiModelProperty(value = "用户余额")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "佣金金额")
    private BigDecimal brokeragePrice;

    @ApiModelProperty(value = "用户剩余积分")
    private BigDecimal integral;

    @ApiModelProperty(value = "用户剩余经验")
    private Integer experience;

    @ApiModelProperty(value = "连续签到天数")
    private Integer signNum;

    @ApiModelProperty(value = "1为正常，0为禁止")
    private Boolean status;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "推广人id")
    private Integer spreadUid;

    @ApiModelProperty(value = "推广员关联时间")
    private Date spreadTime;

    @ApiModelProperty(value = "推广员名称")
    private String spreadNickname;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "是否为推广员")
    private Boolean isPromoter;

    @ApiModelProperty(value = "用户购买次数")
    private Integer payCount;

    @ApiModelProperty(value = "下级人数")
    private Integer spreadCount;

    @ApiModelProperty(value = "详细地址")
    private String addres;

    @ApiModelProperty(value = "管理员编号 ")
    private Integer adminid;

    @ApiModelProperty(value = "用户登陆类型，h5,wechat,routine")
    private String loginType;

    @ApiModelProperty(value = "创建时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "最后一次登录时间")
    private Date lastLoginTime;

    @ApiModelProperty(value = "清除时间")
    private Date cleanTime;

    @ApiModelProperty(value = "提现数据")
    private UserExtractResponse userExtractResponse;

    @ApiModelProperty(value = "订单数据")
    private RetailShopOrderDataResponse retailShopOrderDataResponse;

    @ApiModelProperty(value = "佣金数据")
    private BigDecimal brokerageMoney;

    @ApiModelProperty(value = "推广用户数量")
    private int spreadPeopleCount;
}
