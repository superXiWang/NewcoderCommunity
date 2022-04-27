package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/16-16:48
 */
@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    // 获取会话列表
    public List<Message> selectConversationsListByUserId(int userId, int offset, int limit){
        return messageMapper.selectConversationsListByUserId(userId,offset,limit);
    }

    // 获取会话总数
    public int selectConversationsCountByUserId(int userId){
        return messageMapper.selectConversationsCountByUserId(userId);
    }

    // 获取某个会话的所有对话
    public List<Message> selectMessagesListByConversationId(String conversationId, int offset, int limit){
        return messageMapper.selectMessagesListByConversationId(conversationId,offset,limit);
    }

    // 获取某个会话的所有对话总数
    public int selectMessagesCountByConversationId(String conversationId){
        return messageMapper.selectMessagesCountByConversationId(conversationId);
    }

    // 获取某个会话的未读消息数（如果未指定会话ID，则获取所有未读消息数）
    public int selectUnreadMessagesCount(int userId,String conversationId){
        return messageMapper.selectUnreadMessagesCount(userId,conversationId);
    }

    // 新发起一条对话
    public int insertMessage(Message message){
        String content = message.getContent();
        // 过滤敏感词
        content = sensitiveFilter.filter(content);
        // html转义
        content = HtmlUtils.htmlEscape(content);
        message.setContent(content);

        // System.out.println("--------------------MessageService.insertMessage()---------------");

        return messageMapper.insertMessage(message);
    }

    // 更新对话状态
    public int updateMessagesStatus(List<Integer> ids, int status){
        return messageMapper.updateMessagesStatus(ids,status);
    }

    // 获取某主题下最新的系统通知
    public Message selectNewestTopicNotice(int userId,String topic){
        return messageMapper.selectNewestTopicNotice(userId,topic);
    }

    // 获取某主题下的系统通知总数
    public int selectTopicNoticesCount(int userId,String topic){
        return messageMapper.selectTopicNoticesCount(userId,topic);
    }

    // 获取某主题下未读的系统通知总数（如果未指定主题，则获取所有主题下的未读系统通知数）
    public int selectUnreadTopicNoticesCount(int userId,String topic){
        return messageMapper.selectUnreadTopicNoticesCount(userId, topic);
    }

    // 获取某主题下的系统通知列表
    public List<Message> selectTopicNoticesList(int userId,String topic,int offset,int limit){
        return messageMapper.selectTopicNoticesList(userId,topic,offset,limit);
    }
}
