package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author xi_wang
 * @create 2022-05-2022/5/24-16:46
 */
@SpringBootTest
public class TestDate {
    @Test
    public void testDate(){
        Date date=new Date();
        // 这四个输出一样，默认以CST为时区，东八区是4个CST中的一个
        // Locale只设置语言，不影响时间
        System.out.println("默认："+date);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.CHINA).format(date));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(date));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH).format(date));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.out.println(dateFormat.format(date));
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        System.out.println(dateFormat.format(date));

    }
}
