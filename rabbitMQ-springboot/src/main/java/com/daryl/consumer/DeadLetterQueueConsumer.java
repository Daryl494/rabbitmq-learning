package com.daryl.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * author：Daryl
 * date: 2023/3/26
 */
@Slf4j
@Component
public class DeadLetterQueueConsumer {
    // 指定接收哪个队列的消息
    @RabbitListener(queues = "QD")
    public void receiveMessageFromQueueD(Message message, Channel channel) {
        String msg = new String(message.getBody());
        log.info("当前消息:{}, 收到死信队列的消息:{}", LocalDateTime.now(), msg);
    }
}
