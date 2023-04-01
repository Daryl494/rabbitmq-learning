package com.daryl.mq.demo04_durable.demo08_topic;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * author：Daryl
 * date: 2023/3/23
 */
public class TopicProducer {
    private static final String EXCHANGE_NAME = "topic_log";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        Map<String, String> map = new HashMap<>();
        map.put("quick.orange.rabbit", "被Q1、Q2接收");
        map.put("lazy.orange.elephant", "被Q1、Q2接收");
        map.put("quick.orange.fox", "被Q1接收");
        map.put("lazy.brown.fox", "被Q2接收");
        map.put("lazy.pink.rabbit", "虽然满足Q2的两个条件，但只会被Q2消费一次");
        map.put("quick.brown.fox", "不匹配任何绑定的队列，丢弃");
        map.put("quick.orange.male.rabbit", "不匹配任何绑定的队列，丢弃");
        map.put("lazy.orange.male.rabbit", "4个单词，匹配Q2");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String routingKey = entry.getKey();
            String message = entry.getValue();
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
        }
    }
}
