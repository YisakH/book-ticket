package com.ticket.ticket.seat;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RedisInitializer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        // 초기 SeatInfo 객체 생성
        SeatInfo seatInfo = new SeatInfo();
        seatInfo.setRemainingSeats(1000); // 예: 초기 좌석 수 설정
        seatInfo.setSeatNumbers(List.of()); // 예: 초기 좌석 번호 설정
        seatInfo.setMaxSeats(1000);

        // Redis에 초기값 저장
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set("seat", seatInfo); // "seat" 키로 저장
        System.out.println("Initial SeatInfo set in Redis.");
    }
}