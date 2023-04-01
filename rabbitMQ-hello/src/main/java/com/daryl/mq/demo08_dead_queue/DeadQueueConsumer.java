package com.daryl.mq.demo08_dead_queue;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * author：Daryl
 * date: 2023/3/25
 */
public class DeadQueueConsumer {
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("死信消费者等待接收消息...");
        DeliverCallback deliverCallback = (consumerTag, message) -> System.out.println("死信消费者接受到消息: " + new String(message.getBody()));
        channel.basicConsume(DEAD_QUEUE, true, deliverCallback, consumerTag -> {
        });
    }
}
