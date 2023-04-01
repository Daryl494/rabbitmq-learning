package com.daryl.mq.demo08_dead_queue;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

/**
 * author：Daryl
 * date: 2023/3/25
 */
public class NormalProducer {
    // 普通交换机
    private static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 可以在消息发送处声明也可以在队列中声明
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
        for (int i = 0; i < 10; i++) {
            String message = "message" + i;
            // 超时
//            channel.basicPublish(NORMAL_EXCHANGE, "zhangsan", properties, message.getBytes());
            // 超出最大长度
            channel.basicPublish(NORMAL_EXCHANGE, "zhangsan2", null, message.getBytes());
            // 拒绝
//            channel.basicPublish(NORMAL_EXCHANGE, "zhangsan3", null, message.getBytes());
            System.out.println("生产者发出消息: " + message);
        }
    }
}
