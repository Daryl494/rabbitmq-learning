package com.daryl.consumer;

import com.daryl.config.ConfirmConfig;
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
public class BackupWarningConsumer {
    @RabbitListener(queues = ConfirmConfig.WARNING_QUEUE_NAME)
    /**
     * 配备了备份交换机后，当有消息没有找到队列时就会到这里进行消费
     */
    public void receiveWarningMsg(Message message) {
        String msg = new String(message.getBody());
        log.warn("备份交换机/备份队列 接收到消息: " + msg);
    }
}
