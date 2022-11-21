package cn.matcheasy.java8;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.matcheasy.project.modules.sysuser.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @class: Java8Test
 * @author: wangjing
 * @date: 2021/4/27/0027
 * @desc: TODO
 */
@Slf4j
public class Java8Test {

    @Test
    public void test() {

        List<Map> queryList = new ArrayList<Map>();

        Map m = new HashMap();
        m.put("name", "zhangsan");
        m.put("age", 18);

        queryList.add(m);

        Map m2 = new HashMap();
        m2.put("name", "lisi");
        m2.put("age", 28);

        queryList.add(m2);

        Optional.ofNullable(queryList).ifPresent(list -> {
            List<String> names = list.stream().filter(map -> MapUtil.getInt(map, "age") > 10).map(map -> MapUtil.getStr(map, "name")).collect(Collectors.toList());
            log.info(names.toString());
            list.forEach(map -> {
                String name = MapUtil.getStr(map, "name");
                log.info(name);
            });
            List<Map> mmp = list.stream().skip(1).limit(2).collect(Collectors.toList());
            log.info(mmp.toString());
        });

        Map map1 = Optional.ofNullable(m).filter(map -> "wangwu".equals(MapUtil.getStr(map, "name"))).orElseGet(() -> {
            log.info("null 时执行!");
            m.put("name", "wangwu");
            m.put("age", 35);
            return m;
        });
        log.info(m.toString());

        Optional<String> opt = Optional.ofNullable(null);//可以放null值
        String x = opt.orElse(getDefaultValue());//无论是否为null都会调用
        log.info("---以上为orElse调用,以下为orElseGet调用---");
        String y = opt.orElseGet(() -> getDefaultValue());//为null时会调用

        //BufferedReader reader = new BufferedReader(new FileReader("E:\\test_stream.txt"));
        //Stream<String> lineStream = reader.lines();
        //lineStream.forEach(System.out::println);

        //InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream("generator.yaml");
        //Yaml yaml = new Yaml();
        //configuration = yaml.loadAs(inputStream, Configuration.class);

        log.info(DateUtil.format(new Date(), "yyyy/MM/dd"));


        //利用java8的stream去重
        List<SysUser> userList = new ArrayList<SysUser>();
        SysUser user = new SysUser();
        user.setUserName("123");
        user.setPassword("aaa");
        userList.add(user);
        SysUser user2 = new SysUser();
        user2.setUserName("123");
        user2.setPassword("bbb");
        userList.add(user2);
        SysUser user3 = new SysUser();
        user3.setUserName("123");
        user3.setPassword("bbb");
        userList.add(user3);

        List uniqueList = userList.stream().distinct().collect(Collectors.toList());
        log.info(uniqueList.toString());

        //获取逻辑核心数，如6核心12线程，那么返回的是12
        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

    }

    public String getDefaultValue() {
        log.info("被调用了!");
        return "默认值";
    }

}
