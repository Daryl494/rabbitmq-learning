package com.daryl.mq.demo08_dead_queue;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;


/**
 * 1. 创建普通队列和死信队列以及对应的交换机并作绑定
 * 2. 在普通队列的参数一栏填入死信队列的相关参数
 * 3. 测试时首先将此consumer类启动，在mq中创建出对应的队列和交换机后关闭此consumer，模拟消息消费失败
 *    因为设置了消息的超时时间为10s，因此在10s后便可以在死信消费者中看到消息的消费
 */
public class NormalConsumerWithTimeout {
    // 普通交换机和普通队列
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    private static final String NORMAL_QUEUE = "normal_queue";
    // 死信交换机和死信队列
    private static final String DEAD_EXCHANGE = "dead_exchange";
    private static final String DEAD_QUEUE = "dead_queue";


    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 声明死信交换机和死信队列并绑定
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "lisi");

        /**
         * 填充参数:
         * 1. 声明普通交换机和队列，并设置超时时间参数（可以在队列设置也可以在生产者发送消息时设置消息的超时时间）
         * 2. 填入死信队列的信息
         */
        Map<String, Object> arguments = new HashMap<>();
        // 消息10s过期
        arguments.put("x-message-ttl", 10000);
        // 绑定死信队列（即当普通队列不能消费时，消息会被转发到死信队列中去）
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", "lisi");
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, arguments);
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "zhangsan");

        System.out.println("普通消费者等待接收消息...");
        DeliverCallback deliverCallback = (consumerTag, message) -> System.out.println("消费者接受到消息: " + new String(message.getBody()));
        channel.basicConsume(NORMAL_QUEUE, true, deliverCallback, consumerTag -> {
        });
    }
}
