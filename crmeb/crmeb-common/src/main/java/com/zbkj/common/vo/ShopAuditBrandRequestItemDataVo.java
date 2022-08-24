package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 上传品牌信息 参数 Request itemData
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class ShopAuditBrandRequestItemDataVo {
    /**
     *  认证审核类型 RegisterType 枚举值	描述
     * 1	国内品牌申请-R标
     * 2	国内品牌申请-TM标
     * 3	海外品牌申请-R标
     * 4	海外品牌申请-TM标
     */
    @NotNull(message = "认证审核类型不能为空")
    @TableField(value = "brand_audit_type")
    private Integer brandAuditType;

    /** 商标分类:商标共有45个分类，请按商标实际分类上传 例：“1” */
    @NotBlank(message = "商标实际分类不能为空")
    @TableField(value = "trademark_type")
    private String trademarkType;

    /**
     * 品牌经营类型
     * 枚举值	描述
     * 1	自有品牌
     * 2	代理品牌
     * 3	无品牌
     */
    @NotNull(message = "品牌经营类型不能为空")
    @TableField(value = "brand_management_type")
    private Integer brandManagementType;

    /**
     * 商品产地是否进口
     * 枚举值	描述
     * 1	是
     * 2	否
     */
    @NotNull(message = "商品产地是否进口不能为空")
    @TableField(value = "commodity_origin_type")
    private Integer commodityOriginType;

    /** 商标/品牌词 */
    @NotBlank(message = "商标/品牌词不能为空")
    @TableField(value = "brand_wording")
    private String brandWording;

    /** 销售授权书（如商持人为自然人，还需提供有其签名的身份证正反面扫描件)，图片url/media_id */
    @TableField(value = "sale_authorization")
    private List<String> saleAuthorization;

    /** 商标注册证书，图片url/media_id */
    @TableField(value = "trademark_registration_certificate")
    private List<String> trademarkRegistrationCertificate;

    /** 商标变更证明，图片url/media_id */
    @TableField(value = "trademark_change_certificate")
    private List<String> trademarkChangeCertificate;

    /** 商标注册人姓名 */
    @TableField(value = "trademark_registrant")
    private String trademarkRegistrant;

    /** 商标注册号/申请号 */
    @TableField(value = "trademark_registrant_nu")
    private String trademarkRegistrantNu;

    /** 商标有效期，yyyy-MM-dd HH:mm:ss */
    @TableField(value = "trademark_authorization_period")
    private String trademarkAuthorizationPeriod;

    /** 商标注册申请受理通知书，图片url/media_id */
    @TableField(value = "trademark_registration_application")
    private List<String> trademarkRegistrationApplication;

    /** 商标申请人姓名 */
    @TableField(value = "trademark_applicant")
    private String trademarkApplicant;

    /** 商标申请时间, yyyy-MM-dd HH:mm:ss */
    @TableField(value = "trademark_application_time")
    private String trademarkApplicationTime;

    /** 中华人民共和国海关进口货物报关单，图片url/media_id */
    @TableField(value = "imported_goods_form")
    private List<String> importedGoodsForm;
}
