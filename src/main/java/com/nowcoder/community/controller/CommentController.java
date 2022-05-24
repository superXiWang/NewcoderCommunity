package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author xiwang
 * @create 2022-02-11-17:41
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        // 视图只提交comment中的content, entityType, entityId, targetId，
        // 因此需要补充userId, createTime, status信息
        comment.setCreateTime(new Date());
        comment.setUserId(hostHolder.getValue().getId());
        comment.setStatus(0);

        // 添加评论
        commentService.insertComment(comment);

        // 进行系统通知
        // 1. 封装为事件
        Event event=new Event();
        event.setTopic(TOPIC_COMMENT);
        event.setEntityId(comment.getEntityId());
        event.setEntityType(comment.getEntityType());
            // 添加事件的发起用户
        if(comment.getEntityType()==ENTITY_TYPE_DISCUSSPOST){
            DiscussPost temp= discussPostService.findDiscussPost(comment.getEntityId());
            event.setEntityUserId(temp.getUserId());
        }else if(comment.getEntityType()==ENTITY_TYPE_COMMENT){
            Comment temp = commentService.getCommentById(comment.getEntityId());
            event.setEntityUserId(temp.getUserId());
        }
        event.setUserId(comment.getUserId());
        event.setData("postId",discussPostId);
        // 2. 生产者将事件加入队列
        eventProducer.handleEvent(event);

        // 将 新增评论 封装为 Event，加入消息队列中，用于保存至Elasticsearch
        event=new Event().setTopic(TOPIC_DISCUSSPOST_SAVE_TO_ES)
                .setEntityType(ENTITY_TYPE_DISCUSSPOST)
                .setEntityId(discussPostId);

        eventProducer.handleEvent(event);

        return "redirect:/discuss/detail/"+discussPostId;
    }
}
