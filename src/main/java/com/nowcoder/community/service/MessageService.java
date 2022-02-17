package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

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

    public List<Message> selectConversationsListByUserId(int userId, int offset, int limit){
        return messageMapper.selectConversationsListByUserId(userId,offset,limit);
    }

    public int selectConversationsCountByUserId(int userId){
        return messageMapper.selectConversationsCountByUserId(userId);
    }

    public List<Message> selectMessagesListByConversationId(String conversationId, int offset, int limit){
        return messageMapper.selectMessagesListByConversationId(conversationId,offset,limit);
    }

    public int selectMessagesCountByConversationId(String conversationId){
        return messageMapper.selectMessagesCountByConversationId(conversationId);
    }

    public int selectUnreadMessagesCount(int userId,String conversationId){
        return messageMapper.selectUnreadMessagesCount(userId,conversationId);
    }

    public int insertMessage(Message message){
        String content = message.getContent();
        // 过滤敏感词
        content = sensitiveFilter.filter(content);
        // html转义
        content = HtmlUtils.htmlEscape(content);
        message.setContent(content);
        return messageMapper.insertMessage(message);
    }

    public int updateMessagesStatus(List<Integer> ids, int status){
        return messageMapper.updateMessagesStatus(ids,status);
    }
}
