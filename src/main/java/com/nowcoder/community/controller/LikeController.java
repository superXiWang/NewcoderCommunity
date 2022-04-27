package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/20-14:58
 */
@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    // 1. 对帖子详情页面的点赞功能，提供异步请求，点赞后不刷新页面，只更新点赞数据和点赞状态。
    @RequestMapping(value = "/like",method= RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId){
        // entityType, entityId : 被点赞的实体的类型、Id (帖子、评论、评论的评论可被点赞)
        // entityUserId : 被点赞的实体所属用户的Id
        // postId : 被点赞的实体所属的帖子的Id
        int userId = hostHolder.getValue().getId();
        // 点赞
        likeService.like(userId,entityType,entityId,entityUserId);
        // 更新点赞数量与点赞状态
        long likeCount = likeService.getLikeCount(entityType, entityId);
        int likeStatus = likeService.getLikeStatus(userId, entityType, entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        // 如果为点赞状态，进行系统通知
        if(likeStatus==1){
            // 1. 封装为事件
            Event event=new Event();
            event.setTopic(TOPIC_LIKE);
            event.setEntityId(entityId);
            event.setEntityType(entityType);
            event.setEntityUserId(entityUserId);
            event.setUserId(userId);
            event.setData("postId",postId);
            // 2. 生产者将事件加入队列
            eventProducer.handleEvent(event);
            // System.out.println("--------------------LikeController.like()---------------");
        }

        return CommunityUtil.getJSONString(0,null,map);
    }
}
