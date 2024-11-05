package com.ticket.ticket.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RequestMapping("/seat")
@RestController
public class SeatController {
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public SeatController(RedisTemplate<String, Object> redisTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMsg(String msg){
        kafkaTemplate.send("test-topic", msg);
    }

    @GetMapping("/get")
    public SeatInfo remain() {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        return (SeatInfo) operations.get("seat");
    }

    @PutMapping("/buy")
    public ResponseEntity<String> buySeat(@RequestParam int seatNumber){
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        SeatInfo seatInfo = (SeatInfo) operations.get("seat");


        assert seatInfo != null;
        if (seatInfo.setSeatBooked(seatNumber) < 0){
            return ResponseEntity.ok("자리가 이미 예매되었습니다.");
        }

        operations.set("seat", seatInfo);
        sendMsg(seatNumber + "가 예약 완료되었습니다.");


        return ResponseEntity.ok(seatNumber + " 자리가 예매되었습니다.");
    }

}
