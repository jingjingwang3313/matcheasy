package cn.matcheasy.project.common.api.mail.entity;

import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * @class: Email
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Data
public class Email implements Serializable
{

    private static final long serialVersionUID = 1L;

    /**
     * 发件人地址
     */
    private String fromAddress;

    /**
     * 收件人地址
     */
    private List<String> toAddress;

    /**
     * 抄送人地址
     */
    private List<String> ccAddress;

    /**
     * 密送人地址
     */
    private List<String> bccAddress;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;

    /**
     * 邮件内容(html)
     */
    private String htmlContent;
    
    /**
     * 附件
     */
    private List<File> attachments;
}
