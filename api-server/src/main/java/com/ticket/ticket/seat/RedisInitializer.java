package com.ticket.ticket.seat;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.shaded.io.opentelemetry.proto.metrics.v1.SummaryDataPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RedisInitializer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${max-seat-size}")
    private int maxSeatSize;

    @PostConstruct
    public void init() {
        // redis에 기존 값 없을경우 초기화하기
        if (redisTemplate.hasKey("seat")){
            return;
        }
        // 초기 SeatInfo 객체 생성
        SeatInfo seatInfo = new SeatInfo();
        seatInfo.setRemainingSeats(maxSeatSize); // 초기 좌석 수
        seatInfo.setSeatNumbers(new HashSet<>()); //
        seatInfo.setMaxSeats(maxSeatSize);

        // Redis에 초기값 저장
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set("seat", seatInfo); // "seat" 키로 저장
        System.out.println("Initial SeatInfo set in Redis.");
    }
}