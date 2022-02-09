package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author xiwang
 * @create 2022-02-05-21:16
 */
@Mapper
public interface CommentMapper {
    // 根据评论的对象类型entityType与entityId查找评论
    List<Comment> selectCommentsListByEntity(int entityType, int entityId, int offset, int limit);
    int selectCommentsCountByEntity(int entityType, int entityId);
}
