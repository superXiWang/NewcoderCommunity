package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.Future;

/**
 * @author xi_wang
 * @create 2022-05-2022/5/13-11:55
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestTask {
    @Autowired
    private Task task;
    @Autowired
    private AsyncTask asyncTask;
    @Test
    public void testTask() throws Exception {
        long start = System.currentTimeMillis();
        task.doTaskOne();
        task.doTaskTwo();
        task.doTaskThree();

        long end = System.currentTimeMillis();
        System.out.println( "完成所有任务，耗时：" + (end - start) + "毫秒");
    }
    @Test
    public void testAsyncTask() throws Exception {
        long start = System.currentTimeMillis();
        Future<String> task1 = asyncTask.doTaskOne();
        Future<String> task2 = asyncTask.doTaskTwo();
        Future<String> task3 = asyncTask.doTaskThree();
        while( true) {
            if (task1.isDone() && task2.isDone() && task3.isDone()) {
            // 三个任务都调用完成，退出循环等待
                break;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println( "完成所有任务，耗时：" + (end - start) + "毫秒");
    }
}
