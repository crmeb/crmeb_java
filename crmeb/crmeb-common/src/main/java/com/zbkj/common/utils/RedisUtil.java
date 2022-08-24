package com.zbkj.common.utils;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 * redis工具类
 */

@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

//    private static RedisTemplate<String, Object> redisTemplate = SpringUtil.getBean("redisTemplate", RedisTemplate.class);

    // =============== common ==========================
    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return boolean
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效 失效时间为负数，说明该主键未设置失效时间（失效时间默认为-1）
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false 不存在
     */
    public boolean exists(String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除对应的value
     * @param key string key
     */
    public void delete(String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    // =============== string ==========================
    /**
     * 写入缓存
     * @param key string key
     * @param value string value
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 写入缓存带有效期(默认时间单位:秒)
     * @param key string key
     * @param value string value
     * @param expireTime Long 过期时间
     * @return boolean
     */
    public boolean set(String key, Object value, Long expireTime) {
        try {
            if (expireTime > 0) {
                redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 写入缓存带有效期
     * @param key string key
     * @param value string value
     * @param expireTime Long 过期时间
     * @param timeUnit TimeUnit 时间格式
     * @return boolean
     */
    public boolean set(String key, Object value, Long expireTime, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取缓存
     * @param key string key
     * @return T
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 哈希添加
     * @param key string key
     * @param hashKey Object hashKey
     * @param value Object value
     */

    public void hmSet(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 哈希删除
     * @param key string key
     * @param hashKey Object hashKey
     */

    public void hmDelete(String key, Object hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 哈希获取数据
     * @param key string key
     * @param hashKey Object hashKey
     */
    public Object hmGet(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 哈希数量
     * @param key string key
     */
    public Long getHashSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 列表添加左边添加
     * @param k string key
     * @param v Object v
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    public void lPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.leftPush(k, v);
    }

    /**
     * 从右边拿出来一个
     * @param k string key
     * @param t Long 超时秒数
     */
    public Object getRightPop(String k, Long t){
        return redisTemplate.opsForList().rightPop(k, t, TimeUnit.SECONDS);
    }

    /**
     * 列表获取数量
     * @param k string key
     * @return Long
     */
    public Long getListSize(String k) {
        return redisTemplate.opsForList().size(k);
    }

    /**
     * 列表获取
     * @param k string key
     * @param l long l
     * @param l1 long l1
     * @return List<Object>
     */
    public List<Object> lRange(String k, long l, long l1) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k, l, l1);
    }

    /**
     * 集合添加
     * @param key string key
     * @param value Object value
     */
    public void add(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * 集合获取
     * @param key string key
     * @return Set<Object>
     */
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加    排行榜使用
     * @param key string key
     * @param value Object value
     * @param score double scoure
     */
    public void zAdd(String key, Object value, double score) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, score);
    }


    /**
     * 有序集合获取    排行榜使用
     * @param key string key
     * @param score double scoure
     * @return Set<Object>
     */
    public Set<Object> rangeByScore(String key, double score, double score1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, score, score1);
    }

    /**
     * 递增
     * @param key 键
     * @return long
     */
    public long incrAndCreate(String key){
        if (!exists(key)) {
            set(key, 1);
            return 1;
        }
        return incr(key, 1L);
    }

    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return long
     */
    public long incr(String key, long delta){
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

}
