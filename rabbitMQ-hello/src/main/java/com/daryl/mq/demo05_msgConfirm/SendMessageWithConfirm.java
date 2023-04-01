package com.daryl.mq.demo05_msgConfirm;

/**
 * author：Daryl
 * date: 2023/3/12
 */

import com.daryl.mq.util.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @description: 比较三种发布确认方式（单个确认，批量确认，异步批量确认）的执行效率
 */
public class SendMessageWithConfirm {
    public static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
//        singleMessageConfirm();     // 发布1000条消息，单独确认，总共耗时:591ms
//        batchMessageConfirm();      // 发布1000条消息，每100条确认一次，总共耗时:118ms
        asyncMessageConfirm();          // 发布1000个消息，异步消息确认耗时:55ms
    }

    // 单个消息发布确认
    public static void singleMessageConfirm() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 声明一个新的队列
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        // 开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = String.valueOf(i);
            channel.basicPublish("", queueName, null, message.getBytes());
            // 每发布一条消息等待确认一次
            boolean successFlag = channel.waitForConfirms();
            assert successFlag;
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "条消息，单独确认，总共耗时:" + (end - begin) + "ms");
    }

    // 批量发布确认
    public static void batchMessageConfirm() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 声明一个新的队列
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        // 开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = String.valueOf(i);
            channel.basicPublish("", queueName, null, message.getBytes());
            if ((i + 1) % 100 == 0) {   // 每一百条确认一次，避免一次性确认太多，其中一条出错的话就都失败的情况
                boolean successFlag = channel.waitForConfirms();
                assert successFlag;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "条消息，每100条确认一次，总共耗时:" + (end - begin) + "ms");
    }

    public static void asyncMessageConfirm() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 声明队列
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        // 开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        ConcurrentSkipListMap<Long, String> nackMap = new ConcurrentSkipListMap<>();
        // 消息确认成功时的回调
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            if (multiple) {  // 批量
                // 删除已经确认的消息，剩下的就是未确认的消息(批量删除在此之前的所有消息)
                ConcurrentNavigableMap<Long, String> confirmedMap = nackMap.headMap(deliveryTag, true);
                confirmedMap.clear();
            } else {
                nackMap.remove(deliveryTag);
            }
            System.out.println("确认消息成功:" + deliveryTag);
        };
        // 消息确认失败时的回调
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            System.out.println("确认消息失败:" + deliveryTag);
        };
        // 添加监听器
        channel.addConfirmListener(ackCallback, nackCallback);
        /**
         * 声明一个线程安全且有序的hashMap，使用与高并发的情况
         * 1. 即将序号与消息相关联，key为序号，value为消息message
         * 2. 可以进行批量删除（调表），只要给到序号可以删除序号以前的key-value
         * 3. 支持高并发
         */
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = String.valueOf(i);
            nackMap.put(channel.getNextPublishSeqNo(), message);
            channel.basicPublish("", queueName, null, message.getBytes());
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个消息，异步消息确认耗时:" + (end - begin) + "ms");
    }
}
