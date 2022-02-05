package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author xi_wang
 * @create 2021-12-2021/12/26-19:35
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在Controller之前执行
        // 获取请求中携带的cookie：ticket
        String ticket = CookieUtil.getValue(request, "ticket");
        // System.out.println("------------------------LoginTicketInterceptor: preHandler(): "+ticket+"------------------------");
        if(ticket!=null){
            LoginTicket loginTicket = userService.findLoginTicketByTicket(ticket);
            // 检验登录凭证是否仍然有效
            if(loginTicket!=null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date())){
                // 通过LoginTicket获取userId, 通过userId获取user
                User user = userService.findUserById(loginTicket.getUserId());
                // System.out.println("------------------------LoginTicketInterceptor: preHandler(): "+user.getUsername()+"------------------------");
                // 用ThreadLocal工具类保存user
                hostHolder.setValue(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在Controller之后，模板之前
        User user =hostHolder.getValue();
        // System.out.println("------------------------LoginTicketInterceptor: postHandler(): "+user.getUsername()+"------------------------");
        if(modelAndView!=null && user!=null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
