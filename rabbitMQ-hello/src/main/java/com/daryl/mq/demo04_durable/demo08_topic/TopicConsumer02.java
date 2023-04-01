package com.daryl.mq.demo04_durable.demo08_topic;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * author：Daryl
 * date: 2023/3/23
 */
public class TopicConsumer02 {
    private static final String EXCHANGE_NAME = "topic_log";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = "queue02";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, EXCHANGE_NAME, "*.*.rabbit");
        channel.queueBind(queueName, EXCHANGE_NAME, "lazy.#");  // #代表不管后面有多少个单词，而*仅代表一个单词
        System.out.println(queueName + "正在等待消息...");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(queueName + "接收到消息: " + new String(message.getBody()));
            System.out.println("消息绑定的routingKey为: " + message.getEnvelope().getRoutingKey());
            System.out.println("===============================================");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}
