package com.example.laundrobook;

import java.util.ArrayList;

public class Account{
    private String username;    // username
    //    private Integer roomId;     // room id
    private int passwordHash;    // password
    private final ArrayList<String> bookings = new ArrayList<>();  // bookings

    private int userBookings;

    // Constructor
    public Account() {}
    //^this cannot be deleted. Otherwise, the app will crash

    public Account(String username, int passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        //this.roomId = roomId;
        this.userBookings = 0;

    }

    public String getUsername() {
        return username;
    }

    public int getPasswordHash() {
        return passwordHash;
    }

    public Boolean Check_password(int passwordHash) {
        try{
            return this.getPasswordHash() == passwordHash;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<String> getBookings() {
        return bookings;
    }

    public void addBookings(String machineName) {
        bookings.add(machineName);
    }

    public void removeBookings(String machineName) {
        bookings.remove(machineName);
    }

    public int getUserBookings() {
        return this.userBookings;
    }

    public void setUserBookings() {
        userBookings = getBookings().size();
    }

    public void reduceUserBookings() {
        userBookings = getUserBookings()-1;
    }
}
