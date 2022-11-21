package cn.matcheasy.project.common.api.dwz.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class: DwzMapper
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Mapper
public interface DwzMapper {

    List<Map> selectDwzByCodeOrLongUrl(HashMap paramMap);

    void insertDWZ(HashMap paramMap);

    void increaseCallCount(HashMap paramMap);
}
