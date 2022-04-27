package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import java.util.Scanner;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/17-10:37
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LeetCode {
    @Test
    public void Test() {
        int a=2<<2;
        float f=11.1f;
        double d=1E2;
        byte bb=97;
        int aa=100;
        char c=97;
        String s="1";

        System.out.println(a);
        System.out.println(d);
        System.out.println(f);
        System.out.println("byte="+bb);
        System.out.println("char="+c);
        System.out.println("int="+a);
        System.out.println("float="+f);
        System.out.println("double="+d);
    }

}
class MyThread implements Runnable{

    @Override
    public void run() {
        for(int i=0;i<10;i++){
            System.out.println("输出线程正在执行");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

