package cn.matcheasy.project.common.api.mail.service;

import cn.matcheasy.project.common.api.mail.entity.Email;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.Date;

/**
 * @class: MailServiceImpl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.sender}")
    private String sender;

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * handled by wangjing at 2020/11/23/0023 18:09
     * @desc 发邮件(支持群发)
     */
    @Override
    public void sendMail(Email email) throws Exception {
        long start = System.currentTimeMillis();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //true表示需要创建一个multipart message
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(MimeUtility.encodeText(sender) + " <" + username + ">");
        helper.setTo(email.getToAddress().toArray(new String[email.getToAddress().size()]));
        if (StringUtils.isEmpty(email.getSubject())) {
            helper.setSubject("无主题");
        } else {
            helper.setSubject(email.getSubject());
        }
        helper.setText(email.getContent(), true);
        helper.setSentDate(new Date());
        if (null != email.getAttachments() && email.getAttachments().size() > 0) {
            for (File curFile : email.getAttachments()) {
                FileSystemResource file = new FileSystemResource(curFile);
                helper.addAttachment(MimeUtility.encodeWord(file.getFilename(), "utf-8", "B"), file);
            }
        }
        javaMailSender.send(mimeMessage);
        long sendMillTimes = (System.currentTimeMillis() - start) / 1000;
        log.info("邮件发送成功,用时: " + sendMillTimes + " s");
    }

}
