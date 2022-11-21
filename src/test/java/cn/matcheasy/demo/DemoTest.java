package cn.matcheasy.demo;

import cn.matcheasy.MatcheasyApplicationTests;
import cn.matcheasy.framework.exception.Try;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @class: DemoTest
 * @author: wangjing
 * @date: 2021/2/23/0023
 * @desc: TODO
 */
@Slf4j
public class DemoTest extends MatcheasyApplicationTests
{
    @Test
    public void test()
    {
        log.debug("【Test】= {}", "DemoTest测试用例");

        Try ret = Try.run(() ->
        {
            int a = 1 / 0;
        });
        System.out.println(ret.isFailure());
        System.out.println(ret.isSuccess());
        System.out.println(ret.getCause());
    }
}
