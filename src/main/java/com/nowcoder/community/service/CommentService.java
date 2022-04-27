package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author xiwang
 * @create 2022-02-05-21:27
 */
@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    // 根据评论的对象类型entityType与entityId查找所有评论列表
    public List<Comment> getCommentsListByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsListByEntity(entityType, entityId, offset, limit);
    }

    // 根据评论的id查找对应评论
    public Comment getCommentById(int id){
        return commentMapper.selectCommentById(id);
    }

    // 根据评论的对象类型entityType与entityId查询评论总数
    public int getCommentsCountByEntity(int entityType, int entityId){
        return commentMapper.selectCommentsCountByEntity(entityType,entityId);
    }

    // 插入评论，由于需要调用DiscussPostService的服务，需要开启事务
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int insertComment(Comment comment){
        if(comment==null){
            throw new IllegalArgumentException("参数不能为空！(Comment)");
        }
        // 评论需要过滤敏感词和html转义
        String content = comment.getContent();
        content= HtmlUtils.htmlEscape(content);
        content = sensitiveFilter.filter(content);
        comment.setContent(content);

        // 插入评论
        int rows = commentMapper.insertComment(comment);
        // 更新discuss_post表中，帖子的评论总数
        if(comment.getEntityType()==ENTITY_TYPE_DISCUSSPOST){
            int commentCount = commentMapper.selectCommentsCountByEntity(ENTITY_TYPE_DISCUSSPOST,comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), commentCount);
        }
        return rows;
    }
}
