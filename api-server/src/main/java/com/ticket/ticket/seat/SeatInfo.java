package com.ticket.ticket.seat;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SeatInfo {
    private int remainSeatNum;
    private int maxSeatNum;
    private Set<Long> seatNumbers;
}
