package com.zbkj.crmeb.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.system.dao.SystemAttachmentDao;
import com.zbkj.crmeb.system.model.SystemAttachment;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.upload.service.AsyncService;
import com.zbkj.crmeb.upload.vo.FileResultVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * SystemAttachmentServiceImpl 接口实现
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
@Service
public class SystemAttachmentServiceImpl extends ServiceImpl<SystemAttachmentDao, SystemAttachment>
        implements SystemAttachmentService {

    @Resource
    private SystemAttachmentDao dao;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private SystemConfigService systemConfigService;


    /**
     * 保存文件
     * @param file FileResultVo 文件信息
     * @param pid Integer
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    @Override
    public void create(FileResultVo file, Integer pid) {
        SystemAttachment systemAttachment = new SystemAttachment();
        systemAttachment.setName(file.getFileName());
        systemAttachment.setAttDir(file.getUrl());
        systemAttachment.setSattDir(file.getUrl());
        systemAttachment.setAttSize(file.getFileSize().toString());
        systemAttachment.setAttType(file.getType());
        systemAttachment.setImageType(1);   //图片上传类型 1本地 2七牛云 3OSS 4COS, 默认本地，任务轮询数据库放入云服务
        systemAttachment.setAttDir(file.getServerPath()); // 服务器上存储的绝对地址， 上传到云的时候使用
        systemAttachment.setPid(pid);
        save(systemAttachment);
    }

    /**
     * 查询本地文件，同步到云服务
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    @Override
    @Transactional
    public void async() {
        String uploadType = systemConfigService.getValueByKeyException("uploadType");
        if(Integer.parseInt(uploadType) <= 1){
            //本地存储
            return;
        }

        List<SystemAttachment> asyncList = getAsyncList();
        if(asyncList.size() < 1){
            //没有需要处理的数据
            return;
        }

        asyncService.async(asyncList);
    }

    /**
     * 查询本地文件，同步到云服务
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    private List<SystemAttachment> getAsyncList() {
        PageHelper.startPage(Constants.DEFAULT_PAGE, Constants.DEFAULT_LIMIT);
        LambdaQueryWrapper<SystemAttachment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemAttachment::getImageType, 1);
        lambdaQueryWrapper.isNotNull(SystemAttachment::getAttDir);
        lambdaQueryWrapper.orderByDesc(SystemAttachment::getAttId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 同步到云服务， 更新图片上传类型
     * @param attId Integer 主键id
     * @param type int 图片上传类型 1本地 2七牛云 3OSS 4COS
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    @Override
    public void updateCloudType(Integer attId, int type) {
        SystemAttachment systemAttachment = new SystemAttachment();
        systemAttachment.setAttId(attId);
        systemAttachment.setImageType(type);
        updateById(systemAttachment);
    }

    /**
     * 附件分页
     * @param pid Integer pid
     * @param pageParamRequest PageParamRequest 分页参数
     * @return List<SystemAttachment>
     */
    @Override
    public List<SystemAttachment> getList(Integer pid, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SystemAttachment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemAttachment::getPid, pid);
        lambdaQueryWrapper.orderByDesc(SystemAttachment::getAttId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 给图片加前缀
     * @param path String 路径
     * @return String
     */
    @Override
    public String prefixImage(String path) {
        //TODO 如果那些域名不需要加，则跳过
        return path.replace("image/", getCdnUrl() + "/image/");
    }

    /**
     * 给文件加前缀
     * @param path String 路径
     * @return String
     */
    @Override
    public String prefixFile(String path) {
        return path.replace("file/", getCdnUrl() + "/file/");
    }

    /**
     * 获取cdn url
     * @return String
     */
    private String getCdnUrl() {
        return asyncService.getCurrentBaseUrl();
    }

    /**
     * 清除 cdn url， 在保存数据的时候使用
     * @param path String 文件路径
     * @return String
     */
    @Override
    public String clearPrefix(String path) {
        if(StringUtils.isBlank(path)){
            return path;
        }
        Constants.CND_URL = asyncService.getCurrentBaseUrl();
        if(path.contains(getCdnUrl() + "/")){
            return path.replace(getCdnUrl() + "/", "");
        }

        return path;
    }

    /**
     * 附件基本查询
     * @param systemAttachment 附件参数
     * @return 附件
     */
    @Override
    public List<SystemAttachment> getByEntity(SystemAttachment systemAttachment) {
        LambdaQueryWrapper<SystemAttachment> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(systemAttachment);
        return dao.selectList(lqw);
    }
}

