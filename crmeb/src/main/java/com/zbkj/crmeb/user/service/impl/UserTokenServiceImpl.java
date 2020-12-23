package com.zbkj.crmeb.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constants.Constants;
import com.exception.CrmebException;
import com.zbkj.crmeb.user.dao.UserTokenDao;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserTokenServiceImpl 接口实现
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
@Service
public class UserTokenServiceImpl extends ServiceImpl<UserTokenDao, UserToken> implements UserTokenService {

    @Resource
    private UserTokenDao dao;

    @Autowired
    private UserService userService;

    /**
     * 检测token是否存在
     * @param token String openId
     * @param type int 类型
     * @author Mr.Zhang
     * @since 2020-05-25
     * @return UserToken
     */
    @Override
    public UserToken checkToken(String token, int type) {
        LambdaQueryWrapper<UserToken> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserToken::getType, type).eq(UserToken::getToken, token);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 绑定token关系
     * @param token String token
     * @param type int 类型
     * @author Mr.Zhang
     * @since 2020-05-25
     */
    @Override
    public void bind(String token, int type, Integer userId) {
        UserToken userToken = new UserToken();
        userToken.setToken(token);
        userToken.setType(type);
        userToken.setUid(userId);
        save(userToken);
    }

    /**
     * 解绑微信
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return Boolean
     */
    @Override
    public Boolean unBind(int type, Integer uid) {
        try{
            LambdaQueryWrapper<UserToken> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserToken::getUid, uid).eq(UserToken::getType, type);
            dao.delete(lambdaQueryWrapper);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public UserToken getTokenByUserId(Integer userId, int type) {
        LambdaQueryWrapper<UserToken> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserToken::getUid, userId).eq(UserToken::getType, type);
        return dao.selectOne(lambdaQueryWrapper);
    }

    @Override
    public UserToken getTokenByUserIdException(Integer userId, int type) {
        UserToken userToken = getTokenByUserId(userId, type);
        if(null == userToken){
            throw new CrmebException("没有找到和用户相关的微信数据");
        }

        User user = userService.getById(userId);

        if(null != user.getSubscribe() && !user.getSubscribe()){
            throw new CrmebException("此用户没有关注公众号");
        }

        return userToken;
    }

    @Override
    public List<UserToken> getList(List<Integer> userIdList) {
        LambdaQueryWrapper<UserToken> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(UserToken::getUid, userIdList).eq(UserToken::getType, Constants.PAY_TYPE_WE_CHAT_FROM_PUBLIC);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public UserToken getUserIdByOpenId(String openid, int type) {
        LambdaQueryWrapper<UserToken> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserToken::getToken, openid);
        lambdaQueryWrapper.eq(UserToken::getType, type);
        return dao.selectOne(lambdaQueryWrapper);
    }

    @Override
    public UserToken getByOpenid(String openid) {
        LambdaQueryWrapper<UserToken> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserToken::getToken, openid);
        return dao.selectOne(lambdaQueryWrapper);
    }

    @Override
    public UserToken getByUid(Integer uid) {
        LambdaQueryWrapper<UserToken> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserToken::getUid, uid);
        return dao.selectOne(lambdaQueryWrapper);
    }
}

