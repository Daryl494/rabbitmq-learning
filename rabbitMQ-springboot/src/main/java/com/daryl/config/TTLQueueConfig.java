package com.daryl.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * author：Daryl
 * date: 2023/3/25
 */
@Configuration
public class TTLQueueConfig {
    // 普通交换机名称
    public static final String X_EXCHANGE = "X";
    // 死信交换机名称
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    // 普通队列名称
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    // 死信队列名称
    public static final String DEAD_LETTER_QUEUE = "QD";
    // 新的普通队列（尝试用普通队列+消息ddl来实现延时队列）
    public static final String QUEUE_C = "QC";

    // 声明x交换机
    @Bean("xExchange")
    public DirectExchange xExchange() {
        return new DirectExchange(X_EXCHANGE);
    }

    // 声明y交换机
    @Bean("yExchange")
    public DirectExchange yExchange() {
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    // 声明普通队列A, TTL为10s
    @Bean("queueA")
    public Queue queueA() {
        Map<String, Object> arguments = new HashMap<>();
        // 设置死信交换机
        arguments.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        // 设置死信routingKey
        arguments.put("x-dead-letter-routing-key", "YD");
        // 设置ttl，单位是ms，这里设置10s过期
        arguments.put("x-message-ttl", 10000);
        return QueueBuilder.durable(QUEUE_A).withArguments(arguments).build();
        // 或者也可以直接使用api进行构造
//        return QueueBuilder.durable(QUEUE_A)
//                .deadLetterExchange(Y_DEAD_LETTER_EXCHANGE)
//                .deadLetterRoutingKey("YD")
//                .ttl(10000).build();
    }

    //声明普通队列B， TTL为40s
    @Bean("queueB")
    public Queue queueB() {
        Map<String, Object> arguments = new HashMap<>();
        // 设置死信交换机
        arguments.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        // 设置死信routingKey
        arguments.put("x-dead-letter-routing-key", "YD");
        // 设置ttl，单位是ms，这里设置10s过期
        arguments.put("x-message-ttl", 40000);
        return QueueBuilder.durable(QUEUE_B).withArguments(arguments).build();
        // 或者也可以直接使用api进行构造
//        return QueueBuilder.durable(QUEUE_B)
//                .deadLetterExchange(Y_DEAD_LETTER_EXCHANGE)
//                .deadLetterRoutingKey("YD")
//                .ttl(40000).build();
    }

    // 声明死信队列 queueD
    @Bean("queueD")
    public Queue queueD() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    // 绑定queueA和x交换机(因为存在多个相同类型的bean，所以需要用@Qualifier进行指定)
    @Bean
    public Binding queueABindingX(@Qualifier("queueA") Queue queueA, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    // 绑定queueB和x交换机(因为存在多个相同类型的bean，所以需要用@Qualifier进行指定)
    @Bean
    public Binding queueBBindingX(@Qualifier("queueB") Queue queueB, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }

    // 绑定死信队列和其交换机
    @Bean
    public Binding queueDBindingY(@Qualifier("queueD") Queue queueD, @Qualifier("yExchange") DirectExchange yExchange) {
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }

    @Bean
    public Queue queueC() {
        return QueueBuilder
                .durable(QUEUE_C)   // 队列名称
                .deadLetterExchange(Y_DEAD_LETTER_EXCHANGE) // 死信交换机
                .deadLetterRoutingKey("YD") //死信队列routingKey
                .build();
    }

    @Bean
    public Binding queueCBindingX(@Qualifier("queueC") Queue queueC, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueC).to(xExchange).with("XC");
    }

}
