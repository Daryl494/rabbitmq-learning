package com.daryl.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * author：Daryl
 * date: 2023/4/5
 */
@Configuration
public class DelayQueueConfig {
    // 交换机
    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    // 队列
    public static final String DELAYED_QUEUE_NAME = "delayed.queue";
    // routing Key
    public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";

    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type", "direct");  // 交换机 -> 队列的类型
        /**
         * 1. 交换机名称
         * 2. 交换机类型 (这里填插件提供的交换机类型 x-delayed-message)
         * 3. 是否持久化
         * 4. 是否自动删除
         * 5. 其他参数
         */
        return new CustomExchange(DELAYED_EXCHANGE_NAME, "x-delayed-message", true, false, arguments);
    }

    @Bean
    public Queue delayedQueue() {
        return new Queue(DELAYED_QUEUE_NAME);
    }

    @Bean
    public Binding bindingDelayedQueueAndExchange(
            @Qualifier("delayedExchange") CustomExchange delayedExchange,
            @Qualifier("delayedQueue") Queue delayedQueue) {
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAYED_ROUTING_KEY).noargs();
    }
}
