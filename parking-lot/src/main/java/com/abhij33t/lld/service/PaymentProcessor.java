package com.abhij33t.lld.service;

import com.abhij33t.lld.enums.PaymentStatus;
import com.abhij33t.lld.model.Ticket;
import com.abhij33t.lld.strategy.payment.PaymentStrategy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentProcessor {
    private final PaymentStrategy strategy;

    public boolean pay(Ticket ticket, double amount) {
        boolean success = strategy.processPayment(ticket, amount);
        if (success) {
            ticket.setPaymentStatus(PaymentStatus.SUCCESS);
        } else {
            ticket.setPaymentStatus(PaymentStatus.FAILED);
            System.out.println("Payment failed for ticket: "+ticket.getTicketId());
        }
        return success;
    }
}
