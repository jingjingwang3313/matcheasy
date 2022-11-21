package cn.matcheasy.project.common.task.schedule.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CronMapper {

    @Select("select t_cron from cron limit 1")
    String getCron();

}
