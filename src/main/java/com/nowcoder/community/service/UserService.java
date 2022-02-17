package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.descriptor.web.ContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public User findUserByName(String username) { return userMapper.selectByName(username); }

    public User findUserByEmail(String email) { return userMapper.selectByEmail(email); }

    public LoginTicket findLoginTicketByTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    public Map<String,Object> register(User user){
        Map<String, Object> map = new HashMap<String, Object>();
        if(user==null){
            logger.error("参数不能为空！");
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }
        // 验证账号和密码是否重复
        User user1 = userMapper.selectByName(user.getUsername());
        if(user1!=null){
            map.put("usernameMsg","账号已被注册！");
            return map;
        }
        User user2 = userMapper.selectByEmail(user.getEmail());
        if(user2!=null){
            map.put("emailMsg","邮箱已被注册！");
            return map;
        }

        // 随机salt，加密，注册
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);    //普通用户
        user.setStatus(0);  //未激活
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送验证邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        context.setVariable("url",domain+contextPath+"/activation/"+user.getUsername()+"/"+user.getActivationCode());
        String htmlContent = templateEngine.process("/mail/activation",context);

        mailClient.sendMail(user.getEmail(),"验证邮件",htmlContent);
        return map;
    }

    public int activation(String username, String code){
        User user=userMapper.selectByName(username);
        if(user==null) return ACTIVATION_FAILURE;
        if(user.getStatus()==1) return ACTIVATION_REPEAT;
        if(user.getStatus()==0 && user.getActivationCode().equals(code)){
            System.out.println(user.getId());
            userMapper.updateStatus(user.getId(),1);
            return ACTIVATION_SUCCESS;
        }
        return ACTIVATION_FAILURE;
    }
    /*
        登录功能，判断用户名，密码是否为空且相互匹配，并生成登录凭证
        输入为用户名，密码，登录凭证过期时间
        输出为Map，存有提示信息、登录凭证
     */
    public Map<String,Object> login(String username, String password, int expiredSecond){
        Map<String,Object> map=new HashMap<String, Object>();
        // 空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        // 验证用户状态
        User user=userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","该用户不存在！");
            return map;
        }
        if(user.getStatus()==0){
            map.put("usernameMsg","该用户未激活！");
            return map;
        }
        // 验证用户与密码是否匹配
        String salt = user.getSalt();
        if(!CommunityUtil.md5(password+salt).equals(user.getPassword())){
            map.put("passwordMsg","密码不正确！");
            return map;
        }

        // 只有所有正常，才生成登录凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        String ticket=CommunityUtil.generateUUID();
        loginTicket.setTicket(ticket);
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSecond*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",ticket);

        return map;
    }

    /*
        退出登录，修改LoginTicket的状态
    */
    public void logout(String ticket){
        System.out.println(ticket);
        loginTicketMapper.updateStatus(ticket,1);
    }

    /*
        更新用户的headerUrl
     */
    public int updateHeaderUrl(int userId, String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }

    /*
        更新用户的密码
     */
    public int updatePassword(int userId, String newPassword){
        User user = userMapper.selectById(userId);
        return userMapper.updatePassword(userId,newPassword);
    }

    public int updateStatus(int userId, int status){
        return userMapper.updateStatus(userId,status);
    }
}
