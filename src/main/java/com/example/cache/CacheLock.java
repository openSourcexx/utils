package com.example.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author tangaq
 * @date 2019/4/10
 */
public class CacheLock {
    private Map<String,Object> cacheMap = new ConcurrentHashMap<>();

    private Map<Integer,Integer> roleTotalMap = new ConcurrentHashMap<>();

    private Lock lock = new ReentrantLock();

    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    public Object getCache(String key) {
        // 1.获取读锁
        rwl.readLock().lock();
        Object ret = cacheMap.get(key);
        try {
            // 2.判断是否存在数据
            if (null == ret) {
                // 3.如果数据不存在就从数据库获取数据，此时应先释放读锁，开启写锁
                rwl.readLock().unlock();
                // 4.防止其他线程并发读,再次校验
                if (null == cacheMap.get(key)) {
                    try {
                        rwl.writeLock().lock();
                        // 5.查库取值
                    } finally {
                        rwl.writeLock().unlock(); // 6.释放写锁
                    }

                }
                rwl.readLock().lock(); // 7.再次开启读锁
            }
        } finally {
            rwl.readLock().unlock(); // 8.最终释放读锁
        }
        return ret;
    }

    /**
     * 角色缓存存值
     * @param key
     */
    public void save(int key) {
        lock.lock();
        Integer count = roleTotalMap.get(key);
        try {
            if (null == count) {
                roleTotalMap.put(key,1);
            } else {
                count++;
                roleTotalMap.put(key,count);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 取值
     * @param key
     */
    public void take(int key) {
        lock.lock();
        Integer count = roleTotalMap.get(key);
        try {
            if (null == count) {
                roleTotalMap.put(key,0);
            } else {
                count--;
                if (count < 0) {
                    count = 0;
                }
                roleTotalMap.put(key, count);
            }
        } finally {
            lock.unlock();
        }
    }

    public int getRoleTotal(int key) {
        return roleTotalMap.get(key) == null ? 0 : roleTotalMap.get(key);
    }

}
