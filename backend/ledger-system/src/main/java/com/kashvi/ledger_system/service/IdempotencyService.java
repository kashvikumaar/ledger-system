package com.kashvi.ledger_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class IdempotencyService {

    private static final String PREFIX = "idempotency:";
    private static final String IN_FLIGHT = "IN_FLIGHT";
    private static final long TTL_HOURS = 24;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean claim(String key) {

        Boolean set = redisTemplate.opsForValue()
                .setIfAbsent(
                        PREFIX + key,
                        IN_FLIGHT,
                        TTL_HOURS,
                        TimeUnit.HOURS
                );

        return Boolean.TRUE.equals(set);
    }

    public boolean isInFlight(String key) {

        String value = redisTemplate.opsForValue()
                .get(PREFIX + key);

        return IN_FLIGHT.equals(value);
    }

    public void store(String key, String result) {

        redisTemplate.opsForValue()
                .set(
                        PREFIX + key,
                        result,
                        TTL_HOURS,
                        TimeUnit.HOURS
                );
    }

    public String getStoredResult(String key) {

        return redisTemplate.opsForValue()
                .get(PREFIX + key);
    }

    public void delete(String key) {
        redisTemplate.delete(PREFIX + key);
    }
}