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

    // redis中实体key的样式
    // like:entity:entityTypeString:entityId
    public static String getRedisKey(int entityType,int entityId){
        String entityTypeString = entityType==ENTITY_TYPE_DISCUSSPOST ? "discussPost" : "comment";
        return prefixOfLike_Entity+seperator+entityTypeString+seperator+entityId;
    }

    // redis中用户key的样式
    // like:user:userId
    public static String getRedisKey(int userId){
        return prefixOfLike_User+seperator+userId;
    }

    // redis中用户登录时的验证码，key样式
    // kaptcha:randomString
    public static String getKaptchaKey(String randomString){
        return prefixOfKaptcha+seperator+randomString;
    }

    // redis中用户登录后，存储的登录凭证，key样式
    // loginTicket:ticket
    public static String getLoginTicketKey(String ticket){
        return prefixOfLoginTicket+seperator+ticket;
    }

    // redis存储用户，key样式
    // user:userId, val=User对象
    public static String getUserKey(int userId){
        return prefixOfUser+seperator+userId;
    }
}
