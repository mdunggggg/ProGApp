package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myapp.Controller.CONSTANT;
import com.example.myapp.Database.TopicDatabase;
import com.example.myapp.Model.Topic;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.Serializable;

public class AddActivity extends AppCompatActivity {
    private Button btnSave;
    private EditText etNameTopic, etDescribeTopic;
    private ImageView imageTopic;
    private Button btnSelectImage;
    private String imageTopicPath;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        actionBar = getSupportActionBar();
        actionBar.hide();

        btnSave = findViewById(R.id.btnSave);
        etNameTopic = findViewById(R.id.etNameTopic);
        etDescribeTopic = findViewById(R.id.etDescribeTopic);
        imageTopic = findViewById(R.id.imageViewTopic);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(AddActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(300, 300)
                        .start();
            }
        });

        btnSave.setOnClickListener(v -> {
            if(TextUtils.isEmpty(etNameTopic.getText().toString().trim())){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.notification)
                        .setMessage(R.string.isEmptyName)
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
            if(TextUtils.isEmpty(etDescribeTopic.getText().toString().trim())){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.notification)
                        .setMessage(R.string.isEmptyDescription)
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
            Intent intent = new Intent();
            Topic topic = null;
            if(imageTopicPath == null){
                topic = new Topic(etNameTopic.getText().toString().trim(), etDescribeTopic.getText().toString().trim(), null, R.drawable.image);
            }
            else {
                topic = new Topic(etNameTopic.getText().toString().trim(), etDescribeTopic.getText().toString().trim(), imageTopicPath, null);
            }
            intent.putExtra("data", (Serializable) topic);
            TopicDatabase.getInstance(AddActivity.this).topicDAO().insertTopic(topic);
            setResult(CONSTANT.RESULT_ADD_TOPIC, intent);
            AddActivity.super.onBackPressed();

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        imageTopic.setImageURI(uri);
        imageTopicPath = uri.getPath();
    }
}