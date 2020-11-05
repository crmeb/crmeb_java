package com.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 * redis工具类
 */

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;



    /**
     * 写入缓存
     * @param key string key
     * @param value string value
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return bool
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存带有效期
     * @param key string key
     * @param value string value
     * @param expireTime Long 过期时间
     * @param timeUnit TimeUnit 时间格式
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return bool
     */
    public boolean set(final String key, Object value, Long expireTime, TimeUnit timeUnit) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, timeUnit);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 批量删除对应的value
     * @param keys string key
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     * @param pattern string key
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 删除对应的value
     * @param key string key
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }


    /**
     * 判断缓存中是否有对应的value
     * @param key string key
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return bool
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }


    /**
     * 读取缓存
     * @param key string key
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return Object
     */

    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 哈希 添加
     *
     * @param key
     * @param hashKey
     * @param value
     */

    /**
     * 哈希添加
     * @param key string key
     * @param hashKey Object hashKey
     * @param value Object value
     * @author Mr.Zhang
     * @since 2020-04-13
     */

    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 哈希删除
     * @param key string key
     * @param hashKey Object hashKey
     * @author Mr.Zhang
     * @since 2020-04-13
     */

    public void hmDelete(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.delete(key, hashKey);
    }

    /**
     * 哈希获取数据
     * @param key string key
     * @param hashKey Object hashKey
     * @author Mr.Zhang
     * @return Object
     * @since 2020-04-13
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * 哈希数量
     * @param key string key
     * @author Mr.Zhang
     * @return Object
     * @since 2020-04-13
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
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    public Object getRightPop(String k, Long t){
        return redisTemplate.opsForList().rightPop(k, t, TimeUnit.SECONDS);
    }

    /**
     * 列表获取数量
     * @param k string key
     * @author Mr.Zhang
     * @since 2020-04-13
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
     * @author Mr.Zhang
     * @since 2020-04-13
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
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    public void add(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * 集合获取
     * @param key string key
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return Set<Object>
     */
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     *
     * @param key
     * @param value
     * @param scoure
     */

    /**
     * 有序集合添加    排行榜使用
     * @param key string key
     * @param value Object value
     * @param score double scoure
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    public void zAdd(String key, Object value, double score) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, score);
    }


    /**
     * 有序集合获取    排行榜使用
     * @param key string key
     * @param score double scoure
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return Set<Object>
     */
    public Set<Object> rangeByScore(String key, double score, double score1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, score, score1);
    }
}
