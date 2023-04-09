package com.daryl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * author：Daryl
 * date: 2023/4/5
 */

@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {
    @Autowired
    RabbitTemplate rabbitTemplate;

    // 这一步是为了将当前对象注入到rabbitTemplate中，
    // 因为ConfirmCallback只是rabbitTemplate中的一个属性，不注入的话rabbitTemplate调的时候也不会调到当前类
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }


    /**
     * 参数说明:
     * 1. correlationData 保存回调消息的ID及相关信息
     * 2. 交换机是否接收了消息 true / false
     * 3. cause  接收失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        /**
         * 消息发布确认回调
         */
        String id = correlationData != null ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机接收了id为:{}的消息", id);
        } else {
            log.error("交换机接收消息失败, 消息id为:{}", id);
        }
    }

    /**
     * 消息回退时的回调，用于交换机找不到队列时触发
     * 只有在交换机找不到队列时才会触发，正常发送不会触发
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        String exchange = returnedMessage.getExchange();
        String routingKey = returnedMessage.getRoutingKey();
        int replyCode = returnedMessage.getReplyCode();
        String replyText = returnedMessage.getReplyText();
        Message message = returnedMessage.getMessage();
        log.error("消息:{}已被回退，交换机为:{}，routingKey为:{}，返回错误代码为:{}，错误信息为:{}",
                new String(message.getBody()), exchange, routingKey, replyCode, replyText);
    }
}
