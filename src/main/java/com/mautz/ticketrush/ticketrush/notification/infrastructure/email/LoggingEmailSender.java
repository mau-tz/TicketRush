package com.mautz.ticketrush.ticketrush.notification.infrastructure.email;

import java.util.Map;

import com.mautz.ticketrush.ticketrush.notification.application.port.out.EmailSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Development adapter: logs instead of sending. Swap for an SMTP / provider adapter in production. */
@Component
class LoggingEmailSender implements EmailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailSender.class);

    @Override
    public void send(String to, String subject, String body, Map<String, byte[]> attachments) {
        log.info("EMAIL to={} subject=\"{}\" attachments={}\n{}", to, subject, attachments.keySet(), body);
    }
}
