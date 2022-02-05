package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author xi_wang
 * @create 2021-12-2021/12/7-19:46
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestMail {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void sendTextMail(){
        mailClient.sendMail("m202071089@hust.edu.cn","testTextMail","Hello, spirng mail!");
    }

    @Test
    public void sendHtmlMail(){
        Context context=new Context();
        context.setVariable("username","张三");
        String htmlContent = templateEngine.process("mail/demo",context);
        mailClient.sendMail("m202071089@hust.edu.cn","testHtmlMail",htmlContent);
    }
}
