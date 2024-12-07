package com.ticket.ticket.redis;

import com.ticket.ticket.constants.RedisKey;
import com.ticket.ticket.seat.SeatInfo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class RedisInitializer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final int maxSeatSize;

    @Autowired
    public RedisInitializer(@Value("${ticket.max-seat-size}") int maxSeatSize, RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.maxSeatSize = maxSeatSize;
    }

    @PostConstruct
    public void init() {
        // redis에 기존 값 없을경우 초기화하기
        if (redisTemplate.hasKey(RedisKey.SEAT_NUMBER_INFO.getKey())){
            System.out.println("Redis has key seat. Redis init process exit...");
            return;
        }

        redisTemplate.opsForValue().set(RedisKey.REMAIN_SEAT_NUM.getKey(), maxSeatSize);
    }
}