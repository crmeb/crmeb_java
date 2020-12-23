package com.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.exception.CrmebException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 导出工具类
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
public class ExportUtil {

    /**
     * 导出Excel文件
     * @param fileName  文件名
     * @param title     文件标题
     * @param voList    数据列表
     * @param aliasMap  别名Map（别名需要与数据列表的数据对应）
     * @return 返回给前端的文件名（路径+文件名）
     */
    public static String exportExecl(String fileName, String title, List<?> voList, LinkedHashMap<String, String> aliasMap) {
        if (StrUtil.isBlank(fileName)) {
            throw new CrmebException("文件名不能为空");
        }
        if (StrUtil.isBlank(title)) {
            throw new CrmebException("标题不能为空");
        }
        if (CollUtil.isEmpty(voList)) {
            throw new CrmebException("数据列表不能为空");
        }
        if (CollUtil.isEmpty(aliasMap)) {
            throw new CrmebException("别名map不能为空");
        }

        // 文件名部分
        String newFileName = UploadUtil.getWebPath() +  fileName;
        String filePath = UploadUtil.getServerPath();

        // 判断是否存在当前目录，不存在则创建
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        fileName = filePath.concat(fileName);

        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter(fileName);
//        ExcelWriter writer = ExcelUtil.getWriter("d:/writeMapTest.xlsx");
        CellStyle headCellStyle = writer.getHeadCellStyle();
        Font font = writer.createFont();
        font.setBold(true);
        headCellStyle.setFont(font);

        //自定义标题别名
        aliasMap.forEach((key, value) -> writer.addHeaderAlias(key, value));
        // 合并单元格后的标题行，使用默认标题样式
        writer.merge(aliasMap.size() - 1, title);
        writer.merge(aliasMap.size() - 1, StrUtil.format("生成时间:{}", DateUtil.nowDateTimeStr()));
        //设置宽度自适应
        writer.setColumnWidth(-1, 22);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(voList, true);
        // 关闭writer，释放内存
        writer.close();

        return newFileName;
    }

    /**
     * 上传部分设置
     */
    public static void setUpload(String rootPath, String modelPath, String type) {
        if (StrUtil.isBlank(rootPath) || StrUtil.isBlank(modelPath) || StrUtil.isBlank(type)) {
            throw new CrmebException("请检查上传参数，上传参数不能为空");
        }
        UploadUtil.setRootPath(rootPath);
        UploadUtil.setModelPath(modelPath);
        UploadUtil.setType(type);
    }


}
