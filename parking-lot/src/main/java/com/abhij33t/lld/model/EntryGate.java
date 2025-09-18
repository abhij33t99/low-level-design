package com.abhij33t.lld.model;

import com.abhij33t.lld.enums.GateType;
import com.abhij33t.lld.service.ParkingLotService;

import java.time.LocalDateTime;

public class EntryGate extends Gate{

    public EntryGate(String id) {
        super(id);
    }

    @Override
    public GateType getType() {
        return GateType.ENTRY;
    }

    public Ticket parkVehicle(Vehicle vehicle, LocalDateTime entryTime) {
        return ParkingLotService.getInstance().parkVehicle(vehicle, entryTime);
    }
}
