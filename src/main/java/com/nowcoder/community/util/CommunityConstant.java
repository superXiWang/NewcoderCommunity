package com.nowcoder.community.util;

import org.apache.kafka.common.protocol.types.Field;

/**
 * @author xi_wang
 * @create 2021-12-2021/12/19-13:16
 */
public interface CommunityConstant {
    // 激活状态
    int ACTIVATION_SUCCESS=0;
    int ACTIVATION_REPEAT=1;
    int ACTIVATION_FAILURE=2;
    // 是否勾选“记住我”，登录凭证的失效时间
    int NORMAL_EXPIRED_SECOND=60*10;
    int LONG_EXPIRED_SECOND=60*60*24*7;

    // 评论、点赞针对的实体类型常量；Event中针对的实体类型
    int ENTITY_TYPE_DISCUSSPOST = 1;    // 帖子
    int ENTITY_TYPE_COMMENT = 2;    // 评论
    int ENTITY_TYPE_USER = 3;    // 用户

    // 消息队列：主题
    String TOPIC_LIKE="like";
    String TOPIC_COMMENT="comment";

    // 系统通知发送方 为1
    int MESSAGE_SYSTEM=1;

}
