package com.daryl.mq.demo06_fanout;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * author：Daryl
 * date: 2023/3/23
 */
public class FanoutReceiveLog02 {
    private static final String EXCHANGE_NAME = "log";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        DeliverCallback deliverCallback = (consumerTag, message) -> System.out.println("FanoutReceiveLog02接收到消息: " + new String(message.getBody()));

        System.out.println("FanoutReceiveLog02 等待接收消息...");
        channel.basicConsume(queueName, true, deliverCallback, (consumerTag) -> {
        });
    }
}
