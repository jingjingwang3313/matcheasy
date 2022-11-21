package cn.matcheasy.project.modules.demo.matcheasy;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @class: MatcheasyCtl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Api(tags = "html示例")
@Controller
@RequestMapping("/anon")
public class MatcheasyCtl {

    @ApiOperation(value = "首页", notes = "")
    @GetMapping("/index")
    public String toIndex(HttpServletRequest request) throws Exception {
        return "index";
    }

}
