package com.zbkj.crmeb.front.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.front.response.IndexInfoResponse;
import com.zbkj.crmeb.front.response.IndexProductBannerResponse;

import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @Description IndexService 接口
* @since 2020-04-13
*/
public interface IndexService{
    IndexProductBannerResponse getProductBanner(int type, PageParamRequest pageParamRequest);

    IndexInfoResponse getIndexInfo();

    List<HashMap<String, Object>> hotKeywords();

    HashMap<String, String> getShareConfig();
}