package com.daryl.mq.demo01_hello;

import com.rabbitmq.client.*;

/**
 * author：Daryl
 * date: 2023/3/9
 */
public class Consumer {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        // 创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 工厂IP 连接RabbitMQ的队列
        factory.setHost("192.168.230.131");
        // 用户名
        factory.setUsername("admin");
        // 密码
        factory.setPassword("123");
        // 创建新的连接
        Connection connection = factory.newConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        /**
         * 消费者消费消息
         * 1. 消费哪个队列的消息（队列名）
         * 2. 消费成功后是否自动应答（true表示自动应答，false则表示手动）
         * 3. 消费者接收消息的回调
         * 4. 消费者取消消费的回调
         */

        // 声明接收消息
        DeliverCallback deliverCallback = (consumerTag, message) -> System.out.println(new String(message.getBody()));
        // 取消消息时的回调
        CancelCallback cancelCallback = (consumerTag) -> System.out.println("消息消费被中断");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
