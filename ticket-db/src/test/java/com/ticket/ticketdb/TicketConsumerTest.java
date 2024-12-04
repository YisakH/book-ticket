package com.ticket.ticketdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ticket.ticketdb.db.Ticket;
import com.ticket.ticketdb.db.TicketService;
import com.ticket.ticketdb.kafka.KafkaConsumerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TicketConsumerTest {
    private final TicketService ticketService = mock(TicketService.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final KafkaConsumerService consumer = new KafkaConsumerService(objectMapper, ticketService);

    @Test
    public void testConsume() throws Exception {
        String message = """
                {
                    "transactionId": "txn_12345",
                    "userId": "yshong",
                    "seatNumber": 5,
                    "status": "BOOKED",
                    "timestamp": "2024-12-03T12:10:11.749497Z"
                }
                """;

        consumer.consume(message);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketService, times(1)).saveTicket(captor.capture());

        Ticket capturedTicket = captor.getValue();
        assertEquals("txn_12345", capturedTicket.getTransactionId());
        assertEquals("yshong", capturedTicket.getUserName());
        assertEquals(5, capturedTicket.getSeatNumber());
        assertTrue(capturedTicket.isBooked());
        assertEquals(LocalDateTime.parse("2024-12-03T12:10:11.749497"), capturedTicket.getPurchaseDate());
    }
}
