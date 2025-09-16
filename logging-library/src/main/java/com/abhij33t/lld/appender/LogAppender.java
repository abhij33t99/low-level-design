package com.abhij33t.lld.appender;

import com.abhij33t.lld.model.LogMessage;

public interface LogAppender {
    void append(LogMessage message);
}
