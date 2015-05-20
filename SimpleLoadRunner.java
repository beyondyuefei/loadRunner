package liq.utils;

import static java.lang.System.currentTimeMillis;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 简单的性能压测
 * 
 * @author yuekuo.liq
 */
public class SimpleLoadRunner {
    /**
     * @param concurrencyCounts 并发数
     * @param times 压测次数
     * @param task 执行的任务
     */
    public static void loadRunner(int concurrencyCounts, int times, final Runnable task) {
        //每次任务执行时间的累积
        final AtomicLong totalTime = new AtomicLong(0);
        //任务执行次数
        final AtomicLong totalCount = new AtomicLong(0);
        //任务执行成功数
        final AtomicLong successCount = new AtomicLong(0);
        //任务执行失败数
        final AtomicLong failureCount = new AtomicLong(0);
        //任务开始执行到任务全部执行结束的总耗时
        long runMills = 0;
        final ExecutorService executor = Executors.newFixedThreadPool(concurrencyCounts);
        final StringWriter outWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(outWriter);
        while ((times--) > 0) {
            final CountDownLatch startSignal = new CountDownLatch(1);
            final CountDownLatch endSignal = new CountDownLatch(concurrencyCounts);
            for (int i = 0; i < concurrencyCounts; i++) {
                executor.submit(new Runnable() {
                    public void run() {
                        try {
                            startSignal.await();
                            long s = currentTimeMillis();
                            task.run();
                            long e = currentTimeMillis();
                            totalTime.addAndGet(e - s);
                            successCount.incrementAndGet();
                        } catch (Throwable e) {
                            failureCount.incrementAndGet();
                            e.printStackTrace(printWriter);
                        } finally {
                            totalCount.incrementAndGet();
                            endSignal.countDown();
                        }
                    }
                });
            }
            long startRunmills = currentTimeMillis();
            startSignal.countDown();
            try {
                endSignal.await();
                runMills += currentTimeMillis() - startRunmills;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (successCount.get() == 0) {
            System.out.println("error , all task are failure ...");
            return;
        }
        if (failureCount.get() > 0) {
            System.out.println("the failure task exception stackInfo  start >>>>>>>>>>> ");
            //打出并发测试下的任务异常堆栈信息
            System.err.println(outWriter.toString());
            System.out.println("the failure task exception stackInfo  end  >>>>>>>>>>>>");
        }
        System.out.println();
        System.out.println(String.format("cost time : %s(ms) , concurrency : %s, total: %s, success : %s, failure : %s , tps : %s , average time : %s(ms)",
                                runMills, concurrencyCounts, totalCount, successCount.get(),
                                failureCount.get(),
                                successCount.get() / ((double)runMills / 1000),
                                (double) totalTime.get() / successCount.get()));
        System.out.println();
    }
}
