package com.zbkj.crmeb.export.service;

import com.zbkj.crmeb.export.vo.ProductExcelVo;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
* @author Mr.Zhang
* @Description StoreProductService 接口
* @since 2020-05-06
*/
public interface ExcelService{
    List<ProductExcelVo> product(StoreProductSearchRequest request, HttpServletResponse response);
}
