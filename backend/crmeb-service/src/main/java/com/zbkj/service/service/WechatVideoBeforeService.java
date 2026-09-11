package com.zbkj.service.service;

import com.zbkj.common.request.ShopUploadImgRequest;
import com.zbkj.common.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

/**
 * 接入商品前必须接口
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
public interface WechatVideoBeforeService {
    // 获取类目详情 在其他商品中实现了

    /**
     * 上传图片
     * @return 图片上传结果
     */
    WechatVideoUploadImageResponseVo shopImgUpload(ShopUploadImgRequest request);

    /**
     * 上传品牌信息
     * @param requestVo 待上传品牌参数
     * @return 审核单Id
     */
    ShopAuditBrandResponseVo shopAuditBrand(ShopAuditBrandRequestVo requestVo);

    /**
     * 上传类目资质
     * @param requestVo 类目资质参数
     * @return 类目资质结果
     */
    ShopAuditCategoryResponseVo shopAuditCategory(ShopAuditCategoryRequestVo requestVo);

    /**
     * 获取类目审核结果
     * @param request 待审核类目id
     * @return 审核结果
     */
    ShopAuditResultResponseVo shopAuditResult(HashMap<String,String> request);

    // 待完善
    void shopAuditGetMinCertificate(HashMap<String,String> request);


    WechatVideoUploadImageResponseVo shopImgUploadTest(MultipartFile file, Integer respType, Integer uploadType, String imgUrl);
}
