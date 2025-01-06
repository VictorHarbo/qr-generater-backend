package com.harbojohnston.qrgeneraterbackend.payment;

import com.harbojohnston.qrgeneraterbackend.CurrentOrders;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
public class StripeCheckoutController {

    private static final Logger log = LoggerFactory.getLogger(StripeCheckoutController.class);

    @Value("${stripe.secret.key}")
    private String apiKey;

    @Value("${stripe.prices.free}")
    private String priceFree;

    @Value("${stripe.prices.euros.two}")
    private String priceTwoEuros;

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody Map<String, Object> data) throws Exception {
        Stripe.apiKey = apiKey;

        log.info("Input URL is: '{}'", data.get("inputUrl"));

        Map<String, String> orderMetadata = new HashMap<>();
        String orderId = UUID.randomUUID().toString();
        orderMetadata.put("orderId", orderId);
        CurrentOrders.addOrder(orderId, false);


        log.debug("Received data with id: '{}' and quantity: '{}'. Created order with orderId: '{}'"
                , data.get("id"),  data.get("quantity"), orderId);

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                // 2 euros
                // .setPrice(priceTwoEuros)
                // Free
                .setPrice(priceFree)
                .setQuantity(Long.valueOf((String) data.get("quantity")))
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .putAllMetadata(orderMetadata)
                .addLineItem(lineItem)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/success?id=" + orderId)
                .setCancelUrl("http://localhost:5173/")
                .build();

        Session session = Session.create(params);


        Map<String, String> response = new HashMap<>();
        response.put("sessionId", session.getId());
        return response;
    }

}

