package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.user.model.UserToken;

import java.util.List;

/**
* @author Mr.Zhang
* @Description UserTokenService 接口
* @since 2020-04-28
*/
public interface UserTokenService extends IService<UserToken> {

    UserToken checkToken(String token, int type);

    void bind(String openId, int type, Integer uid);

    Boolean unBind(int type, Integer uid);

    UserToken getTokenByUserId(Integer userId, int type);

    UserToken getTokenByUserIdException(Integer userId, int type);

    List<UserToken> getList(List<Integer> userIdList);

    UserToken getUserIdByOpenId(String openid, int type);
}