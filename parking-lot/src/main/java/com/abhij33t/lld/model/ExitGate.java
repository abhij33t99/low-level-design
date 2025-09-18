package com.abhij33t.lld.model;

import com.abhij33t.lld.enums.GateType;
import com.abhij33t.lld.enums.PaymentMode;
import com.abhij33t.lld.service.ParkingLotService;

import java.time.LocalDateTime;

public class ExitGate extends Gate{
    public ExitGate(String id) {
        super(id);
    }

    @Override
    public GateType getType() {
        return GateType.EXIT;
    }

    public void unparkVehicle(String ticketId, LocalDateTime exitTime, PaymentMode paymentMode) {
        ParkingLotService.getInstance().unparkVehicle(ticketId, exitTime, paymentMode);
    }
}
