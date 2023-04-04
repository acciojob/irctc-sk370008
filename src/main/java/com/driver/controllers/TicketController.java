package com.driver.controllers;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    TicketService ticketService;

    @PostMapping("/book")
    public Integer bookTicket(@RequestBody BookTicketEntryDto bookTicketEntryDto){

        try {
            Integer ticketId = ticketService.bookTicket(bookTicketEntryDto);
            return ticketId;
        }catch (Exception e) {
            return null;
        }
    }

}
