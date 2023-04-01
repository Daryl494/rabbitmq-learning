package com.daryl.mq.demo01_hello;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * author：Daryl
 * date: 2023/3/9
 */
public class Producer {
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
        // 获取信道
        Channel channel = connection.createChannel();
        /**
         * 生成一个队列,参数含义：
         * 1. 队列名称
         * 2. 队列是否需要持久化，存在磁盘中，在mq重启时会自动重新生成队列，默认情况下队列不持久化，存在内存中
         * 3. 该队列是否只供一个消费者消费（如果设置为false，则可以被多个消费者消费，消息共享）
         * 4. 队列是否自动删除，最后一个消费者断开连接后，队列是否需要自动删除
         * 5. 其他参数（如消息是否自动确认等），存于map中
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        /**
         * 发送一个消息
         * 1. 发送到哪个交换机，此处空字符串代表默认的交换机
         * 2. 路由的key值，这里写的是队列的名称
         * 3. 其他参数
         * 4. 发送消息的消息体
         */
        String message = "hello world";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("消息发送成功!");
        connection.close();
    }
}
