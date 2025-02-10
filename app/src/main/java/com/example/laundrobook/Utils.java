package com.example.laundrobook;

import android.util.Log;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {
    public static int test_countdown = 240;
    private static final ArrayList<Machine> machineList = new ArrayList<>();
    private static final ArrayList<CheckBox> checkboxList = new ArrayList<>();

    public Utils() {
    }

    public static void addMachine(Machine machine) {
        if (!getMachineList().contains(machine)) {
            getMachineList().add(machine);
        }
    }

    public static void addCheckBox(CheckBox checkbox) {
        if (!getCheckboxList().contains(checkbox)) {
            getCheckboxList().add(checkbox);
        }
    }


    public static List<Machine> getMachineList() {
        return machineList;
    }

    public static ArrayList<CheckBox> getCheckboxList() {
        return checkboxList;
    }

    public static String updateTiming(DataSnapshot dataSnapshot){
        if (dataSnapshot.exists()){
            Bookings machineDate = dataSnapshot.getValue(Bookings.class);
            assert machineDate != null;
            Log.d("Utils_debug", machineDate.toString());
            Date currentTime = new Date();
            long timeDiff = currentTime.getTime() - machineDate.getDateBooked().getTime();
            long timeDiffHours = timeDiff / (60 * 60 * 1000) % 24;
            long timeDiffSeconds = timeDiff / 1000 % 60;
            if (timeDiffHours > 1){
//                String machineStr = dataSnapshot.child("machineBooked").getValue().toString();
                dataSnapshot.getRef().setValue(null);
                return dataSnapshot.getRef().getKey();
            }
            //Todo : remove this after testing
            if (timeDiffSeconds> test_countdown){
                dataSnapshot.getRef().setValue(null);
                return dataSnapshot.getRef().getKey();
            }
        }
        return "";
    }

    public static Long getTimeBooked(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            Bookings machineDate = dataSnapshot.getValue(Bookings.class);
            assert machineDate != null;
            return machineDate.getDateBooked().getTime();
        }
        return 1L;
    }

    public static String getMachineBooked(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            Bookings machineDate = dataSnapshot.getValue(Bookings.class);
            assert machineDate != null;
            return machineDate.getMachineBooked();
        }
        return "NIL";
    }

    public static void updateUBookLimit(DatabaseReference mRootDatabaseRef, ArrayList<String> missing, Account account, String accountUser) {
        DatabaseReference mUserRef = mRootDatabaseRef.child("Accounts");
        if (!missing.isEmpty()) {
            ArrayList<String> bookings = account.getBookings();
            Log.d("BookingPanel", bookings.toString());
            for (String machineBooked: missing) {
                if (bookings.contains(machineBooked)) {
                    account.removeBookings(machineBooked);
                    mUserRef.child(accountUser).child("bookings").setValue(account.getBookings());
                    account.reduceUserBookings();
                    mUserRef.child(accountUser).child("userBookings").setValue(account.getUserBookings());
                }
            }
        }
        else {
            mUserRef.child(accountUser).child("bookings").setValue(null);
            mUserRef.child(accountUser).child("userBookings").setValue(0);
        }
        for (CheckBox checkbox : Utils.getCheckboxList()) {
            if ( missing.contains(checkbox.getResources().getResourceEntryName(checkbox.getId())) ) {
                Log.d("BookingPanel", "missing" + missing);
                checkbox.setChecked(false);
                checkbox.setEnabled(true);
            }
        }
        missing.clear();
    }

    @NonNull
    @Override
    public String toString() {
        return "This class has a machine list and a checkbox list";
    }
}








