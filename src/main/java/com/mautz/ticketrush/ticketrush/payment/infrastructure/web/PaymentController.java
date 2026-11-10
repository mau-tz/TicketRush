package com.mautz.ticketrush.ticketrush.payment.infrastructure.web;

import java.math.BigDecimal;
import java.util.UUID;

import com.mautz.ticketrush.ticketrush.payment.application.port.in.PayReservationUseCase;
import com.mautz.ticketrush.ticketrush.payment.domain.Payment;
import com.mautz.ticketrush.ticketrush.payment.domain.PaymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
class PaymentController {

    private final PayReservationUseCase pay;

    PaymentController(PayReservationUseCase pay) {
        this.pay = pay;
    }

    @PostMapping
    ResponseEntity<PaymentResponse> pay(@AuthenticationPrincipal Jwt jwt,
            @RequestHeader("Idempotency-Key") String idempotencyKey, @Valid @RequestBody PayRequest request) {
        Payment payment = pay.pay(new PayReservationUseCase.Command(UUID.fromString(jwt.getSubject()),
                request.reservationId(), request.cardToken(), idempotencyKey));
        HttpStatus status = payment.status() == PaymentStatus.APPROVED ? HttpStatus.OK : HttpStatus.PAYMENT_REQUIRED;
        return ResponseEntity.status(status).body(new PaymentResponse(payment.id(), payment.reservationId(),
                payment.amount(), payment.status().name()));
    }

    record PayRequest(@NotNull UUID reservationId, @NotBlank String cardToken) {
    }

    record PaymentResponse(UUID id, UUID reservationId, BigDecimal amount, String status) {
    }
}
