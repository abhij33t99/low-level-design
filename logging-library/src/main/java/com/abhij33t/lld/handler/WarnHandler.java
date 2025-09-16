package com.abhij33t.lld.handler;

import com.abhij33t.lld.enums.LogLevel;

public class WarnHandler extends LogHandler{
    @Override
    public boolean canHandle(LogLevel level) {
        return level == LogLevel.WARN;
    }
}
