package com.mautz.ticketrush.ticketrush.notification.infrastructure.qr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mautz.ticketrush.ticketrush.notification.application.port.out.QrCodeGeneratorPort;
import org.springframework.stereotype.Component;

@Component
class ZxingQrCodeGenerator implements QrCodeGeneratorPort {

    private static final int SIZE = 300;

    @Override
    public byte[] generatePng(String content) {
        try {
            var matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE);
            var out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("Could not generate QR code", e);
        }
    }
}
