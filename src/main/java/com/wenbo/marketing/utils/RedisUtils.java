package com.wenbo.marketing.utils;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author changwenbo
 * @date 2024/8/23 17:42
 */
public final class RedisUtils {
    private RedisUtils() {}

    private static final long HALF_HOURS = 30 * 60 * 1000L;

    private static final long ONE_HOURS = 60 * 60 * 1000L;

    public static void set(String key, String val, StringRedisTemplate redis) {
        redis.opsForValue().set(key, val);
    }

    public static void setEx(String key, String val, long time, StringRedisTemplate redis) {
        redis.opsForValue().set(key, val, time, TimeUnit.MILLISECONDS);
    }

    public static String get(String key, StringRedisTemplate redis) {
        return redis.opsForValue().get(key);
    }

    public static void del(String key, StringRedisTemplate redis) {
        redis.delete(key);
    }

    public static Long decr(String key, StringRedisTemplate redis) {
        return redis.opsForValue().decrement(key);
    }

    public static Long incr(String key, StringRedisTemplate redis) {
        return redis.opsForValue().increment(key);
    }

    public static <T> T tryLock(String key, RedissonClient redissonClient, Supplier<T> supplier) {
        RLock lock = null;
        try {
            lock = redissonClient.getLock(key);
            if (lock.tryLock()) {
                return supplier.get();
            }
            throw new RuntimeException("try lock error key = " + key);
        }

        catch (RuntimeException e) {
            throw e;
        }

        catch (Exception e) {
            throw new RuntimeException("business process error", e);
        }

        finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
