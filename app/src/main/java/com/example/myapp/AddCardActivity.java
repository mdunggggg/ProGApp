package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        actionBar = getSupportActionBar();
        actionBar.hide();

        buttonSaveCard = findViewById(R.id.btnSaveCard);
        etNameCard = findViewById(R.id.etNameCard);
        imageCard = findViewById(R.id.imageViewCard);
        buttonSelectImageCard = findViewById(R.id.btnSelectImageCard);

        buttonSelectImageCard.setOnClickListener(v -> {
            ImagePicker.with(AddCardActivity.this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(300, 300)
                    .start();
        });

        buttonSaveCard.setOnClickListener(v -> {
            if(TextUtils.isEmpty(etNameCard.getText().toString().trim())){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.notification)
                        .setMessage(R.string.isEmptyName)
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
            Card card = null;
            if(imageCardPath == null){
                card = new Card(etNameCard.getText().toString().trim(), null, R.drawable.image);

            }
            else {
                card = new Card(etNameCard.getText().toString().trim(), imageCardPath, null);
            }
            Intent intent = new Intent();
            long id = CardDatabase.getInstance(AddCardActivity.this).cardDAO().insertCardReturnId(card);
            intent.putExtra("id", id);
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