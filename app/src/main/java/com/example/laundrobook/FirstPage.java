package com.example.laundrobook;


import static com.example.laundrobook.MainActivity.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class FirstPage extends AppCompatActivity{

    private TextView displaytext;
    DatabaseReference mRootDatabaseRef; // root node
    DatabaseReference mBookingRef; // reference to the booking node
    DatabaseReference mUserRef; // reference to the user node
    DatabaseReference mAccountNameRef; // reference to the username node
    DatabaseReference mAccBookRef; // reference to the bookings made under user
    DatabaseReference mUserBookingRef; // username-->userBookings
    private final ArrayList<String> machList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        Intent AccountPanel = getIntent();
        String username = AccountPanel.getStringExtra(MainActivity.User);

        mRootDatabaseRef = FirebaseDatabase.getInstance().getReference(); // root node

        mBookingRef = mRootDatabaseRef.child("Booking"); // reference to the booking node


        mUserRef = mRootDatabaseRef.child("Accounts");
        mAccountNameRef = mUserRef.child(username);
        mAccBookRef = mAccountNameRef.child("bookings");
        mUserBookingRef = mAccountNameRef.child("userBookings");

        Button accountsettingsbtn = findViewById(R.id.accountsettingsbtn);
        Button bookbtn = findViewById(R.id.bookbtn);
        Button cancelbtn = findViewById(R.id.cancelbtn);
        Button displaybtn = findViewById(R.id.displaybtn);
        displaytext = findViewById(R.id.displaytext);

        accountsettingsbtn.setOnClickListener(view -> {
            Intent intent = new Intent(FirstPage.this, Dashboard.class);
            startActivity(intent);
        });
        bookbtn.setOnClickListener(view -> {
            Intent intent = new Intent(FirstPage.this, BookingPanel.class);
            intent.putExtra(User, username);
            startActivity(intent);
        });
        cancelbtn.setOnClickListener(view -> {
            mUserBookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot userBookingsdataSnapshot) {
                    userBookingsdataSnapshot.getRef().setValue(0);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            // mAccBookRef is bookings under username
            mAccBookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot machbookingsdataSnapshot) {
                    if (machbookingsdataSnapshot.getValue() != null){
                        for (DataSnapshot childSnapshot : machbookingsdataSnapshot.getChildren()) {
                            String childValue = childSnapshot.getValue(String.class);
                            machList.add(childValue);
                        }
                        machbookingsdataSnapshot.getRef().setValue(null);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            mBookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot BookingSnapshot) {
                    //Toast.makeText(FirstPage.this, machList.toString(), Toast.LENGTH_SHORT).show();
                    for (DataSnapshot childSnapshot : BookingSnapshot.getChildren()) {
                        String childValue = Objects.requireNonNull(childSnapshot.getValue()).toString();
                        for (String machine : machList) {
                            if (childValue.contains(machine)){
                                Toast.makeText(FirstPage.this, "Going to delete " + machine, Toast.LENGTH_SHORT).show();
                                childSnapshot.getRef().setValue(null);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
        displaybtn.setOnClickListener(view -> mBookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    String stringToDisplay = "";
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String childValue = Objects.requireNonNull(childSnapshot.getValue()).toString();
                        String isSameUsername = "%3D" + username + "%2C%20";
                        if ( mBookingRef.child(childValue).toString().contains(isSameUsername) ){
                            Date currentTime = new Date();
                            long timeDiff = currentTime.getTime() - Utils.getTimeBooked(childSnapshot);
                            long timeDiffSeconds = timeDiff / 1000 % 60;
                            long est_time_completion = Utils.test_countdown - timeDiffSeconds;
                            if (est_time_completion < 0){ est_time_completion = 0;}
                            String lineToDisplay = Utils.getMachineBooked(childSnapshot) + " : " + est_time_completion + "s\n";
                            stringToDisplay = stringToDisplay.concat(lineToDisplay);
                        }
                    }
                    if (stringToDisplay.isEmpty()){
                        displaytext.setText("You currently don't have any bookings");
                    } else {displaytext.setText(stringToDisplay);}
                } else {displaytext.setText("You currently don't have any bookings");}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));

    }
}
