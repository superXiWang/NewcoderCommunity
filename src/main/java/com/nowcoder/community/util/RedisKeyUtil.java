package com.nowcoder.community.util;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/20-14:36
 */
public class RedisKeyUtil implements CommunityConstant {
    private static final String prefixOfEntity = "like:entity";
    private static final String prefixOfUser = "like:user";
    private static final String seperator = ":";

    // redis中实体key的样式
    // like:entity:entityTypeString:entityId
    public static String getRedisKey(int entityType,int entityId){
        String entityTypeString = entityType==ENTITY_TYPE_DISCUSSPOST ? "discussPost" : "comment";
        return prefixOfEntity+seperator+entityTypeString+seperator+entityId;
    }

    // redis中用户key的样式
    // like:user:userId
    public static String getRedisKey(int userId){
        return prefixOfUser+seperator+userId;
    }
}
