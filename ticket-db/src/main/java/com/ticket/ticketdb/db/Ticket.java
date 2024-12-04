package com.ticket.ticketdb.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Ticket {

    @Id
    @JsonProperty("transactionId")  // Kafka 메시지의 transactionId를 매핑
    private String transactionId;

    @JsonProperty("userId")  // Kafka 메시지의 userId를 userName에 매핑
    private String userName;

    private int seatNumber;

    @JsonProperty("status")  // Kafka 메시지의 status를 booked에 매핑
    private boolean booked;

    @JsonProperty("timestamp")  // Kafka 메시지의 timestamp를 purchaseDate에 매핑
    private LocalDateTime purchaseDate;

    // status 값(Booked 여부)을 처리하는 커스텀 로직
    public void setBooked(String status) {
        this.booked = "BOOKED".equalsIgnoreCase(status);  // 대소문자 무시
    }
}
