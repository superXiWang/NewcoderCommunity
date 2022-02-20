package com.nowcoder.community.service;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/20-14:42
 * 提供点赞相关的业务逻辑功能
 */

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    // 需要提供以下业务逻辑方法
    // 1. 点赞与取消点赞
    public void like(int userId, int entityType, int entityId){
        // 根据点赞针对的实体，使用工具类生成key值，value值使用集合，记录所有点赞者的id。
        String redisKey = RedisKeyUtil.getRedisKey(entityType, entityId);
        // 判断是否已经点赞
        if(redisTemplate.opsForSet().isMember(redisKey,userId)){
            // 取消点赞
            redisTemplate.opsForSet().remove(redisKey,userId);
        }else {
            // 点赞
            redisTemplate.opsForSet().add(redisKey,userId);
        }
    }

    // 2. 查询点赞数量
    public long getLikeCount(int entityType, int entityId){
        String redisKey = RedisKeyUtil.getRedisKey(entityType, entityId);
        return redisTemplate.opsForSet().size(redisKey);
    }
    // 3. 查询点赞状态
    public int getLikeStatus(int userId,int entityType,int entityId){
        String redisKey = RedisKeyUtil.getRedisKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(redisKey,userId)? 1 : 0;
    }
}
