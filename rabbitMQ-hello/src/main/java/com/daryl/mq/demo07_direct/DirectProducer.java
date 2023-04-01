package com.daryl.mq.demo07_direct;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * author：Daryl
 * date: 2023/3/23
 */
public class DirectProducer {
    private static final String EXCHANGE_NAME = "direct_log";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT); // 绑定为直接消费方式
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            // 根据不同的routingKey发送给不同的队列
//            channel.basicPublish(EXCHANGE_NAME, "info", null, message.getBytes());
//            channel.basicPublish(EXCHANGE_NAME, "warning", null, message.getBytes());
            channel.basicPublish(EXCHANGE_NAME, "error", null, message.getBytes());
            System.out.println("生产者发出消息:" + message);
        }
    }
}
