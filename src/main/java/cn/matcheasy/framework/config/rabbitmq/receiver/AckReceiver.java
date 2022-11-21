package cn.matcheasy.framework.config.rabbitmq.receiver;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * @class AckReceiver
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: MQ手动监听消费, 需要实现 ChannelAwareMessageListener
 */
@Slf4j
@Component
public class AckReceiver implements ChannelAwareMessageListener
{

    @Override
    public void onMessage(Message message, Channel channel) throws Exception
    {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try
        {
            // 因为传递消息的时候用的map传递,所以将Map从Message内取出需要做些处理,获取消息转换根据格式去调整
            String msg = Convert.toStr(message);
            // 可以点进Message里面看源码,单引号直接的数据就是我们的map消息数据
            String[] msgArray = StrUtil.split(msg, "'");
            // 这里的获取消息转换,只作参考,可以自己根据格式去调整
            Dict dictMsg = mapStringToMap(StrUtil.trim(msgArray[1]), 3);
            String messageId = dictMsg.getStr("messageId");
            String messageData = dictMsg.getStr("messageData");
            String createTime = dictMsg.getStr("createTime");

            if ("directQueue".equals(message.getMessageProperties().getConsumerQueue()))
            {
                log.info("消费的消息来自的队列名为：" + message.getMessageProperties().getConsumerQueue());
                log.info("消息成功消费到  messageId:" + messageId + "  messageData:" + messageData + "  createTime:" + createTime);
                log.info("执行directQueue中的消息的业务处理流程......");

            }

            if ("fanout.A".equals(message.getMessageProperties().getConsumerQueue()))
            {
                log.info("消费的消息来自的队列名为：" + message.getMessageProperties().getConsumerQueue());
                log.info("消息成功消费到  messageId:" + messageId + "  messageData:" + messageData + "  createTime:" + createTime);
                log.info("执行fanout.A中的消息的业务处理流程......");

            }

            channel.basicAck(deliveryTag, true); //肯定确认
            //channel.basicReject(deliveryTag, true);//否定确认,拒绝消费当前消息,true会重新放回队列
        }
        catch (Exception e)
        {
            channel.basicReject(deliveryTag, false);
            e.printStackTrace();
        }
    }

    //{key=value,key=value,key=value} 格式转换成map
    private Dict mapStringToMap(String str, int entryNum)
    {
        str = str.substring(1, str.length() - 1);
        String[] strs = str.split(",", entryNum);
        Dict dict = new Dict();
        for (String string : strs)
        {
            String key = string.split("=")[0].trim();
            String value = string.split("=")[1];
            dict.put(key, value);
        }
        return dict;
    }

}
