package cn.matcheasy.framework.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @class ThreadValueUtil
 * @author: wangjing
 * @date: 2021/11/6/0006
 * @desc: 线程内存空间
 */
public class ThreadValueUtil {
    public final static ThreadLocal<Map> threadLocal = new ThreadLocal<>();
    
    public static void set(String key, Object value) {
        Map<String, Object> threadMap = threadLocal.get();
        if (null == threadMap) {
            threadMap = new ConcurrentHashMap<>();
            threadLocal.set(threadMap);
        }
        threadMap.put(key, value);
    }
    
    public static Object get(String key) {
        Map<String, Object> threadMap = threadLocal.get();
        return null == threadMap ? null : threadMap.get(key);
    }
    
    public static void main(String[] args) {
        
        /*ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletionService<Set<String>> completionService = new ExecutorCompletionService<>(executorService);
        List<Future<Set<String>>> futures = new ArrayList<>();
        Future<Set<String>> future1 = completionService.submit(new QueryTask());
        futures.add(future1);
        Future<Set<String>> future2 = completionService.submit(new QueryTask2());
        futures.add(future2);
        Set<String> results = new HashSet<>();
        for (int i = 0; i < futures.size(); i++) {
            try {
                Future<Set<String>> future = futures.get(i); // completionService.take();
                Set<String> result = future.get();
                results.addAll(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        results.forEach(System.out::println);
        executorService.shutdown();*/
        
        List<CompletableFuture<Set<String>>> futures = Lists.newArrayList(
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return queryDatabase1();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }),
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return queryDatabase2();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }),
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return queryDatabase3();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();
        List<String> combinedResult = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(Set::stream)
                .collect(Collectors.toList());
        combinedResult.forEach(System.out::println);
        
    }
    
    static class QueryTask implements Callable<Set<String>> {
        @Override
        public Set<String> call() throws Exception {
            System.out.println(Thread.currentThread().getName());
            Thread.sleep(10000);
            System.out.println("sleep 10s");
            return Sets.newHashSet("aaa", "bbb", "ccc");
        }
    }
    
    static class QueryTask2 implements Callable<Set<String>> {
        @Override
        public Set<String> call() throws Exception {
            System.out.println(Thread.currentThread().getName());
            Thread.sleep(20000);
            System.out.println("sleep 20s");
            return Sets.newHashSet("111", "222", "333");
        }
    }
    
    private static Set<String> queryDatabase1() throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        Thread.sleep(5000);
        System.out.println("sleep 5s");
        return Sets.newHashSet("aaa", "bbb", "ccc");
    }
    
    private static Set<String> queryDatabase2() throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        Thread.sleep(10000);
        System.out.println("sleep 10s");
        return Sets.newHashSet("111", "222", "333");
    }
    
    private static Set<String> queryDatabase3() throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        Thread.sleep(20000);
        System.out.println("sleep 20s");
        return Sets.newHashSet("777", "888", "999");
    }
    
}
