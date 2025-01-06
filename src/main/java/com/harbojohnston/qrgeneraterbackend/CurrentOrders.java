package com.harbojohnston.qrgeneraterbackend;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class CurrentOrders {
    private static final Logger log = LoggerFactory.getLogger(CurrentOrders.class);

    /**
     * Thread-safe in-memory object containing a map of {OrderIds, processedStatus} where orderId is a UUID and processedStatus is a boolean representing if the order has been completed.
     */
    private static final ConcurrentHashMap<String, Boolean> currentOrders = new ConcurrentHashMap<>();

    /**
     * Validate that input key is present in currentOrders.
     * @param key UUID to look for in currentOrders.
     * @return true if payment has completed. Otherwise, returns false. Also returns false if key isn't present.
     */
    public static boolean validate(String key) {
        if (!currentOrders.containsKey(key)) {
            log.warn("No order could be found for key: '{}'", key);
            return false;
        }
        // Validate that the payment has been completed.
        return currentOrders.get(key);
    }

    public static void addOrder(String key, Boolean value) {
        currentOrders.put(key, value);
    }

    public static void removeOrder(String key) {
        currentOrders.remove(key);
    }
    public static void updateOrder(String key) {
        currentOrders.put(key, true);
    }
}
