package com.mautz.ticketrush.ticketrush.notification.application.port.out;

import java.util.Map;

public interface EmailSenderPort {

    /** @param attachments file name to PNG bytes */
    void send(String to, String subject, String body, Map<String, byte[]> attachments);
}
