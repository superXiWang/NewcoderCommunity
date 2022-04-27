package com.nowcoder.community;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/22-22:20
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestSynchronized {
    Lock lock =new ReentrantLock();
    int count=0;
    public static void main(String[] args) throws InterruptedException {
        TestSynchronized testSynchronized = new TestSynchronized();
        MyRunnable myRunnable = testSynchronized.new MyRunnable();
        // 创建4个线程执行同一个任务
        for(int i=0;i<4;i++){
            new Thread(myRunnable).start();
        }
        Thread.sleep(1000);
        System.out.println("最终结果为："+testSynchronized.count);
    }

    class MyRunnable implements Runnable{
        public void run(){
            for (int j = 0; j < 10000; j++) {
                lock.lock();    // 加锁
                try {
                    count++;    // 自增
                }finally {
                    lock.unlock(); // 释放锁
                }
            }
        }
    }
}
