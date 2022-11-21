package cn.matcheasy.project.common.api.mail.service;

import cn.matcheasy.project.common.api.mail.entity.Email;

/**
 * @class: MailService
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
public interface MailService {

    /**
     * handled by wangjing at 2020/11/23/0023 18:09
     * @desc 发邮件服务
     */
    public void sendMail(Email email) throws Exception;

}