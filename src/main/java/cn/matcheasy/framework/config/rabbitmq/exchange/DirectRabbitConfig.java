package cn.matcheasy.framework.config.rabbitmq.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 直连交换机是一对一,根据消息携带的路由键将消息投递给对应队列
 * 大致流程，有一个队列绑定到一个直连交换机上，同时赋予一个路由键 routing key 。
 * 然后当一个消息携带着路由值为X，这个消息通过生产者发送给交换机时，交换机就会根据这个路由值X去寻找绑定值也是X的队列。
 *
 * @class DirectRabbitConfig
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Configuration
public class DirectRabbitConfig {

    //队列
    @Bean
    public Queue TestDirectQueue() {
        // durable:是否持久化,默认是false。持久化队列：会被存储在磁盘上,当消息代理重启时仍然存在;暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        // return new Queue("TestDirectQueue",true,false,false);

        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue("directQueue", true);
    }

    //Direct交换机
    @Bean
    public DirectExchange TestDirectExchange() {
        return new DirectExchange("directExchange", true, false);
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键
    @Bean
    public Binding bindingDirect() {
        //绑定将队列和交换机绑定,并设置用于匹配键：directRouting
        return BindingBuilder.bind(TestDirectQueue()).to(TestDirectExchange()).with("directRouting");
    }

}
