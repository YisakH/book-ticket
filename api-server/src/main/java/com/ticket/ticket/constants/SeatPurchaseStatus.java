package com.ticket.ticket.constants;

public enum SeatPurchaseStatus {
    SUCCESS,            // 성공적으로 구매
    INSUFFICIENT_SEATS, // 잔여 좌석 부족
    BAD_SEAT,
    ALREADY_BOOKED,     // 이미 예약됨
    ERROR               // 기타 에러
}