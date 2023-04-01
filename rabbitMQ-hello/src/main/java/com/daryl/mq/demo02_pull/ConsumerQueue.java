package com.daryl.mq.demo02_pull;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * author：Daryl
 * date: 2023/3/9
 */

/**
 * @Description: 使用两个线程轮询消费消息
 */
public class ConsumerQueue {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        // 主线程
        Channel channel = RabbitMqUtils.getChannel();
        // 消息的接收
        DeliverCallback deliverCallback = (consumerTag, message) -> System.out.println("主线程接收到的消息为:" + new String(message.getBody()));
        // 消息取消
        CancelCallback cancelCallback = (consumerTag) -> System.out.println("消费者取消消费消息回调逻辑");
        System.out.println("主线程等待消息======");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);

        Runnable runnable = () -> {
            try {
                Channel channel2 = RabbitMqUtils.getChannel();
                DeliverCallback deliverCallback2 = (consumerTag, message) -> System.out.println("子线程接收到的消息为:" + new String(message.getBody()));
                CancelCallback cancelCallback2 = (consumerTag) -> System.out.println("消费者取消消费消息回调逻辑");
                System.out.println("子线程等待消息======");
                channel2.basicConsume(QUEUE_NAME, true, deliverCallback2, cancelCallback2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        new Thread(runnable).start();
    }
}
