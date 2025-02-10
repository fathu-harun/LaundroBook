package com.example.laundrobook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Objects;

public class Pop_up extends AppCompatActivity {

    Button confirmButton;
    Button cancelButton;
    TextView textView;
    DatabaseReference mRootDatabaseRef; // root node
    DatabaseReference mBookingRef; // reference to the booking node
    DatabaseReference mUserRef; // reference to the user node

    public final static String machine = "machine";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MyNotificationChannel";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        Intent intent = getIntent();
        String machineSelected = intent.getStringExtra(machine);
        textView = findViewById(R.id.textView);
        textView.setText("Confirm to book " + machineSelected + "?");
        String accountUser = intent.getStringExtra(MainActivity.User);

        mRootDatabaseRef = FirebaseDatabase.getInstance().getReference(); // root node
        mBookingRef = mRootDatabaseRef.child("Booking"); // reference to the booking node
        mUserRef = mRootDatabaseRef.child("Accounts");

        confirmButton = findViewById(R.id.confirm_button);
        cancelButton = findViewById(R.id.cancel_button);

        confirmButton.setOnClickListener(v -> {
            Bookings bookings = new Bookings(accountUser, machineSelected, new Date());
            mBookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(machineSelected)) {
                        String result = Objects.requireNonNull(snapshot.child(machineSelected).getValue()).toString();
                        textView.setText(R.string.machine_selected + result + "!");
                    } else {
                        mBookingRef.child(machineSelected).setValue(bookings);
                        mRootDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot rootSnapshot) {
                                Account account = rootSnapshot.child("Accounts").child(accountUser).getValue(Account.class);
                                assert account != null;
                                account.addBookings(machineSelected);
                                account.setUserBookings();
                                mUserRef.child(accountUser).child("bookings").setValue(account.getBookings());
                                mUserRef.child(accountUser).child("userBookings").setValue(account.getUserBookings());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // Create notification channel
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            CharSequence name = "My Notification Channel";
                            String description = "Description of My Notification Channel";
                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                            channel.setDescription(description);
                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(channel);
                        }

                        // Build the notification
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(Pop_up.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.project_logo_small)
                                .setContentTitle("Successfully booked " + machineSelected)
                                .setContentText("You have successfully booked " + machineSelected + "!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        // Show the notification
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Pop_up.this);
                        ActivityCompat.checkSelfPermission(Pop_up.this, android.Manifest.permission.POST_NOTIFICATIONS);
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                        new CountDownTimer(5000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                if (ActivityCompat.checkSelfPermission(Pop_up.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                notificationManager.notify(NOTIFICATION_ID, builder.setContentText("Washing machine/Dryer is almost done").build());
                            }
                        };
                        finish();
                    }
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        cancelButton.setOnClickListener(v -> finish());
    }
}