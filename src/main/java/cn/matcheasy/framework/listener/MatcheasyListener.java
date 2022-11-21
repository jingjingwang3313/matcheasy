package cn.matcheasy.framework.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @class: MatcheasyListener
 * @author: wangjing
 * @date: 2021/3/13/0013
 * @desc: 以Spring的内置事件ContextRefreshedEvent为例,
 * 当ApplicationContext被初始化或刷新时,
 * 会触发ContextRefreshedEvent事件.
 */
@Slf4j
@Component
public class MatcheasyListener implements ApplicationListener<ContextRefreshedEvent>
{

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        //获取所有的bean
        /*String[] definitionNames = event.getApplicationContext().getBeanDefinitionNames();
        for (String name : definitionNames) {
            log.info("容器bean: 【{}】", name);
        }*/
        //避免邮件附件名截取
        System.setProperty("mail.mime.splitlongparameters", "false");
        log.info("ApplicationContext Refreshed !");
    }

}
