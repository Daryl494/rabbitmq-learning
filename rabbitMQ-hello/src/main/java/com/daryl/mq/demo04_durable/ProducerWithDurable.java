package com.daryl.mq.demo04_durable;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * author：Daryl
 * date: 2023/3/12
 */
public class ProducerWithDurable {
    public static final String QUEUE_NAME = "hello_durable";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 声明队列并让队列持久化(注意，如果队列在此之前已经存在且是非持久化的，则需先删除再创建，否则会报错)
        boolean queueDurable = true;
        channel.queueDeclare(QUEUE_NAME, queueDurable, false, false, null);
        // 从控制台读取消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            // 将消息也持久化
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            System.out.println("生产者发出消息:" + message);
        }
    }
}
