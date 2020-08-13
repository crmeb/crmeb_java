package com.zbkj.crmeb.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.DateUtil;
import com.zbkj.crmeb.finance.dao.UserExtractDao;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.finance.request.UserExtractSearchRequest;
import com.zbkj.crmeb.finance.response.BalanceResponse;
import com.zbkj.crmeb.finance.response.UserExtractResponse;
import com.zbkj.crmeb.finance.service.UserExtractService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;

/**
* @author Mr.Zhang
* @description UserExtractServiceImpl 接口实现
* @date 2020-05-11
*/
@Service
public class UserExtractServiceImpl extends ServiceImpl<UserExtractDao, UserExtract> implements UserExtractService {

    @Resource
    private UserExtractDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private SystemConfigService systemConfigService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-11
    * @return List<UserExtract>
    */
    @Override
    public List<UserExtract> getList(UserExtractSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 UserExtract 类的多条件查询
        LambdaQueryWrapper<UserExtract> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i.
                    or().like(UserExtract::getWechat, request.getKeywords()).   //微信号
                    or().like(UserExtract::getRealName, request.getKeywords()). //名称
                    or().eq(UserExtract::getBankCode, request.getKeywords()). //银行卡
                    or().like(UserExtract::getBankAddress, request.getKeywords()). //开户行
                    or().eq(UserExtract::getAlipayCode, request.getKeywords()). //支付宝
                    or().like(UserExtract::getFailMsg, request.getKeywords()) //失败原因
            );
        }

        //提现状态
        if(request.getStatus() != null && request.getStatus() >= 0){
            lambdaQueryWrapper.eq(UserExtract::getStatus, request.getStatus());
        }

        //提现方式
        if(!StringUtils.isBlank(request.getExtractType())){
            lambdaQueryWrapper.eq(UserExtract::getExtractType, request.getExtractType());
        }

        //时间范围
        if(request.getStartTime() != null && request.getEndTime() != null){
            //判断时间
            int compareDateResult = DateUtil.compareDate(request.getEndTime(), request.getStartTime(), Constants.DATE_FORMAT);
            if(compareDateResult == -1){
                throw new CrmebException("开始时间不能大于结束时间！");
            }

            lambdaQueryWrapper.between(UserExtract::getCreateTime, request.getStartTime(), request.getEndTime());
        }

        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 提现总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BalanceResponse
     */
    @Override
    public BalanceResponse getBalance(Integer userId) {
        BigDecimal withdrawn = getWithdrawn(userId);//已提现
        BigDecimal unDrawn = getUnDrawn(userId);//审核中现
        BigDecimal commissionTotal = userService.getInfo().getBrokeragePrice();//佣金总金额
        //用户总金额 - 已提现 - 审核中
        BigDecimal toBeWithdrawn = commissionTotal.subtract(withdrawn).subtract(unDrawn); //待提现

        return new BalanceResponse(withdrawn, unDrawn, commissionTotal, toBeWithdrawn);
    }


    /**
     * 提现总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BalanceResponse
     */
    @Override
    public BigDecimal getWithdrawn(Integer userId) {
        return getSum(userId, 1);
    }

    /**
     * 审核中总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BalanceResponse
     */
    @Override
    public BigDecimal getUnDrawn(Integer userId) {
        return getSum(userId, 0);
    }

    /**
     * 提现申请
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return Boolean
     */
    @Override
    public Boolean create(UserExtractRequest request, Integer userId) {
        //看是否有足够的金额可提现
        BigDecimal toBeWithdrawn = userService.getById(userId).getBrokeragePrice();//提现总金额
        BigDecimal freeze = getFreeze(userId); //冻结的佣金
        BigDecimal money = toBeWithdrawn.subtract(freeze); //可提现总金额

        if(money.compareTo(ZERO) < 1){
            throw new CrmebException("您当前没有金额可以提现");
        }

        int result = money.compareTo(request.getExtractPrice());
        if(result < 0){
            throw new CrmebException("你当前最多可提现 " + toBeWithdrawn + "元");
        }
        UserExtract userExtract = new UserExtract();
        userExtract.setUid(userId);
        BeanUtils.copyProperties(request, userExtract);
        userExtract.setBalance(toBeWithdrawn.subtract(request.getExtractPrice()));
        return save(userExtract);
    }


