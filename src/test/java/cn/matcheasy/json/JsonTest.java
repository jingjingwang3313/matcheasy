package cn.matcheasy.json;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @class: jSONtEST
 * @author: wangjing
 * @date: 2021/7/20/0020
 * @desc: TODO
 */
@Slf4j
public class JsonTest {

    @Test
    public void test() {
        String jsonStr = "{\"total\": 1,\"pageSize\": 2000,\"currentPage\": 1,\"info\": [{\"device\": \"BBBB50C7C08B\",\"deviceType\": \"1\",\"installDate\": \"2019-05-20 08:26:28\",\"deviceState\": \"5\",\"isDel\": \"N\",\"position\": null,\"shopEntityId\": \"1D1VJFRU185TOP0AB2M102MEGU001E44\"}]}";
        cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
        List<Map> jsonStr1 = getListByJSONArray(Map.class, JSONUtil.toJsonStr(jsonObject.get("info")));
        jsonStr1.forEach(map -> {
            log.info((String) map.get("device"));
        });
    }

    public static <T> List<T> getListByJSONArray(Class<T> clazz, String jArrayStr) {
        List<T> list = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(jArrayStr);
        if (jsonArray == null || jsonArray.isEmpty()) {
            return list;//nerver return null
        }
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            T t = JSONObject.toJavaObject(jsonObject, clazz);
            list.add(t);
        }
        return list;
    }

}
