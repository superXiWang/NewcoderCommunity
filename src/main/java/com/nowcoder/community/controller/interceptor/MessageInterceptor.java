package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xi_wang
 * @create 2022-04-2022/4/27-17:08
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    public HostHolder hostHolder;
    @Autowired
    public MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在Controller之后，模板渲染之前执行
        // 获取用户未读的私信数量以及系统通知数量，并加入Model
        User user= hostHolder.getValue();

        if(user!=null && modelAndView!=null){
            int userId=user.getId();
            int unreadMessageCount=messageService.selectUnreadMessagesCount(userId,null);
            int unreadNoticeCount=messageService.selectUnreadTopicNoticesCount(userId,null);
            modelAndView.addObject("unReadCount",unreadMessageCount+unreadNoticeCount);
        }

    }

}
