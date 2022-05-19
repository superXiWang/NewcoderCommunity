package com.nowcoder.community;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author xi_wang
 * @create 2022-05-2022/5/13-11:55
 */
@Component
public class Task {
    public static Random random = new Random();

    public void doTaskOne() throws Exception {
        System.out.println( "开始做任务一");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt( 10000));
        long end = System.currentTimeMillis();
        System.out.println( "完成任务一，耗时：" + (end - start) + "毫秒");
    }

    public void doTaskTwo() throws Exception {
        System.out.println( "开始做任务二");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt( 10000));
        long end = System.currentTimeMillis();
        System.out.println( "完成任务二，耗时：" + (end - start) + "毫秒");
    }

    public void doTaskThree() throws Exception {
        System.out.println( "开始做任务三");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt( 10000));
        long end = System.currentTimeMillis();
        System.out.println( "完成任务三，耗时：" + (end - start) + "毫秒");
    }
}