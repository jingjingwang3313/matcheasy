package cn.matcheasy.project.common.api.dwz.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class: DwzService
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
public interface DwzService {

    List<Map> selectDwzByCodeOrLongUrl(HashMap paramMap) throws Exception;

    void insertDWZ(HashMap paramMap) throws Exception;

    void increaseCallCount(HashMap paramMap) throws Exception;
}
