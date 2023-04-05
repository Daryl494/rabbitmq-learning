package com.daryl.controller;

import com.daryl.config.DelayQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * author：Daryl
 * date: 2023/3/26
 */
@Slf4j
@RestController
@RequestMapping("/ttl")
public class DelayedMsgController {

    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * 发消息给两个ttl队列: 一个过期时间10s， 另一个过期时间40s，
     * 因为没有对应的消费者，最终在ttl之后都会转发给死信队列进行消费
     */
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable("message") String message) {
        log.info("当前时间:{}, 发送一条消息给两个TTL队列:{}", LocalDateTime.now(), message);

        rabbitTemplate.convertAndSend("X", "XA", "消息来自ttl为10s的队列:" + message);
        rabbitTemplate.convertAndSend("X", "XB", "消息来自ttl为40s的队列:" + message);
    }

    /**
     * 指定消息的ttl而非队列的ttl，这样存在一个问题就是：
     * 如果后进入队列过期时间比前面的短，那么它也只能等前面的消息消费完了才会消费
     * 如第一个消息过期时间为20s， 第二个为2s， 那么第二个只能等第一个消费完了才会消费，
     * 因为队列只会判断第一个消息是否已经过期， 而不会判断后续的消息
     */
    @GetMapping("/sendExpirationMsg/{message}/{ttlTime}")
    public void sendMsg(@PathVariable String message, @PathVariable String ttlTime) {
        log.info("当前时间:{}, 发送一条时长为{}毫秒的ttl消息给普通队列QC:{}", LocalDateTime.now(), ttlTime, message);
        rabbitTemplate.convertAndSend("X", "XC", message, msg -> {
            msg.getMessageProperties().setExpiration(ttlTime);      //设置消息的过期时间
            return msg;
        });
    }

    /**
     * 通过RabbitMQ_DELAYED_MESSAGE_EXCHANGE插件来实现延迟队列
     */
    @GetMapping("/sendDelayedMsg/{message}/{delayTime}")
    public void sendDelayedMsg(@PathVariable String message, @PathVariable Integer delayTime) {
        log.info("当前时间:{}, 发送一条延时时间为{}ms的消息给延迟交换机delay.exchange:{}", LocalDateTime.now(), delayTime, message);
        rabbitTemplate.convertAndSend(DelayQueueConfig.DELAYED_EXCHANGE_NAME, DelayQueueConfig.DELAYED_ROUTING_KEY,
                message, msg -> {
                    msg.getMessageProperties().setDelay(delayTime);     // 设置延迟时间
                    return msg;
                }
        );
    }
}
