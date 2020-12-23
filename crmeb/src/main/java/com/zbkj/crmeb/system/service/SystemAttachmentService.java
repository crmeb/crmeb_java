package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemAttachment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.upload.vo.FileResultVo;

import java.util.List;

/**
 * SystemAttachmentService 接口
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
public interface SystemAttachmentService extends IService<SystemAttachment> {
    void create(FileResultVo file, Integer pid);

    void async();

    void updateCloudType(Integer attId, int type);

    List<SystemAttachment> getList(Integer pid,PageParamRequest pageParamRequest);

    String prefixImage(String path);

    String prefixFile(String str);

    String clearPrefix(String attribute);

    /**
     * 附件基本查询
     * @param systemAttachment 附件参数
     * @return 附件
     */
    List<SystemAttachment> getByEntity(SystemAttachment systemAttachment);
}
