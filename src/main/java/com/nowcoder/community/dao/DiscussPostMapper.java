package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     * 查找一系列帖子，用于首页显示
     * @param userId
     * @param offset
     * @param limit
     * @return 包含很多帖子的列表
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 查询单个帖子，用于帖子详情显示
     * @param id
     * @return
     */
    DiscussPost findDiscussPost(int id);

    // 更新总评论数
    int updateCommentsCountById(int id, int commentCount);
}
