package cn.matcheasy.project.common.task.schedule.service;

import cn.matcheasy.project.common.task.schedule.mapper.CronMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CronServiceImpl implements CronService {

    @Autowired
    CronMapper cronMapper;

    @Override
    public String getCron() {
        return cronMapper.getCron();
    }

}
