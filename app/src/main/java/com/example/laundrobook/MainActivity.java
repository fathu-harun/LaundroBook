package com.example.laundrobook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    final public static String User = "User";
    TextView textViewMsg; // text view to display message
    ImageView charaImage; // character image
    Button loginButton; // login button
    Button registerButton; // register button
    EditText editTextUsername; // username
    EditText editTextPassword; // password
    String username; // username
    String password; // password

    final static String TAG = "Main_debug";

    DatabaseReference mRootDatabaseRef; // root node
    DatabaseReference mAccountRef; // reference to the account node
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views by id
        textViewMsg = findViewById(R.id.textViewMsg);
        charaImage = findViewById(R.id.charaImage);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mRootDatabaseRef = FirebaseDatabase.getInstance().getReference(); // root node
        mAccountRef = mRootDatabaseRef.child("Accounts"); // reference to the account node

        // Login button
        loginButton.setOnClickListener(v -> {

            username = editTextUsername.getText().toString();
            password = editTextPassword.getText().toString();

            mAccountRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) { //if username exist on the Accounts DataBase, Authenticate the password
                        Account account = dataSnapshot.getValue(Account.class);
                        assert account != null;
                        if (account.Check_password(password.hashCode())){
                            textViewMsg.setText(R.string.login_success);
                            Intent intent = new Intent(MainActivity.this, FirstPage.class);
                            intent.putExtra(User, username);
                            startActivity(intent);
                        } else {
                            textViewMsg.setText(R.string.user_change_input_prompt);
                        }
                    } else {
                        textViewMsg.setText(R.string.cannot_find_username);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });


        // Register button
        registerButton.setOnClickListener(view -> {
            username = editTextUsername.getText().toString();
            password = editTextPassword.getText().toString();
            //password check for validity
            Pattern upperCasePattern = Pattern.compile("[A-Z]");
            Pattern lowerCasePattern = Pattern.compile("[a-z]");
            Pattern digitCasePattern = Pattern.compile("[\\d ]");
            mAccountRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        textViewMsg.setText(R.string.username_exists);
                    } else if (password.length()<8) {
                        textViewMsg.setText(R.string.too_short);
                    } else if (!upperCasePattern.matcher(password).find()) {
                        textViewMsg.setText(R.string.uppercase_needed);
                    } else if (!lowerCasePattern.matcher(password).find()) {
                        textViewMsg.setText(R.string.lowercase_needed);
                    } else if (!digitCasePattern.matcher(password).find()) {
                        textViewMsg.setText(R.string.number_needed);
                    } else {
                        Account account = new Account(username, password.hashCode());
                        mAccountRef.child(username).setValue(account);
                        textViewMsg.setText(R.string.account_created);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }
}