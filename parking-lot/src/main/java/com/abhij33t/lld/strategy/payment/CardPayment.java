package com.abhij33t.lld.strategy.payment;

import com.abhij33t.lld.model.Ticket;

public class CardPayment implements PaymentStrategy{
    @Override
    public boolean processPayment(Ticket ticket, double amount) {
        System.out.println("Paid ₹" + amount + " for ticket " + ticket.getTicketId() + " via Card.");
        return true;
    }
}
