package com.daryl.mq.demo08_dead_queue;

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * author：Daryl
 * date: 2023/3/25
 */
public class NormalConsumerWithReject {
    // 普通交换机和普通队列
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    private static final String NORMAL_QUEUE2 = "normal_queue_2";
    // 死信交换机和死信队列
    private static final String DEAD_EXCHANGE = "dead_exchange";
    private static final String DEAD_QUEUE = "dead_queue";


    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 声明死信交换机和死信队列并绑定
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "lisi");

        /**
         * 填充参数:
         * 1. 声明普通交换机和队列，并设置队列积压的最大长度，超过长度的就会被塞入死信队列
         * (注意此处超过最大长度时，会将队列原先的一些消息转入死信队列，而非将后来的消息转入消息队列)
         * 2. 填入死信队列的信息
         */
        Map<String, Object> arguments = new HashMap<>();
        // 设置正常队列的长度限制
        arguments.put("x-max-length", 6);
        // 绑定死信队列（即当普通队列不能消费时，消息会被转发到死信队列中去）
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", "lisi");
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(NORMAL_QUEUE2, false, false, false, arguments);
        channel.queueBind(NORMAL_QUEUE2, NORMAL_EXCHANGE, "zhangsan3");

        System.out.println("普通消费者等待接收消息...");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String msg = new String(message.getBody());
            long deliveryTag = message.getEnvelope().getDeliveryTag();
            if ("message5".equals(msg)) {
                System.out.println("当前消息是5号消息，需要拒绝，转入死信队列");
                channel.basicReject(deliveryTag, false); // 拒绝后不重新入队列
            } else {
                System.out.println("消费者接受到消息: " + msg);
                channel.basicAck(deliveryTag, false);
            }
        };
        channel.basicConsume(NORMAL_QUEUE2, false, deliverCallback, consumerTag -> {
        });
    }
}
