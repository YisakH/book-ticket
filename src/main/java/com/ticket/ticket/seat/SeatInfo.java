package com.ticket.ticket.seat;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SeatInfo {
    private int remainingSeats;
    private int maxSeats;
    private List<Integer> seatNumbers;

    public void decreaseRemainingSeats() {
        remainingSeats--;
    }

    public int setSeatBooked(int seatNumber) {
        if (seatNumbers.contains(seatNumber)) {
            return -1;
        }
        seatNumbers.add(seatNumber);
        remainingSeats--;

        return 0;
    }
}
