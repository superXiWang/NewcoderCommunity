package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.EsDiscussPostService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xi_wang
 * @create 2022-04-2022/4/21-14:21
 */
@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private EsDiscussPostService esDiscussPostService;

    // 响应事件进行系统通知
    @KafkaListener(topics={TOPIC_LIKE,TOPIC_COMMENT})
    public void handleEvent(ConsumerRecord record){
        if(record==null){
            logger.error("消息内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event==null){
            logger.error("消息格式错误！");
            return;
        }

        // 开始处理
        // 1. 从事件中提取信息
            // event对象的data字段，在message中记录在content中，content还需记录event的其他所有未对应到Message对象中的字段，用一个Map来整理
        Map<String, Object> content=new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        for(Map.Entry<String,Object> entry:event.getData().entrySet()){
            content.put(entry.getKey(),entry.getValue());
        }

        // 2. 构造Message对象
        Message message=new Message();
        message.setFromId(MESSAGE_SYSTEM);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        message.setContent(JSONObject.toJSONString(content));

        // 3. 修改Message表
        messageService.insertMessage(message);

        // System.out.println("--------------------EventConsumer.handleEvent()---------------");
    }

    // 响应事件进行 Elasticsearch 的存储
    @KafkaListener(topics={TOPIC_DISCUSSPOST_SAVE_TO_ES})
    public void handleEventSaveToEs(ConsumerRecord record){
        if(record==null){
            logger.error("消息内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event==null){
            logger.error("消息格式错误！");
            return;
        }

        // 查出 discussPostId，找到对应帖子对象
        DiscussPost discussPost = discussPostService.findDiscussPost(event.getEntityId());
        esDiscussPostService.insertDiscussPost(discussPost);
    }
}
