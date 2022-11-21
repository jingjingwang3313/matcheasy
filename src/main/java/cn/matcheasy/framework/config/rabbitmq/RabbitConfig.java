package cn.matcheasy.framework.config.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 访问rabbitmq控制台   http://127.0.0.1:15672/
 * 可以手动创建虚拟host，创建用户，分配权限，创建交换机，创建队列等等，还有查看队列消息，消费效率，推送效率等等。
 * <p>
 * (还有 Header Exchange 头交换机 ，Default Exchange 默认交换机，Dead Letter Exchange 死信交换机)
 * <p>
 * 配置相关的消息确认回调函数, 推送消息存在四种情况:
 * ①消息推送到server，找不到交换机,触发的是 ConfirmCallback 回调函数
 * ②消息推送到server，找到交换机了,没找到队列,触发的是 ConfirmCallback和RetrunCallback两个回调函数
 * ③消息推送成功,触发的是 ConfirmCallback 回调函数
 *
 * @class RabbitConfig
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: RabbitTemplate 配置
 */
@Slf4j
@Configuration
public class RabbitConfig
{
    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory)
    {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        // 设置开启Mandatory,才能触发回调函数
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback()
        {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause)
            {
                // 可以在回调函数根据需求做对应的扩展或者业务数据处理
                log.info("ConfirmCallback:     " + "相关数据：" + correlationData);
                log.info("ConfirmCallback:     " + "确认情况：" + ack);
                log.info("ConfirmCallback:     " + "原因：" + cause);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback()
        {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey)
            {
                // 可以在回调函数根据需求做对应的扩展或者业务数据处理
                log.info("ReturnCallback:     " + "消息：" + message);
                log.info("ReturnCallback:     " + "回应码：" + replyCode);
                log.info("ReturnCallback:     " + "回应信息：" + replyText);
                log.info("ReturnCallback:     " + "交换机：" + exchange);
                log.info("ReturnCallback:     " + "路由键：" + routingKey);
            }
        });

        return rabbitTemplate;
    }
}
