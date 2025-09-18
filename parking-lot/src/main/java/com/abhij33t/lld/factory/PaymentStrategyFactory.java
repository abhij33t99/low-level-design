package com.abhij33t.lld.factory;

import com.abhij33t.lld.enums.PaymentMode;
import com.abhij33t.lld.strategy.payment.CardPayment;
import com.abhij33t.lld.strategy.payment.CashPayment;
import com.abhij33t.lld.strategy.payment.PaymentStrategy;

public class PaymentStrategyFactory {
    public static PaymentStrategy get(PaymentMode mode) {
        return switch (mode) {
            case CARD -> new CardPayment();
            case CASH -> new CashPayment();
        };
    }
}
