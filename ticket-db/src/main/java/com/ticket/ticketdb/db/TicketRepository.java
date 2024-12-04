package com.ticket.ticketdb.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserName(String userName);
    List<Ticket> findByUserNameAndBooked(String userName, boolean booked);
}
