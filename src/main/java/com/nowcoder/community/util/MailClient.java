package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.lang.annotation.Target;

/**
 * @author xi_wang
 * @create 2021-12-2021/12/7-16:24
 */
@Component
public class MailClient {

    private static final Logger logger=LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendMail(String toAddress, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(fromAddress);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content,true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("发送邮件失败："+e.getMessage());
        }

    }
}
