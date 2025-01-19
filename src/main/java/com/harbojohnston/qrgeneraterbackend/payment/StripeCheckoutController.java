package com.harbojohnston.qrgeneraterbackend.payment;

import com.harbojohnston.qrgeneraterbackend.CurrentOrders;
import com.harbojohnston.qrgeneraterbackend.database.OrderService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
public class StripeCheckoutController {

    private static final Logger log = LoggerFactory.getLogger(StripeCheckoutController.class);

    @Autowired
    private OrderService orderService;

    @Value("${stripe.secret.key}")
    private String apiKey;

    @Value("${stripe.prices.current}")
    private String price;

    @Value("${stripe.prices.free}")
    private String priceFree;

    @Value("${stripe.prices.euros.two}")
    private String priceTwoEuros;

    @Value("${returnUrls.success}")
    private String successUrl;

    @Value("${returnUrls.cancel}")
    private String cancelUrl;

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody Map<String, Object> data) throws Exception {
        Stripe.apiKey = apiKey;

        String orderId = UUID.randomUUID().toString();
        String inputUrl = (String) data.get("inputUrl");

        log.info("Input URL is: '{}'", inputUrl);

        Map<String, String> orderMetadata = new HashMap<>();
        // Add orderId to metadata which is sent to stripe
        orderMetadata.put("orderId", orderId);
        // Create the order in the db
        orderService.createNewPayment(orderId, inputUrl);
        log.info("Created order with orderId: '{}'", orderId);

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setPrice(price)
                .setQuantity(Long.valueOf((String) data.get("quantity")))
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .putAllMetadata(orderMetadata)
                .addLineItem(lineItem)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?id=" + orderId)
                .setCancelUrl(cancelUrl)
                .build();

        Session session = Session.create(params);


        Map<String, String> response = new HashMap<>();
        response.put("sessionId", session.getId());
        return response;
    }

}

