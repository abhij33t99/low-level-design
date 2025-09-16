package com.abhij33t.lld.model;

import com.abhij33t.lld.enums.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LogMessage {
    private LogLevel level;
    private String message;
    private long timestamp;
}
