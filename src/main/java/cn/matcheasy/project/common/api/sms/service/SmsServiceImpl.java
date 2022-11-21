package cn.matcheasy.project.common.api.sms.service;

import cn.matcheasy.project.common.api.sms.util.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @class: SmsServiceImpl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 发短信服务实现
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public String sendSMS(String text, String mobile) throws Exception {
        // 1单发,2群发(暂时不使用)
        return SmsUtil.sendSMS(text, mobile, 1);
    }

}
