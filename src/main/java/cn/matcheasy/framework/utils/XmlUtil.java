package cn.matcheasy.framework.utils;

import cn.hutool.json.JSONUtil;
import cn.matcheasy.project.modules.demo.es.entity.EsEntity;
import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @class: XmlUtil
 * @author: wangjing
 * @date: 2021/5/11/0011
 * @desc: TODO
 */
public class XmlUtil extends cn.hutool.core.util.XmlUtil
{

    /**
     * 将xml字符串转为bean
     *
     * @param xmlStr xml字符串
     * @param clazz  需要转成的对象
     * @return
     */
    public static <T> T parseXmlStrToBean(String xmlStr, Class<T> clazz)
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(xmlStr));
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将bean转为xml字符串
     *
     * @param obj          待转的对象
     * @param isFormat     是否格式化
     * @param isIgnoreHead 是否忽略头部
     * @param encode       编码方式
     * @return
     */
    public static String parseBeanToXml(Object obj, boolean isFormat, boolean isIgnoreHead, String encode)
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encode);// //编码格式
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, isFormat);// 是否格式化生成的xml串
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, isIgnoreHead);// 是否省略xml头声明信息
            // 不进行转义字符的处理
            marshaller.setProperty(CharacterEscapeHandler.class.getName(),
                    new CharacterEscapeHandler()
                    {
                        @Override
                        public void escape(char[] ch, int start, int length,
                                           boolean isAttVal, Writer writer) throws IOException
                        {
                            writer.write(ch, start, length);
                        }
                    });

            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            return writer.toString();
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args)
    {
        SysUser user = new SysUser();
        user.setLoginCode("dsadsa");
        user.setPassword("123323");
        user.setUserName("阿尔法狗");

        String s = parseBeanToXml(user, true, false, "GBK");
        System.out.println(s);

        SysUser user1 = parseXmlStrToBean(s, SysUser.class);
        System.out.println(user1.toString());

        //Document document = readXML("C:\\Users\\mydear\\Desktop\\济南土地二级市场工作交接\\王智鑫交接工作\\tt\\2020-05-15-010028723--12--2&2020-05-05.xml");
        //System.out.println(toStr(document));

        System.out.println(JSONUtil.toBean("{\"id\":\"3cd121a1cdd94bb7bc162bf75b0d04d0\",\"itg\":1,\"lg\":2,\"flt\":3.4,\"dub\":5.6,\"str\":\"string\",\"boo\":false,\"date\":\"2021年12月23日\",\"list\":[{\"childDate\":\"20211223145924814\",\"childItg\":1,\"childId\":\"c23a60bec743426e826f12c867a664d8\",\"childLg\":2}]}", EsEntity.class));
    }

}
