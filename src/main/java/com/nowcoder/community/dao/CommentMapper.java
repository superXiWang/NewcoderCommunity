package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @author xiwang
 * @create 2022-02-05-21:16
 */
@Mapper
public interface CommentMapper {
    // 根据评论的对象类型entityType与entityId查找所有评论列表
    List<Comment> selectCommentsListByEntity(int entityType, int entityId, int offset, int limit);

    // 根据评论的id查找评论
    Comment selectCommentById(int id);

    // 查询总评论数
    int selectCommentsCountByEntity(int entityType, int entityId);

    // 插入评论
    int insertComment(Comment comment);
}
