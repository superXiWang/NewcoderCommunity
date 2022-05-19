package com.nowcoder.community.util;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/20-14:36
 */
public class RedisKeyUtil implements CommunityConstant {
    private static final String prefixOfLike_Entity = "like:entity";
    private static final String prefixOfLike_User = "like:user";
    private static final String prefixOfKaptcha = "kaptcha";
    private static final String prefixOfLoginTicket = "loginTicket";
    private static final String prefixOfUser = "user";

    private static final String seperator = ":";

    // redis中记录对实体点赞的用户列表
    // key=like:entity:entityTypeString:entityId, val=集合类型，记录给该实体点赞的用户id
    public static String getRedisKey(int entityType,int entityId){
        String entityTypeString = entityType==ENTITY_TYPE_DISCUSSPOST ? "discussPost" : "comment";
        return prefixOfLike_Entity+seperator+entityTypeString+seperator+entityId;
    }

    // redis中记录该用户被点赞的总数
    // key=like:user:userId, val=该用户被点赞的总数
    public static String getRedisKey(int userId){
        return prefixOfLike_User+seperator+userId;
    }

    // redis中用户登录时的验证码，key样式
    // kaptcha:randomString, val=验证码文本内容
    public static String getKaptchaKey(String randomString){
        return prefixOfKaptcha+seperator+randomString;
    }

    // redis中用户登录后，存储的登录凭证，key样式
    // loginTicket:ticket, val=LoginTicket对象
    public static String getLoginTicketKey(String ticket){
        return prefixOfLoginTicket+seperator+ticket;
    }

    // redis存储用户，key样式
    // user:userId, val=User对象
    public static String getUserKey(int userId){
        return prefixOfUser+seperator+userId;
    }
}
