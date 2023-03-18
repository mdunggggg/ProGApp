package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ImageButton githubButton, fbButton, proptitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: ");
        githubButton = findViewById(R.id.githubButton);
        fbButton = findViewById(R.id.fbButton);
        proptitBtn = findViewById(R.id.proptitButton);
        githubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInternet("https://github.com/mdunggggg");

            }
        });
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInternet("https://www.facebook.com/mdunggggg25703/");
            }
        });
        proptitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInternet("https://www.facebook.com/clubproptit/");
            }
        });

    }

    public void btnLogin_OnClick(View view) {
        Intent intent = new Intent(MainActivity.this, PlayActivity.class);
        startActivity(intent);
        //Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
    }
    void goToInternet(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}