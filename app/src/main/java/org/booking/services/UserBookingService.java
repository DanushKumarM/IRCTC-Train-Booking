package org.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.booking.entities.Ticket;
import org.booking.entities.Train;
import org.booking.entities.User;
import org.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService {

    private Ticket ticket;
    private User user;


    private List<Ticket> ticketsBooked;
    private List<User> userList;

    private  ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_PATH = "app/src/main/java/org/booking/localDb/users.json";



    public UserBookingService(User user1) throws IOException {
        this.user=user1;
        loadUsers();

        }
        public  UserBookingService()throws  IOException{
           loadUsers();
        }

        public void loadUsers() throws  IOException{
            userList = objectMapper.readValue(new File(USERS_PATH), new TypeReference<List<User>>() {});
        }
        public Boolean loginUser(){
            Optional<User> foundUser = userList.stream().filter(user1 -> {
                return user1.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
            }).findFirst();
            return  foundUser.isPresent();
        }

        public  Boolean signUp(User user1) {
            try {
                userList.add(user1);
                saveUserListToFile();
                return Boolean.TRUE;
            } catch (IOException ex) {
                return Boolean.FALSE;
            }
        }
        private  void saveUserListToFile() throws  IOException {
            File usersFile = new File(USERS_PATH);
            objectMapper.writeValue(usersFile, userList);
        }

        public  void  fetchBooking(){
        user.printTickets();
        }

        public  Boolean cancelBooking(String ticketId) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter the Ticket Id to cancel");
            ticketId = sc.next();

            if (ticketId == null || ticketId.isEmpty()) {
                System.out.println("Ticket Id cannot be Null or Empty ");
                return Boolean.FALSE;
            }
            String finalTicketId = ticketId;

            sc.close();
            boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId));
            if (removed) {
                System.out.println("Ticket with ID " + ticketId + " has been cancelled successfully.");
                return Boolean.TRUE;
            } else {
                System.out.println("Ticket with ID " + ticketId + " not found.");
                return Boolean.FALSE;

            }

        }
    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) {
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true; // Booking successful
                } else {
                    return false; // Seat is already booked
                }
            } else {
                return false; // Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    }



