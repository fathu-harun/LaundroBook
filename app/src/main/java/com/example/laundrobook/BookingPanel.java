package com.example.laundrobook;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;


public class BookingPanel extends AppCompatActivity implements View.OnClickListener {

    int[] checkboxIDs;

    Machine[] machineList;
    String TAG = "BookingPanel_debug";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_panel);

        DatabaseReference mRootDatabaseRef = FirebaseDatabase.getInstance().getReference(); // root node

        Intent intentExtra = getIntent();
        String username = intentExtra.getStringExtra(MainActivity.User);

        checkboxIDs = new int[]{
                R.id.W1, R.id.W2, R.id.W3, R.id.W4, R.id.W5, R.id.W6, R.id.W7, R.id.W8, R.id.W9, R.id.W10, R.id.W11,
                R.id.D1, R.id.D2, R.id.D3, R.id.D4, R.id.D5, R.id.D6
        };

        for (int IDs: checkboxIDs) {
            Utils.addCheckBox(findViewById(IDs));
        }

        ArrayList<CheckBox> selectedButton = new ArrayList<>();

        machineList = new Machine[]{
                new Washer("W1"), new Washer("W2"), new Washer("W3"), new Washer("W4"),
                new Washer("W5"), new Washer("W6"), new Washer("W7"), new Washer("W8"),
                new Washer("W9"), new Washer("W10"), new Washer("W11"),
                new Dryer("D1"), new Dryer("D2"), new Dryer("D3"),
                new Dryer("D4"), new Dryer("D5"), new Dryer("D6"),
        };

        for (Machine machine: machineList) {
            Utils.addMachine(machine);
        }

        Button refreshButton = findViewById(R.id.refresh_button);
        Button confirmButton = findViewById(R.id.confirm_button);


        pageRefresh(); // refresh the page on onCreate

        confirmButton.setOnClickListener(view -> {
            // Use an arraylist to get all the selected boxes
            for (CheckBox checkbox : Utils.getCheckboxList()) {
                if (checkbox.isChecked()) {
                    selectedButton.add(checkbox);
                }
            }
            // We want to check if the user has exceeded his/her booking limits
            // An event listener is needed here
            DatabaseReference userRef = mRootDatabaseRef.child("Accounts");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Optional<Integer> optionalInteger = Optional.ofNullable(snapshot.child(username).child("userBookings").getValue(Integer.class));
                    if ( optionalInteger.isPresent() ){
                        int userBookings = optionalInteger.get();
                        if (selectedButton.isEmpty()) {
                            Toast.makeText(BookingPanel.this, R.string.no_box_selected, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (selectedButton.size() > 2 || selectedButton.size()+userBookings > 2) {
                            Toast.makeText(BookingPanel.this, R.string.no_more_than_two, Toast.LENGTH_SHORT).show();
                            selectedButton.clear();
                        }

                        for (CheckBox checkbox: selectedButton) {
                            Intent intent = new Intent(BookingPanel.this, Pop_up.class);
                            intent.putExtra(Pop_up.machine, checkbox.getText().toString());
                            Toast.makeText(BookingPanel.this, checkbox.getResources().getResourceEntryName(checkbox.getId()) + " selected!", Toast.LENGTH_SHORT).show();
                            intent.putExtra(MainActivity.User, username);
                            startActivity(intent);
                        }
                        selectedButton.clear();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }


            });
        });

        refreshButton.setOnClickListener(view -> pageRefresh());
    }

    public void pageRefresh() {
        DatabaseReference mRootDatabaseRef = FirebaseDatabase.getInstance().getReference(); // root node
        DatabaseReference mBookingRef = mRootDatabaseRef.child("Booking");
        String accountUser = getIntent().getStringExtra(MainActivity.User);
        Log.d(TAG, mBookingRef.getRef().toString());
        if (mBookingRef.getKey() == null) {
            for (CheckBox checkbox : Utils.getCheckboxList()) {
                checkbox.setChecked(false);
                checkbox.setEnabled(true);
            }
        }
        for (Machine machine : Utils.getMachineList()) {
            mBookingRef.child(machine.getMachineName()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String machineBooked = Objects.requireNonNull(snapshot.child("machineBooked").getValue()).toString();
                        Log.d(TAG, machineBooked);
                        if (!machineBooked.isEmpty()) {
                            for (CheckBox checkbox : Utils.getCheckboxList()) {
                                if (checkbox.getResources().getResourceEntryName(checkbox.getId()).equals(machineBooked)) {
                                    checkbox.setChecked(false);
                                    checkbox.setEnabled(false);
                                }
                            }
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "Refresh_cancelled");
                }
            });
            ArrayList<String> missing = new ArrayList<>();
            mRootDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot rootSnapshot) {
                    if (!rootSnapshot.child("Booking").exists()) {
                        for (DataSnapshot accountSnapshot: rootSnapshot.child("Accounts").getChildren()){
                            String user = accountSnapshot.getKey();
                            assert user != null;
                            Account account = accountSnapshot.child(user).getValue(Account.class);
                            Utils.updateUBookLimit(mRootDatabaseRef, new ArrayList<>(), account, accountUser);
                        }
                    }
                    else {
                        for (DataSnapshot bookingSnapshot : rootSnapshot.child("Booking").getChildren()) {
                            missing.add(Utils.updateTiming(bookingSnapshot));
                        }
                        if (!missing.isEmpty()) {
                            Account account = rootSnapshot.child("Accounts").child(accountUser).getValue(Account.class);
                            Utils.updateUBookLimit(mRootDatabaseRef, missing, account, accountUser);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
    @Override
    public void onClick(View view) {
    }
}


