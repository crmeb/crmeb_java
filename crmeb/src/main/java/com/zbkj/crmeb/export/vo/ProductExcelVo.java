package com.zbkj.crmeb.export.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProductExcelVo对象", description = "产品导出")
public class ProductExcelVo implements Serializable {

    private static final long serialVersionUID=1L;
    public ProductExcelVo() {}
    public ProductExcelVo(String storeName, String storeInfo, String cateName, BigDecimal price, Integer sales, Integer stock, Integer like, Integer collect) {
        this.storeName = storeName;
        this.storeInfo = storeInfo;
        this.cateName = cateName;
        this.price = price;
        this.sales = sales;
        this.stock = stock;
        this.like = like;
        this.collect = collect;
    }

    @ExcelColumn(value = "商品名称", col = 1)
    private String storeName;

    @ExcelColumn(value = "商品简介", col = 2)
    private String storeInfo;

    @ExcelColumn(value = "分类", col = 3)
    private String cateName;

    @ExcelColumn(value = "商品价格", col = 4)
    private BigDecimal price;

    @ExcelColumn(value = "销量", col = 5)
    private Integer sales;

    @ExcelColumn(value = "库存", col = 6)
    private Integer stock;

    @ExcelColumn(value = "点赞人数", col = 7)
    private Integer like;

    @ExcelColumn(value = "收藏人数", col = 8)
    private Integer collect;

}
