package com.daryl.consumer;

import com.daryl.config.DelayQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * author：Daryl
 * date: 2023/4/5
 */
@Slf4j
@Component
public class DelayQueueConsumer {
    @RabbitListener(queues = DelayQueueConfig.DELAYED_QUEUE_NAME)
    public void receiveDelayMessage(Message message) {
        log.info("当前时间:{}, 延迟消费者收到消息:{}", LocalDateTime.now(), new String(message.getBody()));
    }
}
