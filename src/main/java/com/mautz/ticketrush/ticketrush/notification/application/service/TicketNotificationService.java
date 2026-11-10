package com.mautz.ticketrush.ticketrush.notification.application.service;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mautz.ticketrush.ticketrush.auth.application.port.in.UserQueryApi;
import com.mautz.ticketrush.ticketrush.notification.application.port.out.EmailSenderPort;
import com.mautz.ticketrush.ticketrush.notification.application.port.out.QrCodeGeneratorPort;
import com.mautz.ticketrush.ticketrush.shared.event.ReservationConfirmedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Emails the QR tickets once the booking transaction has committed, without slowing down the request. */
@Service
class TicketNotificationService {

    private static final Logger log = LoggerFactory.getLogger(TicketNotificationService.class);

    private final UserQueryApi users;
    private final QrCodeGeneratorPort qr;
    private final EmailSenderPort email;

    TicketNotificationService(UserQueryApi users, QrCodeGeneratorPort qr, EmailSenderPort email) {
        this.users = users;
        this.qr = qr;
        this.email = email;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void onReservationConfirmed(ReservationConfirmedEvent event) {
        try {
            String to = users.findEmailById(event.userId()).orElse(null);
            if (to == null) {
                return;
            }
            Map<String, byte[]> attachments = new LinkedHashMap<>();
            StringBuilder body = new StringBuilder("Your tickets for " + event.eventName() + ":\n");
            for (var ticket : event.tickets()) {
                attachments.put("ticket-" + ticket.seatLabel() + ".png", qr.generatePng(ticket.code()));
                body.append("- Seat ").append(ticket.seatLabel()).append(" (").append(ticket.code()).append(")\n");
            }
            email.send(to, "Your tickets for " + event.eventName(), body.toString(), attachments);
        } catch (RuntimeException e) {
            log.error("Could not send tickets for reservation {}", event.reservationId(), e);
        }
    }
}
