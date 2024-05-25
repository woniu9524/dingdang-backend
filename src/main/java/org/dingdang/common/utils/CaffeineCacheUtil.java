package org.dingdang.common.utils;

/**
 * @Author: zhangcheng
 * @Date: 2024/5/5 17:34 星期日
 * @Description:
 */
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class CaffeineCacheUtil {
    private static final Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)  // 设置写入30分钟后过期
            .maximumSize(10000)                      // 设置最大缓存项数为10000
            .build();

    /**
     * 存储数据到缓存中。
     * @param key 缓存键，唯一标识一个缓存项。
     * @param value 缓存值。
     */
    public static void put(String key, Object value) {
        cache.put(key, value);
    }

    /**
     * 从缓存中获取数据。
     * @param key 缓存键。
     * @return 返回缓存中的对象，如果缓存中没有对应的数据则返回null。
     */
    public static Object get(String key) {
        return cache.getIfPresent(key);
    }

    /**
     * 从缓存中移除数据。
     * @param key 缓存键。
     */
    public static void remove(String key) {
        cache.invalidate(key);
    }

    /**
     * 清除所有缓存项。
     */
    public static void clearAll() {
        cache.invalidateAll();
    }
}
