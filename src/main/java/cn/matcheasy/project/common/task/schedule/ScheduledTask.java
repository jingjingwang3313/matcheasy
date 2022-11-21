package cn.matcheasy.project.common.task.schedule;

import cn.matcheasy.project.common.task.async.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @class: ScheduledTask
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Service
public class ScheduledTask {

    @Autowired
    private AsyncTask asyncTask;

    /**
     * Cron表达式参数分别表示：
     *   秒（0~59） 例如0/5表示每5秒
     *   分（0~59）
     *   时（0~23）
     *   月的某天（0~31） 需计算
     *   月（0~11）
     *   周几（ 可填1-7 或 SUN/MON/TUE/WED/THU/FRI/SAT）
     */
    @Scheduled(fixedRate = 6000 * 10, initialDelay = 1000 * 60 * 1)
    //@Scheduled(cron = "0 */1 * * * ?")
    public void doWork() throws ExecutionException, InterruptedException {
        Future<String> retult = asyncTask.asyncTask();
        log.info("Scheduled tasks " + String.valueOf(retult.get()));
    }

}
