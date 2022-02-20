package com.nowcoder.community.util;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/20-14:36
 */
public class RedisKeyUtil implements CommunityConstant {
    private static final String prefix = "like:entity";
    private static final String seperator = ":";

    // redis中key的样式
    // like:entity:entityTypeString:entityId
    public static String getRedisKey(int entityType,int entityId){
        String entityTypeString = entityType==ENTITY_TYPE_DISCUSSPOST ? "discussPost" : "comment";
        return prefix+seperator+entityTypeString+seperator+entityId;
    }
}
