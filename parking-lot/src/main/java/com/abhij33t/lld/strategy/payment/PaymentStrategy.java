package com.abhij33t.lld.strategy.payment;

import com.abhij33t.lld.model.Ticket;

public interface PaymentStrategy {
    boolean processPayment(Ticket ticket, double amount);
}
