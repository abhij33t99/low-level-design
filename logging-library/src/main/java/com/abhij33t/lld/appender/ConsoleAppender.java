package com.abhij33t.lld.appender;

import com.abhij33t.lld.formatter.LogFormatter;
import com.abhij33t.lld.model.LogMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsoleAppender implements LogAppender{
    private final LogFormatter formatter;

    @Override
    public void append(LogMessage message) {
        System.out.println(formatter.format(message));
    }
}
