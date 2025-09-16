package com.abhij33t.lld;

import com.abhij33t.lld.config.LogHandlerConfig;
import com.abhij33t.lld.enums.LogLevel;
import com.abhij33t.lld.handler.LogHandler;
import com.abhij33t.lld.model.LogMessage;

public enum Logger {
    INSTANCE;

    private LogHandler handlerChain;
    private volatile boolean initialized;

    public synchronized void init() {
        if (initialized) return;
        this.handlerChain = LogHandlerConfig.build();
    }

    public void log(LogLevel level, String message) {
        LogMessage logMessage = new LogMessage(level, message, System.currentTimeMillis());
        handlerChain.handle(logMessage);
    }

    public void trace(String message) {
        log(LogLevel.TRACE, message);
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void fatal(String message) {
        log(LogLevel.FATAL, message);
    }

}
