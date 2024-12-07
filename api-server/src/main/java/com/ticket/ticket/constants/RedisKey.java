package com.ticket.ticket.constants;

public enum RedisKey {
    SEAT_LIST("seatList"),
    REMAIN_SEAT_NUM("remainSeatNum"),
    SEAT_NUMBER_INFO("seatNumberInfo"),
    LOCK_PREFIX("LockSeat:");

    private final String key;

    RedisKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
