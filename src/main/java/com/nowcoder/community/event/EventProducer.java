package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author xi_wang
 * @create 2022-04-2022/4/21-14:16
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 事件处理
    public void handleEvent(Event event){
        // System.out.println("--------------------进入EventProducer.handleEvent()---------------");
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
        // System.out.println("--------------------EventProducer.handleEvent(), 已发送给Kafka---------------");
    }
}
