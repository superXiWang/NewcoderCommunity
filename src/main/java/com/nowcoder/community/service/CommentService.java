package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiwang
 * @create 2022-02-05-21:27
 */
@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    // 根据评论的对象类型entityType与entityId查找评论
    public List<Comment> getCommentsListByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsListByEntity(entityType, entityId, offset, limit);
    }
    public int getCommentsCountByEntity(int entityType, int entityId){
        return commentMapper.selectCommentsCountByEntity(entityType,entityId);
    }
}
