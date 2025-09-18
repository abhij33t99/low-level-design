package com.abhij33t.lld.model;

import com.abhij33t.lld.enums.GateType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Gate {
    protected final String id;

    public abstract GateType getType();
}
