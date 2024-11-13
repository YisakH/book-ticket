package com.ticket.ticket.seat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public void sendMsg(int seatNumber, String status, String userId, String transactionId){
        Map<String, Object> msg = new HashMap<>();
        msg.put("seatNumber", seatNumber);
        msg.put("status", status);
        msg.put("timestamp", Instant.now().toString());
        msg.put("userId", userId);
        msg.put("transactionId", transactionId);

        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String message = objectMapper.writeValueAsString(msg);
            kafkaTemplate.send("test-topic", message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/buy")
    public ResponseEntity<SeatInfo> remain() {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        return ResponseEntity.ok((SeatInfo) operations.get("seat"));
    }

    @PutMapping("/buy")
    public ResponseEntity<String> buySeat(@RequestParam int seatNumber, Authentication authentication) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        SeatInfo seatInfo = (SeatInfo) operations.get("seat");

        assert seatInfo != null;

        if (seatInfo.getMaxSeats() < seatNumber || seatInfo.getMaxSeats() < 0) {
            return ResponseEntity.badRequest().body("해당 좌석은 예매할 수 없습니다.");
        }
        
        if (seatInfo.setSeatBooked(seatNumber) < 0){
            return ResponseEntity.status(409).body("자리가 이미 예매되었습니다.");
        }

        operations.set("seat", seatInfo);

        String userId = null;
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            userId = jwt.getClaimAsString("preferred_username");  // preferred_username 가져오기
        }
        String transactionId = "txn_" + UUID.randomUUID();  // 트랜잭션 ID 생성
        sendMsg(seatNumber, "BOOKED", userId, transactionId);

        return ResponseEntity.ok(seatNumber + " 자리가 예매되었습니다.");
    }

}
