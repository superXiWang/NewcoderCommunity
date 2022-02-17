package com.nowcoder.community.entity;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/16-15:21
 */
@Component
public class Message {
    private int id;
    private int fromId; // 发信用户id，特殊值 1, 代表系统发送的通知。
    private int toId; // 收信用户id
    private String conversationId; // 对话id
    private String content; // 对话内容
    private int status; // 对话状态 0-未读，1-已读，2-无效
    private Date createTime;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", conversationId='" + conversationId + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
