package com.abhij33t.lld.handler;

import com.abhij33t.lld.appender.LogAppender;
import com.abhij33t.lld.enums.LogLevel;
import com.abhij33t.lld.model.LogMessage;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class LogHandler {
    @Setter
    protected LogHandler next;
    protected final List<LogAppender> appenders = new CopyOnWriteArrayList<>();

    public void subscribe(LogAppender observer) {
        appenders.add(observer);
    }

    public void notifyObservers(LogMessage message) {
        appenders.forEach(a -> a.append(message));
    }

    public void handle(LogMessage message) {
        if (canHandle(message.getLevel())) {
            notifyObservers(message);
        } else if (next != null){
            next.handle(message);
        }
    }

    public abstract boolean canHandle(LogLevel level);
}
