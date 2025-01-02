package com.harbojohnston.qrgeneraterbackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class QrController {
    private static Logger log = LoggerFactory.getLogger(QrController.class);

    @GetMapping("/generateQr")
    public ResponseEntity<byte[]> generateQr(@RequestParam() String url) {
        if (url == null || url.isEmpty()) {
            String message = "A QR code cannot be generated without an input URL.";
            log.error(message);

            // If an error occurs, return the error message as a response
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(message.getBytes());
        }

        int uniCodeCodePoints = url.codePointCount(0, url.length());
        if (uniCodeCodePoints > 738){
            String message = "URl is too long, we cannot guarantee a working QR code.";
            log.error(message);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(message.getBytes());
        }

        try {
            // Create a buffered image with a QR code for the input URL.
            BufferedImage image = QrGenerator.generate(url);

            // Convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", baos);
            byte[] imageBytes = baos.toByteArray();

            // Prepare response
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "image/jpeg");
            headers.setContentDisposition(ContentDisposition.attachment().filename("qrcode.jpeg").build());

            log.info("Returning a QR code for URL: '{}'", url);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("IOException while generating QR code", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
