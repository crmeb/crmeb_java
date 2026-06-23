package com.zbkj.admin.service;

/**
 * 小程序源码下载 Interface
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface WeChatMiniCodeDownloadService {

    /**
     * 小程序源码下载
     * @return 源码压缩包路径
     */
    String WeChatMiniCodeDownload();
}
