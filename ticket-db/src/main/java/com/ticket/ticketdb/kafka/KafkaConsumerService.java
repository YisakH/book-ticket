package com.ticket.ticketdb.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.ticketdb.db.Ticket;
import com.ticket.ticketdb.db.TicketService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final TicketService ticketService;

    public KafkaConsumerService(ObjectMapper objectMapper, TicketService ticketService) {
        this.objectMapper = objectMapper;
        this.ticketService = ticketService;
    }

    @KafkaListener(topics ="test-topic", groupId = "ticket-db")
    public void consume(String message) {
        try{
            Ticket ticket = objectMapper.readValue(message, Ticket.class);
            ticketService.saveTicket(ticket);
        } catch (Exception e){
            System.out.println("Error parsing ticket: " + e.getMessage());
        }
    }
}
