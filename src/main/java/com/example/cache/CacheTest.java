package com.example.cache;

/**
 * @author tangaq
 * @date 2019/4/10
 */
public class CacheTest {
    private static int s = 0;
    private static int t = 0;
    public static void main(String[] args) throws InterruptedException {
        CacheLock cache = new CacheLock();
        for (int i = 0;i < 1000;i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0;j < 5;j++) {
                        System.out.println(Thread.currentThread().getName()+":存入前："+cache.getRoleTotal(1));
                        s++;
                        cache.save(1);
                        System.out.println(Thread.currentThread().getName()+":存入后："+cache.getRoleTotal(1));
                        System.out.println("=========");
                    }
                    for (int j = 0;j < 1;j++) {
                        System.out.println(Thread.currentThread().getName()+":取出前："+cache.getRoleTotal(1));
                        cache.take(1);
                        t++;
                        System.out.println(Thread.currentThread().getName()+":取出后："+cache.getRoleTotal(1));
                    }
                }
            }).start();
        }
        Thread.sleep(1000);
        System.out.println("存入次数 " + s + ",取出:" + t + ",最终:"+ cache.getRoleTotal(1));
    }
}
