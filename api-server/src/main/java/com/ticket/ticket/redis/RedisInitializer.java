package com.ticket.ticket.redis;

import com.ticket.ticket.constants.RedisKey;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisInitializer {

    private final RedisTemplate<String, Long> redisTemplate;
    private final int maxSeatSize;

    @Autowired
    public RedisInitializer(@Value("${ticket.max-seat-size}") int maxSeatSize, RedisTemplate<String, Long> redisTemplate) {
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

        redisTemplate.opsForHash()
                .put(RedisKey.SEAT_NUMBER_INFO.getKey(),
                        RedisKey.REMAIN_SEAT_NUM.getKey(),
                        String.valueOf(maxSeatSize)); // 문자열로 저장


    }
}