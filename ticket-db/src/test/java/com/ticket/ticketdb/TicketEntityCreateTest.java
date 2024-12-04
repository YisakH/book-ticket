package com.ticket.ticketdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ticket.ticketdb.db.Ticket;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TicketEntityCreateTest {
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Test
    public void testTicketMapping() throws Exception {
        String msg =  """
                {
                    "transactionId": "txn_12345",
                    "userId": "yshong",
                    "seatNumber": 5,
                    "status": "BOOKED",
                    "timestamp": "2024-12-03T12:10:11.749497Z"
                }
                """;

        Ticket ticket = objectMapper.readValue(msg, Ticket.class);

        assertEquals("txn_12345", ticket.getTransactionId());
        assertEquals("yshong", ticket.getUserName());
        assertEquals(5, ticket.getSeatNumber());
        assertTrue(ticket.isBooked());
        assertEquals(LocalDateTime.parse("2024-12-03T12:10:11.749497"), ticket.getPurchaseDate());
    }
}
