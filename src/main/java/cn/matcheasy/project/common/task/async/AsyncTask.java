package cn.matcheasy.project.common.task.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * @class: AsyncTask
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Slf4j
@Component
public class AsyncTask {

    @Async("threadPoolExecutor")
    public Future<String> asyncTask() throws InterruptedException {
        Thread.sleep(1000);
        return new AsyncResult<>("Execute AsyncTask!");
    }

}
