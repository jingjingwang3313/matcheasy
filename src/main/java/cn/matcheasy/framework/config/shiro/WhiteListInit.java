package cn.matcheasy.framework.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * @class WhiteListInit
 * @author: wangjing
 * @date: 2020/11/9/0009
 * @desc: TODO
 */
@Slf4j
public class WhiteListInit {

    // 字符编码
    private static String ENCODING = "UTF-8";

    /**
     * handled by wangjing at 2020/11/9/0009 14:53
     * @desc 读取url白名单
     */
    public static LinkedList<String> readWhiteListFile() {
        LinkedList<String> whiteList = null;
        InputStreamReader read = null;
        BufferedReader br = null;
        try {
            // 读取文件输入流
            InputStream stream = ClassUtils.getDefaultClassLoader().getResourceAsStream("whiteList.txt");
            read = new InputStreamReader(stream, ENCODING);
            whiteList = new LinkedList<String>();
            // StringBuffer sb = new StringBuffer();
            // BufferedReader是包装类，先把字符读到缓存里，到缓存满了，再读入内存，提高了读的效率。
            br = new BufferedReader(read);
            String txt = null;
            // 读取文件，将文件内容放入到set中
            while ((txt = br.readLine()) != null) {
                whiteList.add(txt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                if (br != null) {
                    br.close();
                }
                if (read != null) {
                    read.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("WhiteListInit Success !");
        return whiteList;
    }
}