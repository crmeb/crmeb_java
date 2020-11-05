package com.zbkj.crmeb.user.request;

import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Classname UserBillDetailList
 * @Description 用户操作记录查询
 * @Date 10/17/20 3:11 下午
 * @Created by stivepeim
 */
@Data
public class UserBillDetailListRequest {
    private String nickname; // 订单号或者推荐人昵称=备注模糊查询
    private String dateLimit; // 时间参数
    private String keywords;
}
