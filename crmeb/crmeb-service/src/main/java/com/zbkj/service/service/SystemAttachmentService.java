package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.model.system.SystemAttachment;
import com.zbkj.common.request.SystemAttachmentMoveRequest;
import com.zbkj.common.request.SystemAttachmentRequest;

import java.util.List;

/**
 * SystemAttachmentService 接口
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
public interface SystemAttachmentService extends IService<SystemAttachment> {

    /**
     * 同步到云服务， 更新图片上传类型
     * @param attId Integer 主键id
     * @param type int 图片上传类型 1本地 2七牛云 3OSS 4COS
     */
    void updateCloudType(Integer attId, int type);

    /**
     * 附件分页
     * @param pid Integer pid
     * @param attType 格式png,jpeg,jpg,audio/mpeg,text/plain,video/mp4,gif
     * @param pageParamRequest PageParamRequest 分页参数
     * @return List<SystemAttachment>
     */
    List<SystemAttachment> getList(Integer pid, String attType, PageParamRequest pageParamRequest);

    /**
     * 给图片加前缀
     * @param path String 路径
     * @return String
     */
    String prefixImage(String path);

    /**
     * 给文件加前缀
     * @param path String 路径
     * @return String
     */
    String prefixFile(String path);

    /**
     * 清除 cdn url， 在保存数据的时候使用
     * @param path String 文件路径
     * @return String
     */
    String clearPrefix(String path);

    /**
     * 新增附件
     * @param systemAttachmentRequest 新增参数
     */
    Boolean add(SystemAttachmentRequest systemAttachmentRequest);

    /**
     * 编辑附件
     * @param systemAttachmentRequest 更新参数
     */
    Boolean edit(SystemAttachmentRequest systemAttachmentRequest);

    /**
     * 更改图片目录
     * @param move 参数
     */
    Boolean updateAttrId(SystemAttachmentMoveRequest move);
}
