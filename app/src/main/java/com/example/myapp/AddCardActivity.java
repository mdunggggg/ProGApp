package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myapp.Controller.CONSTANT;
import com.example.myapp.Database.CardDatabase;
import com.example.myapp.Model.Card;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.Serializable;

public class AddCardActivity extends AppCompatActivity {
    private Button buttonSaveCard;
    private Button buttonSelectImageCard;
    private EditText etNameCard;
    private ImageView imageCard;
    private String imageCardPath;
    private int idImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        buttonSaveCard = findViewById(R.id.btnSaveCard);
        etNameCard = findViewById(R.id.etNameCard);
        imageCard = findViewById(R.id.imageViewCard);
        buttonSelectImageCard = findViewById(R.id.btnSelectImageCard);

        buttonSelectImageCard.setOnClickListener(v -> {
            ImagePicker.with(AddCardActivity.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(300, 300)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        buttonSaveCard.setOnClickListener(v -> {
            Intent intent = new Intent();
            Card card = new Card(etNameCard.getText().toString(), imageCardPath, null);
          //  intent.putExtra("data", (Serializable) card);
            long id = CardDatabase.getInstance(AddCardActivity.this).cardDAO().insertCardReturnId(card);
            intent.putExtra("id", id);
            System.out.println("ID before: " + id);
            setResult(CONSTANT.RESULT_ADD_CARD, intent);
           AddCardActivity.super.onBackPressed();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        imageCard.setImageURI(uri);
        imageCardPath = uri.getPath();
    }
}