package com.zbkj.crmeb.export.service;

import com.zbkj.crmeb.bargain.request.StoreBargainSearchRequest;
import com.zbkj.crmeb.combination.request.StoreCombinationSearchRequest;
import com.zbkj.crmeb.export.vo.BargainProductExcelVo;
import com.zbkj.crmeb.export.vo.ProductExcelVo;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
* StoreProductService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface ExcelService{
//    List<ProductExcelVo> product(StoreProductSearchRequest request, HttpServletResponse response);

    String exportBargainProduct(StoreBargainSearchRequest request, HttpServletResponse response);

    String exportCombinationProduct(StoreCombinationSearchRequest request, HttpServletResponse response);

    String exportProduct(StoreProductSearchRequest request, HttpServletResponse response);
}
