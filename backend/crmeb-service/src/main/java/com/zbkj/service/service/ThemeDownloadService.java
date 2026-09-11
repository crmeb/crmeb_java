package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.theme.ThemeDownload;

/**
 * 主题下载记录服务
 */
public interface ThemeDownloadService extends IService<ThemeDownload> {

    /**
     * 异步导出主题并回写下载地址。
     *
     * @param recordId 下载记录ID
     * @param themeId 主题ID
     */
    void exportThemeDownload(Integer recordId, Integer themeId);
}
