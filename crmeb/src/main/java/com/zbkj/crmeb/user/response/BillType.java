package com.zbkj.crmeb.user.response;

import lombok.Data;

/**
 * @Classname BillType
 * @Description 资金操作类型
 * @Date 9/28/20 6:27 下午
 * @Created by stivepeim
 */
@Data
public class BillType {
    public BillType(String title, String type) {
        this.title = title;
        this.type = type;
    }

    private String title;
    private String type;
}
