package com.harbojohnston.qrgeneraterbackend.payment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harbojohnston.qrgeneraterbackend.CurrentOrders;
import com.harbojohnston.qrgeneraterbackend.database.OrderService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    @Autowired
    private OrderService orderService;

    @Value("${stripe.webhook.secret}")
    private String ENDPOINT_SECRET;

    public String sessionId;

    @PostMapping("/webhook")
    public String handleWebhook(HttpServletRequest request) {
        String orderId = null;
        String payload;
        payload = getPayload(request);

        if (payload == null) return "Error reading payload";

        String sigHeader = request.getHeader("Stripe-Signature");

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, ENDPOINT_SECRET);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
                log.info("Payment successful for session: '{}'", session.getId());

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(payload);

                orderId = jsonNode.get("data")
                        .get("object").get("metadata").get("orderId").asText();

            }

        } catch (NullPointerException e){
            log.error("No order ID was present in the webhook data. Verification failed.");
        }
        catch (Exception e) {
            log.info("An error occurred while processing payload", e);
            return "Webhook signature verification failed";
        }

        if (orderId == null) {
            throw new RuntimeException("No orderId has been extracted.");
        }

        try{
            // Marks order with payment completed
            orderService.updatePaymentCompleted(orderId, true);
            log.info("Updated payment for orderId: '{}'.", orderId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.debug("Webhook processed for request: '{}' ", request);
        return "Webhook processed";
    }

    /**
     * Get payload from http request.
     * @return payload as a string. We expect JSON here.
     */
    private static String getPayload(HttpServletRequest request) {
        String payload;
        try {
            payload = new BufferedReader(request.getReader()).lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.info("An error occurred while reading payload", e);
            return null;
        }
        return payload;
    }
}

