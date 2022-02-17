package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/16-15:24
 */
@Mapper
public interface MessageMapper {
    // 查询某用户的所有对话列表，仅显示对话中最新的消息，支持分页
    List<Message> selectConversationsListByUserId(int userId, int offset, int limit);
    // 查询某用户的对话数目
    int selectConversationsCountByUserId(int userId);
    // 查询某条对话的所有消息列表，支持分页
    List<Message> selectMessagesListByConversationId(String conversationId, int offset, int limit);
    // 查询某条对话的所有消息数目
    int selectMessagesCountByConversationId(String conversationId);
    // 查询某条对话的未读消息数目
    int selectUnreadMessagesCount(int userId,String conversationId);
    // 新增私信
    int insertMessage(Message message);
    // 更新多个消息的状态
    int updateMessagesStatus(List<Integer> ids, int status);
}