//    /**
//     * 可提现总金额
//     * @author Mr.Zhang
//     * @since 2020-06-08
//     * @return Boolean
//     */
//    @Override
//    public BigDecimal getToBeWihDraw(Integer userId) {
//        //可提现佣金
//        //返佣 +
//        BigDecimal withDrawable = userBillService.getSumBigDecimal(1, userId, Constants.USER_BILL_CATEGORY_MONEY, Constants.SEARCH_DATE_LATELY_30, Constants.USER_BILL_TYPE_BROKERAGE);
//
//        //退款退的佣金 -
//        BigDecimal refund = userBillService.getSumBigDecimal(0, userId, Constants.USER_BILL_CATEGORY_MONEY, Constants.SEARCH_DATE_LATELY_30, Constants.USER_BILL_TYPE_BROKERAGE);
//
//        BigDecimal subtract = withDrawable.subtract(refund);
//        subtract = (subtract.compareTo(ZERO) < 1) ? ZERO : subtract;
//
//        //用户累计佣金
//        BigDecimal brokeragePrice = userService.getById(userId).getBrokeragePrice();
//
//        //可提现佣金
//        return brokeragePrice.subtract(subtract);
//    }

    /**
     * 冻结的佣金
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return Boolean
     */
    @Override
    public BigDecimal getFreeze(Integer userId) {
        //冻结时间
        //查看是否在冻结期之内， 如果在是需要回滚的，如果不在则不需要回滚
        String time = systemConfigService.getValueByKey(Constants.CONFIG_KEY_STORE_BROKERAGE_EXTRACT_TIME);

        String date = null;
        if(StringUtils.isNotBlank(time)){
            String startTime = DateUtil.nowDateTime(Constants.DATE_FORMAT);
            String endTime = DateUtil.addDay(DateUtil.nowDateTime(), Integer.parseInt(time), Constants.DATE_FORMAT);
            date = startTime + "," + endTime;
        }

        //在此期间获得的佣金
        BigDecimal getSum = userBillService.getSumBigDecimal(1, userId, Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, null, date);

        //在此期间消耗的佣金
        BigDecimal subSum = userBillService.getSumBigDecimal(0, userId, Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, null, date);

        //冻结的佣金
        return getSum.subtract(subSum);
    }

    /**
     * 总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BigDecimal
     */
    private BigDecimal getSum(Integer userId, int status) {
        QueryWrapper<UserExtract> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(extract_price) as extract_price");
        if(userId != null){
            queryWrapper.eq("uid", userId);
        }
        queryWrapper.eq("status", status);
        UserExtract userExtract = dao.selectOne(queryWrapper);
        if(null == userExtract){
            return ZERO;
        }
        return userExtract.getExtractPrice();
    }

    /**
     * 获取用户对应的提现数据
     * @param userId 用户id
     * @return 提现数据
     */
    @Override
    public UserExtractResponse getUserExtractByUserId(Integer userId) {
        QueryWrapper<UserExtract> qw = new QueryWrapper<>();
        qw.select("SUM(extract_price) as extract_price,count(id) as id, uid");
        qw.ge("status", 1);
        qw.eq("uid",userId);
        qw.groupBy("uid");
        UserExtract ux = dao.selectOne(qw);
        UserExtractResponse uexr = new UserExtractResponse();
//        uexr.setEuid(ux.getUid());
        if(null != ux){
            uexr.setExtractCountNum(ux.getId()); // 这里的id其实是数量，借变量传递
            uexr.setExtractCountPrice(ux.getExtractPrice());
        }else{
            uexr.setExtractCountNum(0); // 这里的id其实是数量，借变量传递
            uexr.setExtractCountPrice(ZERO);
        }

        return uexr;
    }

    /**
     * 根据用户id集合获取对应提现用户集合
     * @param userIds 用户id集合
     * @return 提现用户集合
     */
    @Override
    public List<UserExtract> getListByUserIds(List<Integer> userIds) {
        LambdaQueryWrapper<UserExtract> lqw = new LambdaQueryWrapper<>();
        lqw.in(UserExtract::getUid, userIds);
        return dao.selectList(lqw);
    }
}

