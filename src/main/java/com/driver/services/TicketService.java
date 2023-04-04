package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db

        Train train = trainRepository.findById(bookTicketEntryDto.getTrainId()).get();


        int noOfAvailableTickets = train.getNoOfSeats() - train.getBookedTickets().size();
        if (noOfAvailableTickets < bookTicketEntryDto.getNoOfSeats()){
            throw new Exception("Less tickets are available");
        }

        String listOfStations = train.getRoute();
        String[] listOfStationsArr = listOfStations.split(",");
        boolean isSourceStationPresent = false;
        boolean isDestinationStationPresent = false;
        for (String stationName : listOfStationsArr){
            if (stationName.equals(bookTicketEntryDto.getFromStation().toString())){
                isSourceStationPresent = true;
            }

            if (stationName.equals(bookTicketEntryDto.getToStation().toString())){
                isDestinationStationPresent = true;
            }
        }

        if (!isSourceStationPresent && !isDestinationStationPresent ||
                !isSourceStationPresent && isDestinationStationPresent ||
                isSourceStationPresent && !isDestinationStationPresent){
                   throw new Exception("Invalid stations");
        }

        List<Ticket> bookedTickets = train.getBookedTickets();
        Ticket ticket = new Ticket();

        //List of passengers
        List<Passenger> passengerList = new ArrayList<>();
        List<Integer> listOfPassengerId = bookTicketEntryDto.getPassengerIds();
        for (Integer passengerId : listOfPassengerId){
            Passenger passenger = passengerRepository.findById(passengerId).get();
            passengerList.add(passenger);
        }
        ticket.setPassengersList(passengerList);
        ticket.setTrain(train);
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        //Fare calculation
        int travelledStations = 0;
        int startStationIndex = -1;
        int endStationIndex = -1;
        for (int i = 0;i<listOfStationsArr.length;i++){
            if (listOfStationsArr[i].equals(bookTicketEntryDto.getFromStation().toString())){
                startStationIndex = i;
            }
            if (listOfStationsArr[i].equals(bookTicketEntryDto.getToStation().toString())){
                endStationIndex = i;
            }
        }
        travelledStations = endStationIndex - startStationIndex;
        int totalFare = passengerList.size() * travelledStations*300;
        ticket.setTotalFare(totalFare);

        ticket.setToStation(bookTicketEntryDto.getToStation());

        bookedTickets.add(ticket);

        Passenger passenger = passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get();
        List<Ticket> ticketsBookedByPassenger = passenger.getBookedTickets();
        ticketsBookedByPassenger.add(ticket);


        ticketRepository.save(ticket);

        return ticket.getTicketId();

    }
}
