package com.zbkj.crmeb.export.service.impl;

import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.export.service.ExcelService;
import com.zbkj.crmeb.export.vo.ProductExcelVo;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.service.StoreProductRelationService;
import com.zbkj.crmeb.store.service.StoreProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
*  ExcelServiceImpl 接口实现
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
@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;


    /**
     * 商品列表导出
     * @param request 请求参数
     * @param response 返回数据
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @Override
    public List<ProductExcelVo> product(StoreProductSearchRequest request, HttpServletResponse response) {

        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(Constants.DEFAULT_PAGE);
        pageParamRequest.setLimit(Constants.EXPORT_MAX_LIMIT);
        PageInfo<StoreProductResponse> storeProductResponsePageInfo = storeProductService.getList(request, pageParamRequest);
        List<StoreProductResponse> list = storeProductResponsePageInfo.getList();
        if(list.size() < 1){
            throw new CrmebException("没有可导出的数据！");
        }

        //从集合中取出ID
        List<Integer> idList = list.stream().map(StoreProductResponse::getId).distinct().collect(Collectors.toList());

        //产品分类id
        List<String> cateIdListStr = list.stream().map(StoreProductResponse::getCateId).distinct().collect(Collectors.toList());

        HashMap<Integer, String> categoryNameList = new HashMap<Integer, String>();
        if(cateIdListStr.size() > 0){
            String join = StringUtils.join(cateIdListStr, ",");
            List<Integer> cateIdList = CrmebUtil.stringToArray(join);
            categoryNameList = categoryService.getListInId(cateIdList);
        }



        //点赞
        HashMap<Integer, Integer> likeCount = storeProductRelationService.getLikeCountListInProductId(idList);

        //收藏
        HashMap<Integer, Integer> collectCount = storeProductRelationService.getCollectCountListInProductId(idList);

        ArrayList<ProductExcelVo> productExcelVoList = new ArrayList<>();
        for (StoreProductResponse storeProductResponse : list ) {
            productExcelVoList.add(
                    new ProductExcelVo(
                            storeProductResponse.getStoreName(),
                            storeProductResponse.getStoreInfo(),
                            CrmebUtil.getValueByIndex(categoryNameList, storeProductResponse.getCateId()),
                            storeProductResponse.getPrice(),
                            storeProductResponse.getSales(),
                            storeProductResponse.getStock(),
                            CrmebUtil.getIntValueByIndex(likeCount, storeProductResponse.getId()),
                            CrmebUtil.getIntValueByIndex(collectCount, storeProductResponse.getId())
                    )
            );
        }
        return productExcelVoList;
    }
}

