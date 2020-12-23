package com.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * 公共对象
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
public class MyRecord implements Serializable {

    private static final long serialVersionUID = 905784513600884082L;

    private static final Logger logger = LoggerFactory.getLogger(MyRecord.class);

    private Map<String, Object> columns;

    void setColumnsMap(Map<String, Object> columns) {
        this.columns = columns;
    }

    /**
     * Return columns map.
     */
    public Map<String, Object> getColumns() {
        if (null == columns) {
            columns = new HashMap<String, Object>();
        }
        return columns;
    }

    /**
     * Set columns value with map.
     *
     * @param columns the columns map
     */
    public MyRecord setColums(Map<String, Object> columns) {
        this.getColumns().putAll(columns);
        return this;
    }

    /**
     * Set columns value with MyRecord.
     *
     * @param record the MyRecord object
     */
    public MyRecord setColums(MyRecord record) {
        this.getColumns().putAll(record.getColumns());
        return this;
    }

    /**
     * Set columns value with JSONObject.
     *
     * @param jsonObject the MyRecord object
     */
    public MyRecord setColums(JSONObject jsonObject) {
        Map<String, Object> columns = this.getColumns();
        jsonObject.forEach(columns::put);
        return this;
    }

    /**
     * Set columns value with Model object.
     * @param t
     * @param <T>
     * @return
     */
    public <T> MyRecord setColums(T t) {
        Map<String, Object> columns = this.getColumns();

        String[] fieldNames = getFiledName(t);

        for (int i = 0; i < fieldNames.length; i++) {
            String name = fieldNames[i];
            if (!StringUtils.isEmpty(name) &&"serialVersionUID".equals(name)) {
                continue ;
            }
            Object value = getFieldValueByName(name, t);
            if (null != value) {
//                if (getFiledType(name, t).equals(Date.class)) {
//                    value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)value);
//                }
                columns.put(name, value);
            }
        }
        return this;
    }

    /**
     * Remove attribute of this myRecord.
     *
     * @param column the column name of the myRecord
     */
    public MyRecord remove(String column) {
        getColumns().remove(column);
        return this;
    }

    /**
     * Remove columns of this myRecord.
     *
     * @param columns the column names of the myRecord
     */
    public MyRecord remove(String... columns) {
        if (columns != null)
            for (String c : columns)
                this.getColumns().remove(c);
        return this;
    }

    /**
     * Remove columns if it is null.
     */
    public MyRecord removeNullValueColumns() {
        for (java.util.Iterator<Map.Entry<String, Object>> it = getColumns().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> e = it.next();
            if (e.getValue() == null) {
                it.remove();
            }
        }
        return this;
    }

    /**
     * Keep columns of this record and remove other columns.
     *
     * @param columns the column names of the record
     */
    public MyRecord keep(String... columns) {
        if (columns != null && columns.length > 0) {
            Map<String, Object> newColumns = new HashMap<String, Object>(columns.length);    // getConfig().containerFactory.getColumnsMap();
            for (String c : columns)
                if (this.getColumns().containsKey(c))    // prevent put null value to the newColumns
                    newColumns.put(c, this.getColumns().get(c));

            this.getColumns().clear();
            this.getColumns().putAll(newColumns);
        } else
            this.getColumns().clear();
        return this;
    }

    /**
     * Keep column of this record and remove other columns.
     *
     * @param column the column names of the record
     */
    public MyRecord keep(String column) {
        if (getColumns().containsKey(column)) {    // prevent put null value to the newColumns
            Object keepIt = getColumns().get(column);
            getColumns().clear();
            getColumns().put(column, keepIt);
        } else
            getColumns().clear();
        return this;
    }

    /**
     * Remove all columns of this record.
     */
    public MyRecord clear() {
        getColumns().clear();
        return this;
    }

    /**
     * Set column to record.
     *
     * @param column the column name
     * @param value  the value of the column
     */
    public MyRecord set(String column, Object value) {
        getColumns().put(column, value);
        return this;
    }

    /**
     * Set column to record.
     *
     * @param column the column name
     * @param valueList  the value of the column
     */
    public MyRecord set(String column, List<MyRecord> valueList) {
        List<HashMap<String, Object>> value = new ArrayList<>();
        valueList.forEach(o -> {
            HashMap<String, Object> va = new HashMap<>(o.getColumns());
            value.add(va);
        });
        getColumns().put(column, value);
        return this;
    }

    /**
     * Get column of any mysql type
     */
    public <T> T get(String column) {
        return (T) getColumns().get(column);
    }

    /**
     * Get column of any mysql type. Returns defaultValue if null.
     */
    public <T> T get(String column, Object defaultValue) {
        Object result = getColumns().get(column);
        return (T) (result != null ? result : defaultValue);
    }

