package com.harbojohnston.qrgeneraterbackend;

import io.nayuki.qrcodegen.QrCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * Generate a simple QR code for an input URL.
 */
public class QrGenerator {
    private static Logger log = LoggerFactory.getLogger(QrGenerator.class);


    /**
     * Create a QR code for input url.
     * @param url to generate QR code from.
     * @return a BufferedImage object containing the QR.
     */
    public static BufferedImage generate(String url) throws IOException {
        validateInputUrl(url);

        QrCode qrCode = QrCode.encodeText(url, QrCode.Ecc.MEDIUM);
        BufferedImage img = toImage(qrCode, 4, 10);

        return img;
    }

    private static BufferedImage toImage(QrCode qr, int scale, int border) {
        return toImage(qr, scale, border, 0xFFFFFF, 0x000000);
    }

    private static BufferedImage toImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {
        Objects.requireNonNull(qr);
        if (scale <= 0 || border < 0) {
            throw new IllegalArgumentException("Value out of range");
        }
        if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale) {
            throw new IllegalArgumentException("Scale or border too large");
        }

        BufferedImage result = new BufferedImage(
                (qr.size + border * 2) * scale,
                (qr.size + border * 2) * scale,
                BufferedImage.TYPE_INT_RGB
        );
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                boolean color = qr.getModule(x / scale - border, y / scale - border);
                result.setRGB(x, y, color ? darkColor : lightColor);
            }
        }
        return result;
    }

    private static void validateInputUrl(String url) {
        int uniCodeCodePoints = url.codePointCount(0, url.length());

        log.info("URL has '{}' unicode code points.", url.codePointCount(0, url.length()));
        if (uniCodeCodePoints > 738){
            String message = "URL is too long, we cannot guarantee a working QR code.";
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }
}
