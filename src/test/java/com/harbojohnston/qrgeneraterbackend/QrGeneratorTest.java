package com.harbojohnston.qrgeneraterbackend;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QrGeneratorTest {

    @Test
    public void testUrlToLong() {
        String url = "This is an obscure long url....................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            QrGenerator.generate(url);
        });

        String expectedMessage = "URL is too long, we cannot guarantee a working QR code.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
