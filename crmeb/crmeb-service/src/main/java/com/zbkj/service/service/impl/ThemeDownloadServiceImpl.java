package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.theme.ThemeDownload;
import com.zbkj.service.dao.ThemeDownloadDao;
import com.zbkj.service.service.ThemeDownloadService;
import com.zbkj.service.service.ThemeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 主题下载记录服务实现
 */
@Slf4j
@Service
public class ThemeDownloadServiceImpl extends ServiceImpl<ThemeDownloadDao, ThemeDownload> implements ThemeDownloadService {

    @Lazy
    @Autowired
    private ThemeService themeService;

    /**
     * 异步导出主题并回写下载地址。
     *
     * @param recordId 下载记录ID
     * @param themeId 主题ID
     */
    @Async
    @Override
    public void exportThemeDownload(Integer recordId, Integer themeId) {
        try {
            String downloadUrl = themeService.exportTheme(themeId);
            ThemeDownload update = new ThemeDownload();
            update.setId(recordId);
            update.setDownloadUrl(downloadUrl);
            updateById(update);
        } catch (Exception e) {
            log.error("主题异步导出失败，recordId={}, themeId={}", recordId, themeId, e);
        }
    }
}
