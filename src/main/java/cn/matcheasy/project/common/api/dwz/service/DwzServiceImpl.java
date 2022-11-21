package cn.matcheasy.project.common.api.dwz.service;

import cn.matcheasy.project.common.api.dwz.mapper.DwzMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class: DwzServiceImpl
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Service
public class DwzServiceImpl implements DwzService {

    @Autowired
    private DwzMapper dwzMapper;

    @Override
    @Cacheable(value = "dwzCache", condition = "", unless = "#result == null || #result.size() == 0")
    public List<Map> selectDwzByCodeOrLongUrl(HashMap paramMap) throws Exception {
        List<Map> mapsMaster = dwzMapper.selectDwzByCodeOrLongUrl(paramMap);
        return mapsMaster;
    }

    @Override
    public void insertDWZ(HashMap paramMap) throws Exception {
        dwzMapper.insertDWZ(paramMap);
    }

    @Override
    public void increaseCallCount(HashMap paramMap) throws Exception {
        dwzMapper.increaseCallCount(paramMap);
    }

}

