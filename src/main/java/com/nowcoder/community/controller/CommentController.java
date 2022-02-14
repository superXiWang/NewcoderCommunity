package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.service.CommentService;
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
public class CommentController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        // 视图只提交comment中的content, entityType, entityId, targetId，
        // 因此需要补充userId, createTime, status信息
        comment.setCreateTime(new Date());
        comment.setUserId(hostHolder.getValue().getId());
        comment.setStatus(0);

        // 添加评论
        commentService.insertComment(comment);
        return "redirect:/discuss/detail/"+discussPostId;
    }
}
