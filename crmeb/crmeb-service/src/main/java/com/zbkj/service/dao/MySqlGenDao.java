package com.zbkj.service.dao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MySqlGenDao {
    /**
     * Mysql 查询列表
     * @param map 表名称
     * @return 列表
     */
    List<Map<String, Object>> getList(Map<String, Object> map);

    /**
     * Mysql 查询获取表信息
     * @param tableName 表名称
     * @return 表信息
     */
    Map<String, String> getTable(String tableName);

    /**
     * mysql 获取列信息
     * @param tableName 表名称
     * @return 表对应的列信息
     */
    List<Map<String, String>> getColumns(String tableName);
}
