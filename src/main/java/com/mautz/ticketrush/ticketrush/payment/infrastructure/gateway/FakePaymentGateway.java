package com.mautz.ticketrush.ticketrush.payment.infrastructure.gateway;

import java.math.BigDecimal;

import com.mautz.ticketrush.ticketrush.payment.application.port.out.PaymentGatewayPort;
import org.springframework.stereotype.Component;

/** Stand-in for a real provider (Stripe, ...): approves everything except the token "tok_declined". */
@Component
class FakePaymentGateway implements PaymentGatewayPort {

    @Override
    public boolean charge(BigDecimal amount, String cardToken) {
        return !"tok_declined".equals(cardToken);
    }
}
