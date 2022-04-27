package com.nowcoder.community.entity;

import com.nowcoder.community.util.CommunityConstant;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xi_wang
 * @create 2022-04-2022/4/21-14:10
 */
@Component
public class Event implements CommunityConstant {
    private int id;
    private String topic;   // 点赞、评论
    private int userId;
    private int entityType; // 点赞[帖子、评论（包括评论的评论）], 评论[帖子、评论]
    private int entityId;
    private int entityUserId;
    private Map<String,Object> data=new HashMap<>();

    // 改造setter方法，使其支持流式操作

    public int getId() {
        return id;
    }

    public Event setId(int id) {
        this.id = id;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String,Object> getData() {
        return this.data;
    }

    public Event setData(String key,Object val) {
        this.data.put(key,val);
        return this;
    }
}
