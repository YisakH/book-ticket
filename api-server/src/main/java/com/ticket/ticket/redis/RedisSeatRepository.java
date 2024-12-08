package com.ticket.ticket.redis;

import com.ticket.ticket.constants.RedisKey;
import com.ticket.ticket.constants.SeatPurchaseStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Set;

@Repository
public class RedisSeatRepository {

    private final int maxRetries;
    private final int maxSleepTime;
    private final RedisTemplate<String, Long> redisTemplate;

    public RedisSeatRepository(@Value("${ticket.max-retries}") int maxRetries,
                               @Value("${ticket.max-sleep-time}") int maxSleepTime,
                               RedisTemplate<String, Long> redisTemplate) {
        this.maxRetries = maxRetries;
        this.maxSleepTime = maxSleepTime;
        this.redisTemplate = redisTemplate;
    }

    public Set<Long> getSeatList() {
        return redisTemplate.opsForSet().members(RedisKey.SEAT_LIST.getKey());
    }

    public int getRemainSeatNum(){
        Long val = (Long) redisTemplate.opsForHash()
            .get(RedisKey.SEAT_NUMBER_INFO.getKey(), RedisKey.REMAIN_SEAT_NUM.getKey());

        if (val == null) {
            return -1;
        }
        return val.intValue();
    }

    public SeatPurchaseStatus buySeat(Long seatNumber){
        boolean success = false;
        ValueOperations<String, Long> opsForValue = redisTemplate.opsForValue();
        SetOperations<String, Long> opsForSet = redisTemplate.opsForSet();
        HashOperations<String, String, Long> opsForHash = redisTemplate.opsForHash();

        for (int i = 0; i < maxRetries && !success; i++){
            Boolean locked = opsForValue
                    .setIfAbsent(generateKey(seatNumber), 1L, Duration.ofMillis(300));
            if (locked != null && locked){
                try {
                    Boolean isMember = opsForSet
                            .isMember(RedisKey.SEAT_LIST.getKey(), seatNumber);
                    if (isMember != null && isMember) {
                        return SeatPurchaseStatus.ALREADY_BOOKED;
                    }
                    opsForSet.add(RedisKey.SEAT_LIST.getKey(), seatNumber);
                    opsForHash.increment(RedisKey.SEAT_NUMBER_INFO.getKey(),
                            RedisKey.REMAIN_SEAT_NUM.getKey(), -1);
                    success = true;
                } finally {
                    // 4. 잠금 해제
                    redisTemplate.delete(generateKey(seatNumber));
                }
            }
            else{
                try{
                    Thread.sleep(maxSleepTime);
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    return SeatPurchaseStatus.ERROR;
                }
            }
        }
        if (!success){
            return SeatPurchaseStatus.ERROR;
        }
        return SeatPurchaseStatus.SUCCESS;
    }

    public String generateKey(Long key) {
        return RedisKey.LOCK_PREFIX.getKey() + key;
    }
}
