package com.daryl.controller;

import cn.hutool.core.lang.UUID;
import com.daryl.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author：Daryl
 * date: 2023/4/5
 */
@Slf4j
@RestController
@RequestMapping("/confirm")
public class ConfirmController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsg/{msg}")
    public void sendMsg(@PathVariable String msg) {
        // 填入消息的id
        CorrelationData correlationData = new CorrelationData(UUID.fastUUID().toString());
        // 正常地发送一条消息
//        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME, ConfirmConfig.CONFIRM_ROUTING_KEY, msg, correlationData);
        // 这里发送到一个不存在的交换机中，模拟发送失败，查看效果
//        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME + "123", ConfirmConfig.CONFIRM_ROUTING_KEY, msg, correlationData);
        // 发送给一个不存在的routingKey，交换机找不到对应的队列，这种情况是认为交换机已经正常接收消息的
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME, ConfirmConfig.CONFIRM_ROUTING_KEY + "123", msg, correlationData);
        log.info("发布确认生产者发送消息:{}", msg);
    }
}
