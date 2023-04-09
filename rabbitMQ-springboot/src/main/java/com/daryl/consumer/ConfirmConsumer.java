package com.daryl.consumer;

import com.daryl.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * author：Daryl
 * date: 2023/4/5
 */
@Slf4j
@Component
public class ConfirmConsumer {
    @RabbitListener(queues = ConfirmConfig.CONFIRM_QUEUE_NAME)
    public void receiveConfirmMsg(Message message) {
        log.info("发布确认消费者收到消息:{}", new String(message.getBody()));
    }
}
