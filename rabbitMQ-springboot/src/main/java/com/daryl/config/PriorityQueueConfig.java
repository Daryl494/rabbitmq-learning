package com.daryl.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author：Daryl
 * date: 2023/4/9
 */

/**
 * 配置优先队列
 */
@Configuration
public class PriorityQueueConfig {
    private static final String PRIORITY_EXCHANGE_NAME = "priority.exchange";
    public static final String PRIORITY_QUEUE_NAME = "priority.queue";
    public static final String ROUTING_KEY = "priority";

    @Bean
    public DirectExchange priorityExchange() {
        return new DirectExchange(PRIORITY_EXCHANGE_NAME);
    }

    @Bean
    public Queue priorityQueue() {
        return QueueBuilder
                .durable(PRIORITY_QUEUE_NAME)
                .maxPriority(10)        // 设置最大的优先级（默认是0-255， 此处设置为0-10，不设置过大，避免浪费内存）
                .build();
    }

    @Bean
    public Binding bindingPriorityQueueWithPriorityExchange(
            @Qualifier("priorityExchange") DirectExchange priorityExchange,
            @Qualifier("priorityQueue") Queue priorityQueue
    ) {
        return BindingBuilder.bind(priorityQueue).to(priorityExchange).with(ROUTING_KEY);
    }
}
