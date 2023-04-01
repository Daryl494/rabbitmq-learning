package com.daryl.mq.demo07_direct;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * author：Daryl
 * date: 2023/3/23
 */
public class DirectConsumer02 {
    private static final String EXCHANGE_NAME = "direct_log";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = "disk";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, EXCHANGE_NAME, "error");

        DeliverCallback deliverCallback = (consumerTag, message) -> System.out.println("DirectConsumer02 接收到消息:" + new String(message.getBody()));
        System.out.println("DirectConsumer02 等待接收消息...");
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}
