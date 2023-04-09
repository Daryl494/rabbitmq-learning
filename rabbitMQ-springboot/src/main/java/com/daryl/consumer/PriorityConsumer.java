package com.daryl.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * author：Daryl
 * date: 2023/4/9
 */
@Slf4j
@Component
public class PriorityConsumer {
    @RabbitListener(queues = "priority.queue")
    public void receiveMsg(Message message) {
        log.info("消费者接收并消费了消息:" + new String(message.getBody()));
    }
}
