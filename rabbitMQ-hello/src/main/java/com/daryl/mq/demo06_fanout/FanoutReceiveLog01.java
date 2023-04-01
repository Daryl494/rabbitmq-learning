package com.daryl.mq.demo06_fanout;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * author：Daryl
 * date: 2023/3/23
 */
public class FanoutReceiveLog01 {
    private static final String EXCHANGE_NAME = "log";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT); // 声明交换机
        String queueName = channel.queueDeclare().getQueue();   // 创建一个临时队列
        channel.queueBind(queueName, EXCHANGE_NAME, "");    // fanout忽略routingKey

        DeliverCallback deliverCallback = (consumerTag, message) -> System.out.println("FanoutReceiveLog01接收到消息: " + new String(message.getBody()));
        System.out.println("FanoutReceiveLog01 等待接收消息...");

        channel.basicConsume(queueName, true, deliverCallback, (consumerTag) -> {
        });
    }
}
