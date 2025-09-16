package com.abhij33t.lld.config;

import com.abhij33t.lld.appender.LogAppender;
import com.abhij33t.lld.enums.LogLevel;
import com.abhij33t.lld.handler.*;

public class LogHandlerConfig {
    private static final LogHandler trace = new TraceHandler();
    private static final LogHandler debug = new DebugHandler();
    private static final LogHandler info = new InfoHandler();
    private static final LogHandler warn = new WarnHandler();
    private static final LogHandler error = new ErrorHandler();
    private static final LogHandler fatal = new FatalHandler();

    public static LogHandler build() {
        trace.setNext(debug);
        debug.setNext(info);
        info.setNext(warn);
        warn.setNext(error);
        error.setNext(fatal);

        return trace;
    }

    public static void addAppenderForLevel(LogLevel level, LogAppender appender) {
        switch (level) {
            case TRACE -> trace.subscribe(appender);
            case DEBUG -> debug.subscribe(appender);
            case INFO -> info.subscribe(appender);
            case WARN -> warn.subscribe(appender);
            case ERROR -> error.subscribe(appender);
            case FATAL -> fatal.subscribe(appender);
        }
    }
}
