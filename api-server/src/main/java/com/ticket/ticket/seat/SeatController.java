package com.ticket.ticket.seat;

import com.ticket.ticket.constants.SeatPurchaseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/seat")
@RestController
public class SeatController {
    private final SeatService seatService;

    @Autowired
    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/buy")
    public ResponseEntity<SeatInfo> remain() {
        return ResponseEntity.ok(seatService.getRemainSeats());
    }

    @PutMapping("/buy")
    public ResponseEntity<String> buySeat(@RequestParam int seatNumber, Authentication authentication) {
        String userId = null;
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            userId = jwt.getClaimAsString("preferred_username");  // preferred_username 가져오기
        }
        SeatPurchaseStatus seatStatus = seatService.buySeats(seatNumber, userId);

        switch (seatStatus) {
            case BAD_SEAT:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not enough seats available.");
            case SUCCESS:
                return ResponseEntity.status(HttpStatus.OK).body("Successfully purchased seat.");
            case INSUFFICIENT_SEATS:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient seats.");
            case ALREADY_BOOKED:
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Already Booked.");
            case ERROR:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred.");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("처리되지 않은 에러 발생");
    }

}
