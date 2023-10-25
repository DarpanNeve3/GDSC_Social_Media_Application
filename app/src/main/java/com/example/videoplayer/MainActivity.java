package com.example.videoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    public static final String MSG = "com.example.videoplayer.User";
    BottomNavigationView bmv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        bmv= findViewById(R.id.bmv);
        bmv.setOnNavigationItemSelectedListener(this);
        bmv.setSelectedItemId(R.id.videos);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.profile:
                Intent intent =new Intent(this,PreUserProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.videos:
                Intent i = new Intent(this,PreVideoActivity.class);
                startActivity(i);
                return true;

            case R.id.images:
                Intent i1 = new Intent(this,PreImageActivity.class);
                startActivity(i1);
                return true;

            case R.id.audios:
                Intent i2 = new Intent(this,PreAudioActivity.class);
                startActivity(i2);
                return true;
        }
        return false;
    }
    public void showVideoActivity(View view)
    {
            Intent intent = new Intent(this,PreVideoActivity.class);
            startActivity(intent);
    }
//    public void showMusic(View view)
//    {
//        Intent intent = new Intent(this,MusicActivity.class);
//        startActivity(intent);
//    }
//    public void showNewMusic(View view)
//    {
//        Intent intent = new Intent(this,MusicLayout.class);
//        startActivity(intent);
//    }
//
//    public void signup(View view) {
//        Intent i =new Intent(this,SignupActivity.class);
//        startActivity(i);
//    }
//
//    public void login(View view) {
//        Intent i =new Intent(this,PreUserProfileActivity.class);
//        startActivity(i);
//    }
//
//
//    public void upload(View view) {
//        Intent i = new Intent(this,UploadActivity.class);
//        startActivity(i);
//    }
//
//    public void showImage(View view) {
//        Intent i = new Intent(this,PreImageActivity.class);
//        startActivity(i);
//    }
}