package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
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
    private int idImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        btnSave = findViewById(R.id.btnSave);
        etNameTopic = findViewById(R.id.etNameTopic);
        etDescribeTopic = findViewById(R.id.etDescribeTopic);
        imageTopic = findViewById(R.id.imageViewTopic);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(AddActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(300, 300)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        btnSave.setOnClickListener(v -> {
            Intent intent = new Intent();
            Topic topic = new Topic(etNameTopic.getText().toString(), etDescribeTopic.getText().toString(), imageTopicPath, null);
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