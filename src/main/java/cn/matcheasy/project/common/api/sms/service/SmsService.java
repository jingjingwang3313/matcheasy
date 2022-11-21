package cn.matcheasy.project.common.api.sms.service;

/**
 * @class: SmsService
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 发短信服务
 */
public interface SmsService {

    public String sendSMS(String text, String mobile) throws Exception;

}
