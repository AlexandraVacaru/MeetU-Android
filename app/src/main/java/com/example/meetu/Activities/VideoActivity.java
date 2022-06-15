package com.example.meetu.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetu.Adapters.VideoHolder;
import com.example.meetu.Models.VideoModel;
import com.example.meetu.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class VideoActivity extends AppCompatActivity {

    FloatingActionButton addVideo;
    RecyclerView videoRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        addVideo = (FloatingActionButton) findViewById(R.id.addVideo);
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddVideoActivity.class));
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.action_add_post);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_post:
                        startActivity(new Intent(VideoActivity.this, AddPostActivity.class));
                        overridePendingTransition(0, 0);
                    case R.id.home:
                        startActivity(new Intent(VideoActivity.this, HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.about:
                        return true;
                    case R.id.signOut:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(VideoActivity.this, WelcomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        videoRecyclerView = (RecyclerView) findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<VideoModel> options = new FirebaseRecyclerOptions.Builder<VideoModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Videos"), VideoModel.class)
                .build();

        FirebaseRecyclerAdapter<VideoModel,VideoHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<VideoModel, VideoHolder>(options) {

            @NonNull
            @Override
            public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video, parent,false);
                return new VideoHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull VideoHolder holder, int position, @NonNull VideoModel model) {
                holder.prepareExoPlayer(getApplication(), model.getVideoTitle(), model.getVideoUrl());
            }
        };

        firebaseRecyclerAdapter.startListening();
        videoRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

}