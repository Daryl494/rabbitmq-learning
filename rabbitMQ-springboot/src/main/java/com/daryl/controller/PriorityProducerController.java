package com.daryl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author：Daryl
 * date: 2023/4/9
 */
@Slf4j
@RestController
@RequestMapping("/priority")
public class PriorityProducerController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 一次性发10条，并配置其中一条的优先级较高
     * ps: 注意这里需要先发完/将消费者注释掉，让消息堆积在队列中，然后再写消费者重新启动项目，
     * 否则在发的过程中消息就已经被消费，存在不准确性
     */
    @GetMapping("/sendMsg")
    public void sendPriorityMsg() {

        for (int i = 0; i < 10; i++) {
            String message = "message: " + i;
            if (i == 5) {
                rabbitTemplate.convertAndSend("priority.exchange", "priority", message, msg -> {
                    msg.getMessageProperties().setPriority(5);      // 设置优先级为5
                    return msg;
                });
            } else {
                rabbitTemplate.convertAndSend("priority.exchange", "priority", message);
            }
        }
    }
}
