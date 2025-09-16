package com.abhij33t.lld.formatter;

import com.abhij33t.lld.model.LogMessage;

public interface LogFormatter {
    String format(LogMessage message);
}
