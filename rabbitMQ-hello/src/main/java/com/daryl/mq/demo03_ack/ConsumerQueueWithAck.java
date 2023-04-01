package com.daryl.mq.demo03_ack;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * author：Daryl
 * date: 2023/3/11
 */

/**
 * @Description: 实现手动确认消息（消费者消费完成后再去跟ack确认消息已完成。
 * 先前的是自动确认，即mq发送完消息便确认完成，但如果在消费过程中出错则会导致消息丢失）
 */
public class ConsumerQueueWithAck {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("主线程等待消息===");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            try {
                System.out.println("主线程接收到消息:" + new String(message.getBody()));
                Thread.sleep(1000);
                System.out.println("主线程消费消息完成:" + new String(message.getBody()));
                // 消费成功后手动应答(不批量)
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        CancelCallback cancelCallback = (consumerTag) -> System.out.println("消费者取消消费");
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);

        new Thread(() -> {
            try {
                Channel channel1 = RabbitMqUtils.getChannel();
                System.out.println("子线程等待消息===");
                DeliverCallback deliverCallback1 = (consumerTag, message) -> {
                    try {
                        System.out.println("子线程接收到消息:" + new String(message.getBody()));
                        if (1 == 1) {
                            System.out.println("子线程抛出异常===");
                            throw new RuntimeException("测试消费失败");
                        }
                        Thread.sleep(10000);
                        System.out.println("子线程消费消息完成:" + new String(message.getBody()));
                        // 消费成功后手动应答(不批量)
                        channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                };
                CancelCallback cancelCallback1 = (consumerTag) -> System.out.println("消费者取消消费");
                channel1.basicConsume(QUEUE_NAME, autoAck, deliverCallback1, cancelCallback1);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
