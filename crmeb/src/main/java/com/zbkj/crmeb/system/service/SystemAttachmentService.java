package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemAttachment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.upload.vo.FileResultVo;

import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemAttachmentService 接口
* @since 2020-05-08
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
