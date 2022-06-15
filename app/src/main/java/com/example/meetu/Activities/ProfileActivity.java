package com.example.meetu.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meetu.Models.User;
import com.example.meetu.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;


    private Button logout;
    private Button addUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.about);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_post:
                        startActivity(new Intent(ProfileActivity.this, AddPostActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.about:
                        return true;
                    case R.id.signOut:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileActivity.this, WelcomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                    return false;
            }
        });


        final TextView greetingTextView = (TextView) findViewById(R.id.greeting);
        final TextView emailTextView = (TextView) findViewById(R.id.emailAddress);
        final TextView firstNameTextView = (TextView) findViewById(R.id.firstName);
        final TextView lastNameTextView = (TextView) findViewById(R.id.lastName);


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null) {
                    String email = userProfile.email;
                    String firstName = userProfile.firstName;
                    String lastName = userProfile.lastName;

                    greetingTextView.setText("Welcome, " + firstName + " " +  lastName + "!");
                    emailTextView.setText(email);
                    firstNameTextView.setText(firstName);
                    lastNameTextView.setText(lastName);

                } else {
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(ProfileActivity.this);
                    if(acct != null) {
                        String email = acct.getEmail();
                        String firstName = acct.getGivenName();
                        String lastName = acct.getFamilyName();

                        greetingTextView.setText("Welcome, " + firstName + " " +  lastName + "!");
                        emailTextView.setText(email);
                        firstNameTextView.setText(firstName);
                        lastNameTextView.setText(lastName);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something wrong happened!", Toast.LENGTH_LONG).show();

            }
        });

        logout = findViewById(R.id.signOut);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, WelcomeActivity.class));
            }
        });

        addUsername = findViewById(R.id.videoPlay);

        addUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, VideoActivity.class));
            }
        });

    }
}