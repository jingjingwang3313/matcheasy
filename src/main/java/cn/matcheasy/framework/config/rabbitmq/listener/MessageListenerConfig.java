package cn.matcheasy.framework.config.rabbitmq.listener;

import cn.matcheasy.framework.config.rabbitmq.receiver.AckReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @class MessageListenerConfig
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 消费者接收到消息的消息确认机制
 */
@Slf4j
@Configuration
public class MessageListenerConfig
{

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private AckReceiver ackReceiver;// 消息接收处理类

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer()
    {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        // 设置当前的消费者数量
        container.setConcurrentConsumers(1);
        // 设置当前的消费者数量上限
        container.setMaxConcurrentConsumers(5);
        // 设置是否重回队列
        container.setDefaultRequeueRejected(true);
        // RabbitMQ默认是自动确认,改为手动确认消息
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 设置监听外露
        container.setExposeListenerChannel(true);

        // 设置需要手动确认监听的队列
        container.setQueueNames("directQueue", "fanout.A");

        // 如果同时设置多个如下： 前提是队列都是必须已经创建存在的
        //  container.setQueueNames("directQueue","directQueue2","directQueue3");

        // 另一种设置队列的方法,如果使用这种情况,那么要设置多个,就使用addQueues
        //container.setQueues(new Queue("directQueue", true));
        //container.addQueues(new Queue("directQueue2",true));
        //container.addQueues(new Queue("directQueue3",true));

        container.setMessageListener(ackReceiver); //对应的手动确认消息监听类

        return container;
    }

}

