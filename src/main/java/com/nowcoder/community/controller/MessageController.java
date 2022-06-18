package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author xi_wang
 * @create 2022-02-2022/2/16-16:52
 */
@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    // 查看会话列表
    @RequestMapping(value = "/conversation/list",method = RequestMethod.GET)
    public String getConversationsList(Model model, Page page){
        // 获取当前用户
        User user = hostHolder.getValue();
        // 设置分页参数
        page.setPath("/conversation/list");
        page.setRows(messageService.selectConversationsCountByUserId(user.getId()));
        page.setLimit(5);

        List<Message> messagesList = messageService.selectConversationsListByUserId(user.getId(), page.getOffset(), page.getLimit());
        // 用List<Map>整理每个对话的数据，包括对话用户、对话最新的消息、对话未读消息数、对话总消息数
        List<Map<String,Object>> messageViewObjectList = new ArrayList<>();
        if(messagesList!=null){
            for(Message eachMessage:messagesList){
                Map<String,Object> map = new HashMap<>();
                // 对话最新的消息
                map.put("message",eachMessage);
                // 对话用户
                int targetId = eachMessage.getFromId() == user.getId() ? eachMessage.getToId() : eachMessage.getFromId();
                map.put("targetUser", userService.findUserById(targetId));
                // 对话未读消息数
                map.put("unReadCount",messageService.selectUnreadMessagesCount(user.getId(),eachMessage.getConversationId()));
                // 对话总消息数
                map.put("messagesCount",messageService.selectMessagesCountByConversationId(eachMessage.getConversationId()));
                messageViewObjectList.add(map);
            }
        }

        // model中应包含的数据：page，总未读消息数，List<Map>整理的每个对话的数据
        model.addAttribute("messageViewObjectList",messageViewObjectList);

        // 添加私信未读数量
        model.addAttribute("unReadMessagesCount",messageService.selectUnreadMessagesCount(user.getId(), null));

        // 记录系统通知未读总数量
        model.addAttribute("unReadNoticeCount",messageService.selectUnreadTopicNoticesCount(user.getId(),null));

        return "/site/letter";
    }

    // 查看某个会话的所有信息
    @RequestMapping(value = "/conversation-detail/{conversationId}",method = RequestMethod.GET)
    public String getConversationDetail(@PathVariable("conversationId") String conversationId, Model model, Page page){
        // 设置分页参数
        page.setPath("/conversation-detail"+conversationId);
        page.setRows(messageService.selectMessagesCountByConversationId(conversationId));
        page.setLimit(10);

        // 用List<Map>整理的每个消息的数据，包括：发送消息的用户、消息。
        List<Message> messagesList = messageService.selectMessagesListByConversationId(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> messageViewObjectList = new ArrayList<>();
        if(messagesList!=null){
            for(Message eachMessage:messagesList){
                Map<String,Object> map = new HashMap<>();
                // 消息
                map.put("message",eachMessage);
                // 发送消息的用户
                map.put("fromUser", userService.findUserById(eachMessage.getFromId()));
                messageViewObjectList.add(map);
            }
        }
        // 将会话中的未读消息状态更新为已读
        List<Integer> unreadMessageIds = getUnreadMessageIds(messagesList);
        if(!unreadMessageIds.isEmpty()){
            messageService.updateMessagesStatus(unreadMessageIds,1);
        }

        // model中应包含的数据：page，对话用户、List<Map>整理的每个消息的数据。
        model.addAttribute("messageViewObjectList",messageViewObjectList);
        // 查找对话的用户
        User currentUser = hostHolder.getValue(); // 当前用户
        String[] ids = conversationId.split("_");
        int targetId = currentUser.getId()!=Integer.parseInt(ids[0]) ? Integer.parseInt(ids[0]) : Integer.parseInt(ids[1]);
        model.addAttribute("targetUser",userService.findUserById(targetId));
        return "/site/letter-detail";
    }

    private List<Integer> getUnreadMessageIds(List<Message> messagesList){
        List<Integer> unReadMessageIds = new ArrayList<>();
        if(messagesList==null) return unReadMessageIds;
        for(Message each:messagesList){
            if(each.getToId()==hostHolder.getValue().getId() && each.getStatus()==0){
                unReadMessageIds.add(each.getId());
            }
        }
        return unReadMessageIds;
    }

    // 新增会话
    @RequestMapping(value = "/conversation/add",method = RequestMethod.POST)
    @ResponseBody
    // 该方法从表单接收一个toName和一个content，返回一个JSON格式的字符串
    public String addMessage(String toName, String content){
        User toUser = userService.findUserByName(toName);
        if(toUser==null) {
            return CommunityUtil.getJSONString(1,"目标用户不存在！");
        }
        Message message = new Message();
        message.setContent(content);
        message.setCreateTime(new Date());
        User currentUser = hostHolder.getValue();
        message.setFromId(currentUser.getId());
        message.setToId(toUser.getId());
        message.setStatus(0);
        if(currentUser.getId()<toUser.getId()){
            message.setConversationId(String.valueOf(currentUser.getId())+"_"+String.valueOf(toUser.getId()));
        }else{
            message.setConversationId(String.valueOf(toUser.getId())+"_"+String.valueOf(currentUser.getId()));
        }
        messageService.insertMessage(message);
        return CommunityUtil.getJSONString(0);  // 正常
    }

    // 显示系统通知概览页
    @RequestMapping(value = "/notice/list",method = RequestMethod.GET)
    public String getNoticePage(Model model){
        int userId=hostHolder.getValue().getId();

        // 记录系统通知未读总数量
        model.addAttribute("unReadNoticeCount",messageService.selectUnreadTopicNoticesCount(userId,null));

        // 记录 点赞 主题系统通知
        // 记录 最新一条消息
        Message newestTopicNotice= messageService.selectNewestTopicNotice(userId,TOPIC_LIKE);
        if(newestTopicNotice!=null){
            Map<String,Object> tempRecord=new HashMap<>();
            tempRecord.put("newestTopicMessage",newestTopicNotice);
            String content= HtmlUtils.htmlUnescape(newestTopicNotice.getContent());
            Map<String,Object> data = JSONObject.parseObject(content);
            // 将content中的剩余信息加入tempRecord
            // 动作发起用户
            tempRecord.put("user",userService.findUserById((int) data.get("userId")));
            // 动作针对的实体类型
            tempRecord.put("entityType",data.get("entityType"));
            // 动作针对的实体Id
            tempRecord.put("entityId",data.get("entityId"));
            // 动作针对的实体所属的帖子Id
            tempRecord.put("postId",data.get("postId"));

            // 记录 点赞主题未读通知数量
            tempRecord.put("unReadTopicNoticeCount",messageService.selectUnreadTopicNoticesCount(userId,TOPIC_LIKE));
            // 记录 点赞主题通知总数量
            tempRecord.put("topicNoticeCount",messageService.selectTopicNoticesCount(userId,TOPIC_LIKE));

            model.addAttribute("likeRecord",tempRecord);
        }



        // 记录 评论 主题系统通知
        // 记录 最新一条消息
        newestTopicNotice= messageService.selectNewestTopicNotice(userId,TOPIC_COMMENT);
        if(newestTopicNotice!=null){
            Map<String,Object> tempRecord=new HashMap<>();
            tempRecord.put("newestTopicMessage",newestTopicNotice);
            String content= HtmlUtils.htmlUnescape(newestTopicNotice.getContent());
            Map<String,Object> data = JSONObject.parseObject(content);
            // 将content中的剩余信息加入tempRecord
            // 动作发起用户
            tempRecord.put("user",userService.findUserById((int) data.get("userId")));
            // 动作针对的实体类型
            tempRecord.put("entityType",data.get("entityType"));
            // 动作针对的实体Id
            tempRecord.put("entityId",data.get("entityId"));
            // 动作针对的实体所属的帖子Id
            tempRecord.put("postId",data.get("postId"));


            // 记录 点赞主题未读通知数量
            tempRecord.put("unReadTopicNoticeCount",messageService.selectUnreadTopicNoticesCount(userId,TOPIC_COMMENT));
            // 记录 点赞主题通知总数量
            tempRecord.put("topicNoticeCount",messageService.selectTopicNoticesCount(userId,TOPIC_COMMENT));

            model.addAttribute("commentRecord",tempRecord);
        }


        // 添加私信未读数量
        model.addAttribute("unReadMessagesCount",messageService.selectUnreadMessagesCount(userId, null));

        return "/site/notice";
    }

    @RequestMapping(value = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getTopicNoticeDetail(@PathVariable("topic") String topic, Model model, Page page){
        int userId=hostHolder.getValue().getId();

        // 设置分页
        page.setRows(messageService.selectTopicNoticesCount(userId,topic));
        page.setPath("/notice/detail/"+topic);
        // 获取该主题下的通知列表
        List<Message> noticeList = messageService.selectTopicNoticesList(userId, topic, page.getOffset(), page.getLimit());

        // 遍历通知列表，使用List<Map>记录各个通知的触发用户、触发类型、帖子id、时间
        List<Map<String,Object>> noticeListVO=new ArrayList<>();
        for(Message each:noticeList){
            Map<String,Object> eachRecord=new HashMap<>();
            // 从message的content字段取出信息
            String content = each.getContent();
            Map<String,Object> data = JSONObject.parseObject(HtmlUtils.htmlUnescape(content));
            // 触发用户
            eachRecord.put("triggerUser",userService.findUserById((int)data.get("userId")));
            // 触发类型
            eachRecord.put("entityType",data.get("entityType"));
            // 帖子id
            eachRecord.put("postId",data.get("postId"));
            // 时间
            eachRecord.put("time",each.getCreateTime());

            noticeListVO.add(eachRecord);
        }

        model.addAttribute("noticeListVO",noticeListVO);
        model.addAttribute("topic",topic);

        // 将会话中的未读系统通知状态更新为已读
        List<Integer> unreadMessageIds = getUnreadMessageIds(noticeList);
        if(!unreadMessageIds.isEmpty()){
            messageService.updateMessagesStatus(unreadMessageIds,1);
        }

        return "/site/notice-detail";
    }
}
