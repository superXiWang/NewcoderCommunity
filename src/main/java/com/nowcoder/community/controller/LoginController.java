package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.nowcoder.community.config.KaptchaConfig;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * @author xi_wang
 * @create 2021-12-2021/12/9-16:02
 */
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private Producer defaultKaptcha;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(value="/login",method = RequestMethod.POST)
    public String getLoginPage(String username, String password, String verifyCode, boolean isRememberMe, Model model,
            /*HttpSession session,*/ HttpServletResponse response,@CookieValue("kaptchaKey") String kaptchaKey){
        // ???session??????????????????????????????
//        if(!verifyCode.equalsIgnoreCase((String) session.getAttribute("text"))){
//            model.addAttribute("verifyCodeMsg","??????????????????");
//            return "/site/login";
//        }
        // ????????????redis??????????????????????????????key???cookie?????????
        String kaptcha=null;
        if(StringUtils.isNotBlank(kaptchaKey)){
            kaptcha=userService.getKaptchaFromRedis(kaptchaKey);
        }
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(verifyCode) || !verifyCode.equalsIgnoreCase(kaptcha)){
            model.addAttribute("verifyCodeMsg","??????????????????");
            return "/site/login";
        }

        // ?????????????????????
        int expiredSecond=isRememberMe?CommunityConstant.LONG_EXPIRED_SECOND:CommunityConstant.NORMAL_EXPIRED_SECOND;
        Map<String, Object> loginMsg = userService.login(username, password, expiredSecond);
        if(!loginMsg.containsKey("ticket")){
            model.addAttribute("usernameMsg",loginMsg.get("usernameMsg"));
            model.addAttribute("passwordMsg",loginMsg.get("passwordMsg"));
            return "site/login";
        }else{
            // ???????????????ticket??????cookie?????????response
            Cookie cookie =new Cookie("ticket",(String) loginMsg.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSecond);
            response.addCookie(cookie);
            return "redirect:/index";
        }
    }

    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String getLogout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        // ????????????????????????Spring SecurityContext??????Authentication
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String getRegisterPage(Model model, User user){  // ?????????????????????????????????????????????????????????User??????
        Map<String, Object> registerMsg = userService.register(user);
        if (registerMsg==null || registerMsg.isEmpty()){
            model.addAttribute("msg","?????????????????????????????????????????????????????????????????????!");
            model.addAttribute("url","/index");
            return "/site/operate-result";
        }
        model.addAttribute("usernameMsg",registerMsg.get("usernameMsg"));
        model.addAttribute("passwordMsg",registerMsg.get("passwordMsg"));
        model.addAttribute("emailMsg",registerMsg.get("emailMsg"));

        return "/site/register";
    }

    @RequestMapping(value="/activation/{username}/{activationCode}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("username") String username, @PathVariable("activationCode") String activationCode){
        int result = userService.activation(username,activationCode);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","?????????????????????????????????????????????");
            model.addAttribute("url","/login");
        }
        if (result==ACTIVATION_REPEAT){
            model.addAttribute("msg","?????????????????????????????????????????????????????????????????????");
            model.addAttribute("url","/login");
        }
        if (result==ACTIVATION_FAILURE){
            model.addAttribute("msg","???????????????????????????????????????");
            model.addAttribute("url","/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(value = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response /*, HttpSession session */){
        String text= defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(text);

        // ??????session???????????????????????????
        // session.setAttribute("text",text);
        // ????????????redis?????????????????????????????????redis???key???cookie?????????????????????session??????????????????session???????????????
        String kaptchaKey=RedisKeyUtil.getKaptchaKey(CommunityUtil.generateUUID());
        userService.storeKaptchaToRedis(kaptchaKey,text);
        Cookie cookie=new Cookie("kaptchaKey",kaptchaKey);
        cookie.setPath(contextPath);
        cookie.setMaxAge(60);
        response.addCookie(cookie);

        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            logger.error("????????????????????????"+e.getMessage());
        }
    }

}
