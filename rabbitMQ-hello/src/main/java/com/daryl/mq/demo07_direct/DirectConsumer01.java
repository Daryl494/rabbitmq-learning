package com.daryl.mq.demo07_direct;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * author：Daryl
 * date: 2023/3/23
 */
public class DirectConsumer01 {
    private static final String EXCHANGE_NAME = "direct_log";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
//        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 将info和warning绑定到console队列中
        String queueName = "console";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, EXCHANGE_NAME, "info");
        channel.queueBind(queueName, EXCHANGE_NAME, "warning");

        DeliverCallback deliverCallback = (consumerTag, message) -> System.out.println("DirectConsumer01 接收到消息: " + new String(message.getBody()));
        System.out.println("DirectConsumer01 等待接收消息");
        channel.basicConsume(queueName, true, deliverCallback, (consumerTag -> {
        }));
    }
}
