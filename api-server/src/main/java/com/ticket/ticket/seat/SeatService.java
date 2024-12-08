package com.ticket.ticket.seat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.ticket.constants.SeatPurchaseStatus;
import com.ticket.ticket.redis.RedisSeatRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ticket.ticket.constants.SeatPurchaseStatus.SUCCESS;

@Service
public class SeatService {

    private final int maxSeats;
    private final RedisSeatRepository redisRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SeatService(RedisSeatRepository redisRepository, KafkaTemplate<String, String> kafkaTemplate,
                       @Value("${ticket.max-seat-size}") int maxSeats) {
        this.redisRepository = redisRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.maxSeats = maxSeats;
    }

    protected SeatInfo getRemainSeats(){
        SeatInfo seatInfo = new SeatInfo();
        seatInfo.setSeatNumbers(redisRepository.getSeatList());
        seatInfo.setMaxSeatNum(maxSeats);
        seatInfo.setRemainSeatNum(redisRepository.getRemainSeatNum());

        return seatInfo;
    }

    protected SeatPurchaseStatus buySeats(int seatNumber, String userId){
        SeatPurchaseStatus status = redisRepository.buySeat((long) seatNumber);

        if (status != SUCCESS){
            return status;
        }

        String transactionId = "txn_" + UUID.randomUUID();  // 트랜잭션 ID 생성
        sendMsg(seatNumber, "BOOKED", userId, transactionId);

        return SUCCESS;
    }

    private void sendMsg(int seatNumber, String status, String userId, String transactionId){
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
}
