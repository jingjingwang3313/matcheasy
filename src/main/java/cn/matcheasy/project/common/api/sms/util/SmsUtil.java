package cn.matcheasy.project.common.api.sms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @class: SmsUtil
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
public class SmsUtil {

    /**
     * 发送短信
     * @param text   发送内容
     * @param mobile 要发送的手机号码（多个英文逗号分隔）最大提交不能超过1000个。
     * @param type   1单发,速度快;2群发,速度慢
     * @return 返回null表示成功，有内容则是错误消息
     */
    public static String sendSMS(String text, String mobile, int type) throws Exception {
        log.info("短信##type=" + type + "    mobile##=" + mobile + "    text=##" + text);
        if (StringUtils.isEmpty(text) || StringUtils.isEmpty(mobile)) {
            return "手机号或短信内容不能为空！";
        }
        //发送短信地址
        String urlstr = "http://sdk8.interface.sudas.cn/z_mdsmssend.php";
        //单发
        String sn1 = "SUD-KEY-010-00385";                                  //账号
        String pwd1 = "FB05FF3F55135A4FDBFA965D5BFDA8AE";                  //密码
        //群发
        String sn2 = "SUD-KEY-010-00369";                                  //账号
        String pwd2 = "FB05FF3F55135A4FDBFA965D5BFDA8AE";                  //密码

        String content = java.net.URLEncoder.encode(text, "utf-8");   //在提交时必须通过urlencode做编码处理。
        String scode = "1";      //1表示UTF-8，2表示GB2312
        String stype = "1";      //默认接口参数值为1
        String ssafe = "2";      //校验标识 必填 启用则配合MD5key密钥对内容进行加密。1表示启用。2表示禁用.
        // String Md5Sign="";    //如果启用ssafe，此参数值为将接口其他参数按照要求通过MD5加密得到的32位字符串。加密顺序：MD5(sn|pwd|mobile|content|ext|rrid|stime|stype|ssafe|scode|md5key)
        // String Md5key= "md5密钥";//ssafe启用时必填。配置Md5Sign参数时，MD5加密使用。联系客服人员索取。
        String stime = "";       //定时时间  参数必须提交，值可为空。值为空表示立即发送。参数格式2011-8-22 13:37:35
        String rrid = "";        //唯一标识	参数必须提交，值可为空。值非空是接口返回rrid，需保证唯一性。最长16位
        String ext = "";         //由服务商提供。
        String debug = "0";      //调试标识  用作调试接口。0时要发短信。值为1时表示接口调试，只记录提交数据，返回提交结果（此结果与正式发送结果返回值一致）。正式提交不用配置此参数。
        StringBuffer postData = new StringBuffer();
        if (type == 1) {//单发
            postData.append("sn=").append(sn1).append("&pwd=").append(pwd1);
        } else if (type == 2) {//群发
            postData.append("sn=").append(sn2).append("&pwd=").append(pwd2);
        } else {
            return "请指定短信发送类型!";
        }
        //组织发送内容
        postData.append("&scode=").append(scode).append("&stype=").append(stype)
                .append("&ssafe=").append(ssafe).append("&stime=").append(stime)
                .append("&rrid=").append(rrid).append("&ext=").append(ext)
                .append("&debug=").append(debug).append("&content=").append(content)
                .append("&mobile=").append(mobile);
        log.info(postData.toString());
        //发送POST请求
        URL url = new URL(urlstr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Length", "" + postData.length());
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
        out.write(postData.toString());
        out.flush();
        out.close();
        //获取响应状态
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            log.info("connect failed!");
            return "发送短信连接失败";
        }
        //获取响应内容体
        String line, result = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        while ((line = in.readLine()) != null) {
            result += line;
        }
        in.close();
        return getReturnMsg(result);

    }


    /**
     * 返回消息
     * @param ret
     * @return
     */
    public static String getReturnMsg(String ret) {
        if ("-2".equals(ret)) {
            return ret + ":ISP帐号/密码不正确 1.序列号未注册2.密码加密不正确3.密码已被修改 ";
        } else if ("-4".equals(ret)) {
            return ret + ":短信余额不足";
        } else if ("-5".equals(ret)) {
            return ret + ":短信数据格式错误";
        } else if ("-6".equals(ret)) {
            return ret + ":短信参数有误";
        } else if ("-7".equals(ret)) {
            return ret + ":短信权限受限 该序列号是否已经开通了调用该方法的权限";
        } else if ("-8".equals(ret)) {
            return ret + ":短信流量控制错误";
        } else if ("-9".equals(ret)) {
            return ret + ":短信扩展码权限错误 该序列号是否已经开通了扩展子号的权限";
        } else if ("-10".equals(ret)) {
            return ret + ":短信内容过长";
        } else if ("-12".equals(ret)) {
            return ret + ":发送短信序列号状态错误 序列号是否被禁用";
        } else if ("-13".equals(ret)) {
            return ret + ":短信内容包含关键字";
        } else if ("-14".equals(ret)) {
            return ret + ":发送短信服务器写文件失败";
        } else if ("-15".equals(ret)) {
            return ret + ":短信文件内容base64编码错误";
        } else if ("-17".equals(ret)) {
            return ret + ":发送短信没有权限";
        } else if ("-18".equals(ret)) {
            return ret + ":上次提交短信没有等待返回不能继续提交";
        } else if ("-19".equals(ret)) {
            return ret + ":发送短信禁止同时使用多个接口地址";
        } else if ("-20".equals(ret)) {
            return ret + ":短信数据校验错误 MD5Sign值校验有误。加密顺序见参数表";
        } else if ("-21".equals(ret)) {
            return ret + ":手机号码超限制 号码数量超过通道最大限制";
        }
        log.info("发送短信返回结果:" + ret);
        return null;//成功
    }

}
