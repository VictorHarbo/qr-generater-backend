package com.harbojohnston.qrgeneraterbackend;

import com.harbojohnston.qrgeneraterbackend.database.OrderEntity;
import com.harbojohnston.qrgeneraterbackend.database.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final Logger log = LoggerFactory.getLogger(QrController.class);

    @Autowired
    private OrderService orderService;

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
            byte[] imageBytes = getQrCodeAsByteArray(url);

            HttpHeaders headers = prepareHttpResponse();

            log.info("Returning a QR code for URL: '{}'", url);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("IOException while generating QR code", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/generateQrByOrderNumber")
    public ResponseEntity<byte[]> generateQrFromOrderNumber(@RequestParam() String orderNumber) {
        if (orderNumber == null || orderNumber.isEmpty()) {
            String message = "A QR code cannot be generated without an input order number.";
            log.error(message);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(message.getBytes());
        }


        log.debug("Started generating QR code for order number: '{}'", orderNumber);
        OrderEntity order = orderService.getEntityByUuid(orderNumber);

        if (order.isPaymentCompleted()) {
            try {
                byte[] qrCode = getQrCodeAsByteArray(order.getUrl());
                HttpHeaders headers = prepareHttpResponse();

                log.info("Created QR code for order number: '{}'", orderNumber);
                return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
            } catch (IOException e) {
                log.error("IOException while generating QR code", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.error("Payment for order: '{}' has not been completed.", order.getUuid());
            return new ResponseEntity<>(HttpStatus.PAYMENT_REQUIRED);
        }
    }

    private static HttpHeaders prepareHttpResponse() {
        // Prepare response
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/jpeg");
        headers.setContentDisposition(ContentDisposition.attachment().filename("qrcode.jpeg").build());
        return headers;
    }


    private static byte[] getQrCodeAsByteArray(String url) throws IOException {
        // Create a buffered image with a QR code for the input URL.
        BufferedImage image = QrGenerator.generate(url);

        // Convert BufferedImage to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }
}
