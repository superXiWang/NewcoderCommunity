package com.nowcoder.community;

import com.nowcoder.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/16-19:45
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ChangePassword {
    @Test
    public void changePassword(){
        String password = "aaa";
        String salt = "167f9";
        String combinePassword = CommunityUtil.md5(password+salt);
        System.out.println(combinePassword);
    }
}
