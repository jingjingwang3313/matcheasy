package cn.matcheasy.project.common.api.mq.controller;

import cn.hutool.core.date.DateUtil;
import cn.matcheasy.framework.result.Result;
import cn.matcheasy.framework.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @class: Sender
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 发送消息到 Mq
 */
@Slf4j
@RestController
@RequestMapping("/anon/mq")
public class Sender
{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send/{n}")
    public Result sendMsg(@PathVariable("n") String n)
    {
        Map<String, Object> map = new HashMap<>();
        try
        {
            String messageId = String.valueOf(UUIDUtil.fastSimpleUUID());
            String messageData = "[" + n + "]-{Hello , world!}";
            String createTime = DateUtil.now();
            map.put("messageId", messageId);
            map.put("messageData", messageData);
            map.put("createTime", createTime);

            if (StringUtils.equals(n, "1"))
            {
                //将消息携带绑定键值：directRouting 发送到直连交换机directExchange
                rabbitTemplate.convertAndSend("directExchange", "directRouting", map);
            }
            else if (StringUtils.equals(n, "2"))
            {
                //将消息携带绑定键值：topic.man 发送到主题交换机topicExchange
                rabbitTemplate.convertAndSend("topicExchange", "topic.man", map);
            }
            else if (StringUtils.equals(n, "3"))
            {
                //将消息携带绑定键值：topic.# 发送到主题交换机topicExchange
                rabbitTemplate.convertAndSend("topicExchange", "topic.woman", map);
            }
            else if (StringUtils.equals(n, "4"))
            {
                //将消息发送到扇型交换机fanoutExchange
                rabbitTemplate.convertAndSend("fanoutExchange", null, map);
            }
            else
            {
                log.info("【{}】值不能发送消息!" + n);
                return Result.error("【" + n + "】值不能发送消息!");
            }
            log.info("发送消息: 【{}】", map.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.success(map);
    }

}
