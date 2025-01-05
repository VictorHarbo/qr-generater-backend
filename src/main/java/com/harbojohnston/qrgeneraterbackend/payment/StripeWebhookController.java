package com.harbojohnston.qrgeneraterbackend.payment;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    @Value("${stripe.webhook.secret}")
    private String ENDPOINT_SECRET;

    @PostMapping("/webhook")
    public String handleWebhook(HttpServletRequest request) {
        String payload;
        try {
            payload = new BufferedReader(request.getReader()).lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.info("An error occurred while reading payload", e);
            return "Error reading payload";
        }

        String sigHeader = request.getHeader("Stripe-Signature");

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, ENDPOINT_SECRET);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
                log.info("Payment successful for session: " + session.getId());
                // Handle successful payment
            }

        } catch (Exception e) {
            log.info("An error occurred while processing payload", e);
            return "Webhook signature verification failed";
        }

        log.info("Webhook processed for request: '{}' ", request);
        return "Webhook processed";
    }
}

