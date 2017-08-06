package org.graceframework.util;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Tong on 2017/7/31.
 * 弱引用 缓存自动清理
 */
public class AutoCleanCache<K,V> {

    private final Map<K, V> cache = new WeakHashMap<>();

    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = cacheLock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = cacheLock.writeLock();

    /**
     * 从缓存池中查找值
     *
     * @param key 键
     * @return 值
     */
    public V get(K key) {
        // 尝试读取缓存
        readLock.lock();
        V value;
        try {
            value = cache.get(key);
        } finally {
            readLock.unlock();
        }
        return value;
    }

    /**
     * 放入缓存
     * @param key 键
     * @param value 值
     * @return 值
     */
    public V put(K key, V value){
        writeLock.lock();
        try {
            cache.put(key, value);
        } finally {
            writeLock.unlock();
        }
        return value;
    }

    /**
     * 移除缓存
     *
     * @param key 键
     * @return 移除的值
     */
    public V remove(K key) {
        writeLock.lock();
        try {
            return cache.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 清空缓存池
     */
    public void clear() {
        writeLock.lock();
        try {
            this.cache.clear();
        } finally {
            writeLock.unlock();
        }
    }
}
