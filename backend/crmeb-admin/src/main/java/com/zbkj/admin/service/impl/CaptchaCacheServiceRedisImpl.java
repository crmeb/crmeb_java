package com.zbkj.admin.service.impl;

import com.anji.captcha.service.CaptchaCacheService;
import com.zbkj.common.utils.RedisUtil;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 对于分布式部署的应用，我们建议应用自己实现CaptchaCacheService，比如用Redis，参考service/spring-boot代码示例。
 * 如果应用是单点的，也没有使用redis，那默认使用内存。
 * 内存缓存只适合单节点部署的应用，否则验证码生产与验证在节点之间信息不同步，导致失败。
 *
 * ☆☆☆ SPI： 在resources目录新建META-INF.services文件夹(两层)，参考当前服务resources。
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/6/13
 */
public class CaptchaCacheServiceRedisImpl implements CaptchaCacheService {

    @Resource
    private RedisUtil redisUtil;


    @Override
    public String type() {
        return "redis";
    }


    @Override
    public void set(String key, String value, long expiresInSeconds) {
        redisUtil.getRedisTemplate().opsForValue().set(key, value, expiresInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisUtil.getRedisTemplate().hasKey(key));
    }

    @Override
    public void delete(String key) {
        redisUtil.getRedisTemplate().delete(key);
    }

    @Override
    public String get(String key) {
        return (String) redisUtil.getRedisTemplate().opsForValue().get(key);
    }

    @Override
    public Long increment(String key, long val) {
        return redisUtil.getRedisTemplate().opsForValue().increment(key,val);
    }
}
