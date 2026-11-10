package com.mautz.ticketrush.ticketrush.notification.application.port.out;

public interface QrCodeGeneratorPort {

    byte[] generatePng(String content);
}
