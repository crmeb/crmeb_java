package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.utils.CrmebDateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 活动背景 response
 * @Auther: 大粽子
 * @Date: 2023/1/9 16:17
 * @Description: 描述对应的业务场景
 */
@Data
public class ActivityStyleResponse {
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "活动名称")
    private String name;

    @ApiModelProperty(value = "活动类型0=活动边框，1=活动背景")
    private Boolean type;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date starttime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endtime;

    @ApiModelProperty(value = "活动边框素材地址")
    private String style;

    @ApiModelProperty(value = "状态 -1=已结束，0=未开始，1=进行中")
    private Integer runningStatus;

    @ApiModelProperty(value = "状态 0=关闭1=开启")
    private Boolean status;

    @ApiModelProperty(value = "商品参与类型 0=全部商品参与，1=指定商品参与，2=指定品牌参与，3=指定商品分类参与，4=指定商户产品")
    private Integer method;

    @ApiModelProperty(value = "参与活动商品")
    private String products;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createtime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updatetime;

    /**
     * -1 已结束 结束时间小于当前时间
     * 1 进行中 结束时间大于当前时间 && 开始时间小于等于当前时间
     * 0 未开始 开始时间大于当前时间
     * @param starttime 开始时间
     * @param endtime 结束时间
     */
    public void setRunningStatus(Date starttime, Date endtime, Date currenttime) {
        // isEnd = -1 = 已结束
        int isEnd = CrmebDateUtil.compareDate(CrmebDateUtil.dateToStr(endtime, DateConstants.DATE_FORMAT),
                CrmebDateUtil.dateToStr(currenttime, DateConstants.DATE_FORMAT),
                DateConstants.DATE_FORMAT);
        // isIng1 = 1 和下面并且链接
        int isIng1 = CrmebDateUtil.compareDate(CrmebDateUtil.dateToStr(endtime, DateConstants.DATE_FORMAT),
                CrmebDateUtil.dateToStr(currenttime, DateConstants.DATE_FORMAT),
                DateConstants.DATE_FORMAT);
        // isIng2 = -1 和上面并且链接
        int isIng2 = CrmebDateUtil.compareDate(CrmebDateUtil.dateToStr(starttime, DateConstants.DATE_FORMAT),
                CrmebDateUtil.dateToStr(currenttime, DateConstants.DATE_FORMAT),
                DateConstants.DATE_FORMAT);
        // isPer = 1 = 未开始
        int isPer = CrmebDateUtil.compareDate(CrmebDateUtil.dateToStr(starttime, DateConstants.DATE_FORMAT),
                CrmebDateUtil.dateToStr(currenttime, DateConstants.DATE_FORMAT),
                DateConstants.DATE_FORMAT);
        Integer tempRunningStatus = null;
        if(isEnd == -1){
            tempRunningStatus = -1;
        }else if(isPer == 1){
            tempRunningStatus = 0;
        }else if(isIng1 == 1 && isIng2 == -1){
            tempRunningStatus = 1;
        }
        this.runningStatus = tempRunningStatus;
    }
}
