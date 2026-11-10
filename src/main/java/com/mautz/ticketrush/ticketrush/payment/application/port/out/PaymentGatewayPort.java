package com.mautz.ticketrush.ticketrush.payment.application.port.out;

import java.math.BigDecimal;

public interface PaymentGatewayPort {

    /** @return true if the charge was approved */
    boolean charge(BigDecimal amount, String cardToken);
}
