package com.example.laundrobook;

import androidx.annotation.NonNull;

import java.util.Date;

// class for creating objects to put into firebase
public class Bookings {

    private String accountUsername;
    private String machineBooked;
    private Date dateBooked;

    // Constructor
    public Bookings() {
    }
    public Bookings( String accountUsername, String machineBooked, Date dateBooked) {
        this.accountUsername = accountUsername;
        this.machineBooked = machineBooked;
        this.dateBooked = dateBooked;
    }

    public String getAccountUsername() {return accountUsername;}
    // Leaving this here even though it is unused to observe good coding practices (ie encapsulation)


    public void setAccountUsername(String accountUsername) {this.accountUsername = accountUsername;}
    // Leaving this here even though it is unused to observe good coding practices (ie encapsulation)

    public String getMachineBooked() {
        return machineBooked;
    }

    public void setMachineBooked(String machineBooked) {this.machineBooked = machineBooked;}
    // Leaving this here even though it is unused to observe good coding practices (ie encapsulation)

    public Date getDateBooked() {
        return dateBooked;
    }

    public void setDateBooked(Date dateBooked) {this.dateBooked = dateBooked;}
    // Leaving this here even though it is unused to observe good coding practices (ie encapsulation)

    @NonNull
    @Override
    public String toString() {
        return this.accountUsername + " has booked machine " + this.machineBooked;
    }
}










