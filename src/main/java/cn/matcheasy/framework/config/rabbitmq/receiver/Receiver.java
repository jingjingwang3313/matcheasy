package cn.matcheasy.framework.config.rabbitmq.receiver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @class Receiver
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: MQ自动监听消费
 */
@Slf4j
@Component
public class Receiver
{

    @RabbitHandler
    @RabbitListener(queues = {"directQueue"})
    public void receive(Map msg)
    {
        log.info("消费者收到 directQueue 消息: " + msg.toString());
    }

    @RabbitHandler
    @RabbitListener(queues = {"topic.man"})
    public void receive2(Map msg)
    {
        log.info("消费者收到 topic.man 消息: " + msg.toString());
    }

    @RabbitHandler
    @RabbitListener(queues = {"topic.woman"})
    public void receive3(Map msg)
    {
        log.info("消费者收到 topic.woman 消息: " + msg.toString());
    }

    @RabbitHandler
    @RabbitListener(queues = "fanout.A")
    public void receive4(Map msg)
    {
        log.info("消费者收到 fanout.A 消息  : " + msg.toString());
    }

    @RabbitHandler
    @RabbitListener(queues = "fanout.B")
    public void receive5(Map msg)
    {
        log.info("消费者收到 fanout.B 消息  : " + msg.toString());
    }

    @RabbitHandler
    @RabbitListener(queues = "fanout.C")
    public void receive6(Map msg)
    {
        log.info("消费者收到 fanout.C 消息  : " + msg.toString());
    }

}
