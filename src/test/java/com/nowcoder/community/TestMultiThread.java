package com.nowcoder.community;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/22-21:50
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestMultiThread{
    private static AtomicInteger atomicInteger= new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException{
        multiThreadAdd();
    }
    private static void multiThreadAdd() throws InterruptedException{
        // 创建线程池,内含2个线程
        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        for(int i=0;i<100;i++){
            threadPool.execute(()->{
                for(int j=0;j<4;j++){
                    int res = atomicInteger.incrementAndGet();
                    System.out.println("线程:"+Thread.currentThread().getName()+" count="+res);
                }
            });
        }
        threadPool.shutdown();
        Thread.sleep(1000);
        System.out.println("最终结果为:"+atomicInteger.get());
    }
}