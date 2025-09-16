package com.abhij33t.lld;

import com.abhij33t.lld.appender.AsyncFileAppender;
import com.abhij33t.lld.appender.ConsoleAppender;
import com.abhij33t.lld.appender.FileAppender;
import com.abhij33t.lld.config.LogHandlerConfig;
import com.abhij33t.lld.enums.LogLevel;
import com.abhij33t.lld.formatter.PlainTextFormatter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Logger logger = Logger.INSTANCE;
        logger.init();

        LogHandlerConfig.addAppenderForLevel(LogLevel.INFO, new ConsoleAppender(new PlainTextFormatter()));
        LogHandlerConfig.addAppenderForLevel(LogLevel.WARN, new ConsoleAppender(new PlainTextFormatter()));
        LogHandlerConfig.addAppenderForLevel(LogLevel.WARN, new FileAppender(new PlainTextFormatter(), "logs.txt"));
        logger.info("Hi");
        logger.error("err");

        AsyncFileAppender asyncFileAppender = new AsyncFileAppender(new PlainTextFormatter(), "trace-logs.txt", 1000, 4);
        LogHandlerConfig.addAppenderForLevel(LogLevel.TRACE, asyncFileAppender);

        ExecutorService pool = Executors.newCachedThreadPool();
            int tasks = 5000;
            IntStream.range(0, tasks).forEach(i -> pool.submit(() -> {
                logger.trace("trace event : "+i);
            }));


        pool.shutdown();
        if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
            pool.shutdownNow();
        }

        TimeUnit.SECONDS.sleep(5);
        asyncFileAppender.close();

    }
}
