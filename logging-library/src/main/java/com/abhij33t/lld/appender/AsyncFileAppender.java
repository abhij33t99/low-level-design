package com.abhij33t.lld.appender;

import com.abhij33t.lld.formatter.LogFormatter;
import com.abhij33t.lld.model.LogMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

public class AsyncFileAppender implements LogAppender{

    private final LogFormatter formatter;
    private final BufferedWriter writer;
    private final BlockingQueue<LogMessage> queue;
    private final ExecutorService workers;
    private final int workerCount;

    //Sentinel used to signal shutdown to workers
    private static final LogMessage POISON = new LogMessage(null, "__POISON__", -1L);

    private volatile boolean open = true;

    public AsyncFileAppender(LogFormatter formatter, String fileName, int capacity, int workerCount) {
        this.formatter = formatter;

        try{
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) Files.createFile(path);
            this.writer = new BufferedWriter(new FileWriter(fileName, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.queue = new LinkedBlockingQueue<>(capacity);
        ThreadFactory tf = r -> {
            Thread t = new Thread(r, "async-file-appender");
            t.setDaemon(true);
            return t;
        };

        this.workers = Executors.newFixedThreadPool(workerCount, tf);
        this.workerCount = workerCount;
        //submit worker tasks to the pool
        for (int i=0; i<workerCount; i++)
            workers.execute(this::runWorker);
    }

    @Override
    public void append(LogMessage message) {
        if (!open) return;

        try {
            queue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runWorker() {
        try {
            while (true) {
                LogMessage message = queue.take();
                if (message == POISON) break;

                try {
                    String line = formatter.format(message);
                    synchronized (writer) {
                        writer.write(line);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void close() {
        if (!open) return;
        open = false;

        //Signal workers to exit
        for (int i=0; i<workerCount; i++) {
            try {
                queue.put(POISON);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        workers.shutdown();
        try {
            if (!workers.awaitTermination(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            workers.shutdown();
            Thread.currentThread().interrupt();
        }

        synchronized (writer) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
