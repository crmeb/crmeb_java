package com.zbkj.common.model.wechat.video;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 组件商户品牌表
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_pay_component_shop_brand")
@ApiModel(value="PayComponentShopBrand对象", description="组件商户品牌表")
public class PayComponentShopBrand implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "品牌ID")
    private Integer brandId;

    @ApiModelProperty(value = "审核单id")
    private String auditId;

    @ApiModelProperty(value = "营业执照或组织机构代码证，图片url/media_id,逗号分隔")
    private String license;

    @ApiModelProperty(value = "认证审核类型，1：国内品牌申请-R标，2：国内品牌申请-TM标，3：海外品牌申请-R标，4：海外品牌申请-TM标")
    private Integer brandAuditType;

    @ApiModelProperty(value = "商标分类，共有45个分类")
    private String trademarkType;

    @ApiModelProperty(value = "选择品牌经营类型：1-自有品牌，2-代理品牌，3-无品牌")
    private Integer brandManagementType;

    @ApiModelProperty(value = "商品产地是否进口：1-是，2-否")
    private Integer commodityOriginType;

    @ApiModelProperty(value = "商标/品牌词")
    private String brandWording;

    @ApiModelProperty(value = "销售授权书（如商持人为自然人，还需提供有其签名的身份证正反面扫描件)，图片url/media_id,逗号分隔")
    private String saleAuthorization;

    @ApiModelProperty(value = "商标注册证书，图片url/media_id,逗号分隔")
    private String trademarkRegistrationCertificate;

    @ApiModelProperty(value = "商标变更证明，图片url/media_id,逗号分隔")
    private String trademarkChangeCertificate;

    @ApiModelProperty(value = "商标注册人姓名")
    private String trademarkRegistrant;

    @ApiModelProperty(value = "商标注册号/申请号")
    private String trademarkRegistrantNu;

    @ApiModelProperty(value = "商标有效期，yyyy-MM-dd HH:mm:ss")
    private String trademarkAuthorizationPeriod;

    @ApiModelProperty(value = "商标注册申请受理通知书，图片url/media_id,逗号分隔")
    private String trademarkRegistrationApplication;

    @ApiModelProperty(value = "商标申请人姓名")
    private String trademarkApplicant;

    @ApiModelProperty(value = "商标申请时间, yyyy-MM-dd HH:mm:ss")
    private String trademarkApplicationTime;

    @ApiModelProperty(value = "中华人民共和国海关进口货物报关单，图片url/media_id,逗号分隔")
    private String importedGoodsForm;

    @ApiModelProperty(value = "审核状态, 0：审核中，1：审核成功，9：审核拒绝")
    private Integer status;

    @ApiModelProperty(value = "如果审核拒绝，返回拒绝原因")
    private String rejectReason;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
