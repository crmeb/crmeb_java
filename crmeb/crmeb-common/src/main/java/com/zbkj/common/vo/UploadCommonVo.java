package com.zbkj.common.vo;

import lombok.Data;

/**
 * 文件公共上传对象
 *
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
@Data
public class UploadCommonVo {

    //服务器存储地址
    private String rootPath;

    //类型
    private String type;

    //模块
    private String modelPath;

    //扩展名
    private String extStr;

    //文件大小上限
    private int size;

}
