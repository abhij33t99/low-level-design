package com.abhij33t.lld.strategy.payment;

import com.abhij33t.lld.model.Ticket;

public class CashPayment implements PaymentStrategy{
    @Override
    public boolean processPayment(Ticket ticket, double amount) {
        System.out.println("Paid ₹" + amount + " for ticket " + ticket.getTicketId() + " via Cash.");
        return true;
    }
}
