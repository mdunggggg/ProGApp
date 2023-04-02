package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myapp.Controller.CONSTANT;
import com.example.myapp.Database.TopicDatabase;
import com.example.myapp.Model.Topic;
import com.github.dhaval2404.imagepicker.ImagePicker;

public class EditTopicActivity extends AppCompatActivity {
    private Topic topic;
    private ActionBar actionBar;
    private EditText etEditNameTopic;
    private EditText etEditDescriptionTopic;
    private ImageView imageViewTopic;
    private Button btnSelectImageTopic;
    private Button btnSaveTopic;
    private String imageCardPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_topic);

        // Set ActionBar
        actionBar = getSupportActionBar();
        actionBar.hide();

        // Init UI
        etEditNameTopic = findViewById(R.id.etNameTopic);
        etEditDescriptionTopic = findViewById(R.id.editDescribeTopic);
        imageViewTopic = findViewById(R.id.editViewTopic);
        btnSelectImageTopic = findViewById(R.id.btnEditImage);
        btnSaveTopic = findViewById(R.id.btnSave);

        // Get data from intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        topic = (Topic)bundle.getSerializable("Topic");

        etEditNameTopic.setText(topic.getNameTopic());
        if(topic.getImageTopic() == null){
            imageViewTopic.setImageResource(topic.getIdImage());
        }
        else{
            imageCardPath = topic.getImageTopic();
            imageViewTopic.setImageBitmap(BitmapFactory.decodeFile(topic.getImageTopic()));
        }
        etEditDescriptionTopic.setText(topic.getDescribeTopic());

        // Set Event

        btnSelectImageTopic.setOnClickListener(v -> {
            ImagePicker.with(EditTopicActivity.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(300, 300)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });
        btnSaveTopic.setOnClickListener(v -> {
            if(TextUtils.isEmpty(etEditNameTopic.getText().toString().trim())){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.notification)
                        .setMessage(R.string.isEmptyName)
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
            if(TextUtils.isEmpty(etEditDescriptionTopic.getText().toString().trim())){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.notification)
                        .setMessage(R.string.isEmptyDescription)
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
            Intent intentEdit = new Intent(EditTopicActivity.this, PlayActivity.class);
            updateTopic();
            setResult(CONSTANT.RESULT_EDIT_TOPIC, intentEdit);
            EditTopicActivity.super.onBackPressed();
        });

    }
    public void updateTopic(){
        topic.setNameTopic(etEditNameTopic.getText().toString().trim());
        topic.setDescribeTopic(etEditDescriptionTopic.getText().toString().trim());
        topic.setImageTopic(imageCardPath);
        TopicDatabase.getInstance(this).topicDAO().updateTopic(topic);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        imageViewTopic.setImageURI(uri);
        imageCardPath = uri.getPath();
    }
}