    public Object getObject(String column) {
        return getColumns().get(column);
    }

    public Object getObject(String column, Object defaultValue) {
        Object result = getColumns().get(column);
        return result != null ? result : defaultValue;
    }

    /**
     * Get column of mysql type: varchar, char, enum, set, text, tinytext, mediumtext, longtext
     */
    public String getStr(String column) {
        // return (String)getColumns().get(column);
        Object s = getColumns().get(column);
        return s != null ? s.toString() : null;
    }

    /**
     * Get column of mysql type: int, integer, tinyint(n) n > 1, smallint, mediumint
     */
    public Integer getInt(String column) {
        Number n = getNumber(column);
        return n != null ? n.intValue() : null;
    }

    /**
     * Get column of mysql type: bigint
     */
    public Long getLong(String column) {
        Number n = getNumber(column);
        return n != null ? n.longValue() : null;
    }

    /**
     * Get column of mysql type: unsigned bigint
     */
    public java.math.BigInteger getBigInteger(String column) {
        return (java.math.BigInteger) getColumns().get(column);
    }

    /**
     * Get column of mysql type: date, year
     */
    public java.util.Date getDate(String column) {
        return (java.util.Date) getColumns().get(column);
    }

    /**
     * Get column of mysql type: time
     */
    public java.sql.Time getTime(String column) {
        return (java.sql.Time) getColumns().get(column);
    }

    /**
     * Get column of mysql type: timestamp, datetime
     */
    public java.sql.Timestamp getTimestamp(String column) {
        return (java.sql.Timestamp) getColumns().get(column);
    }

    /**
     * Get column of mysql type: real, double
     */
    public Double getDouble(String column) {
        Number n = getNumber(column);
        return n != null ? n.doubleValue() : null;
    }

    /**
     * Get column of mysql type: float
     */
    public Float getFloat(String column) {
        Number n = getNumber(column);
        return n != null ? n.floatValue() : null;
    }

    public Short getShort(String column) {
        Number n = getNumber(column);
        return n != null ? n.shortValue() : null;
    }

    public Byte getByte(String column) {
        Number n = getNumber(column);
        return n != null ? n.byteValue() : null;
    }

    /**
     * Get column of mysql type: bit, tinyint(1)
     */
    public Boolean getBoolean(String column) {
        return (Boolean) getColumns().get(column);
    }

    /**
     * Get column of mysql type: decimal, numeric
     */
    public BigDecimal getBigDecimal(String column) {
        Object n = getColumns().get(column);
        if (n instanceof BigDecimal) {
            return (BigDecimal) n;
        } else if (n != null) {
            return new BigDecimal(n.toString());
        } else {
            return null;
        }
    }

    /**
     * Get column of mysql type: binary, varbinary, tinyblob, blob, mediumblob, longblob
     * I have not finished the test.
     */
    public byte[] getBytes(String column) {
        return (byte[]) getColumns().get(column);
    }

    /**
     * Get column of any type that extends from Number
     */
    public Number getNumber(String column) {
        return (Number) getColumns().get(column);
    }

    public String toString() {
        if (columns == null) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        boolean first = true;
        for (Map.Entry<String, Object> e : getColumns().entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            Object value = e.getValue();
            if (value != null) {
                value = value.toString();
            }
            sb.append(e.getKey()).append(':').append(value);
        }
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (!(o instanceof MyRecord))
            return false;
        if (o == this)
            return true;
        return getColumns().equals(((MyRecord) o).getColumns());
    }

    public int hashCode() {
        return getColumns().hashCode();
    }

    /**
     * Return column names of this record.
     */
    public String[] getColumnNames() {
        Set<String> attrNameSet = getColumns().keySet();
        return attrNameSet.toArray(new String[attrNameSet.size()]);
    }

    /**
     * Return column values of this record.
     */
    public Object[] getColumnValues() {
        java.util.Collection<Object> attrValueCollection = getColumns().values();
        return attrValueCollection.toArray(new Object[attrValueCollection.size()]);
    }

    /**
     * Return json string of this record.
     */
    public String toJson() {
        return JSON.toJSONString(getColumns());
    }

    /**
     * @param o
     * @return
     * @desc 获取属性名数组
     */
    private static String[] getFiledName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * @param fieldName
     * @param o
     * @return
     * @desc 根据属性名获取属性值
     */
    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取属性值失败！" + e, e);
        }
        return null;
    }

    /**
     * @param fieldName
     * @param o
     * @return
     * @desc 获取属性的数据类型
     */
    private static Object getFiledType(String fieldName, Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(fieldName, field.getName())) {
                return field.getType();
            }
        }
        return null;
    }

}
