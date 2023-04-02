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
import android.widget.TextView;

import com.example.myapp.Controller.CONSTANT;
import com.example.myapp.Database.CardDatabase;
import com.example.myapp.Model.Card;
import com.github.dhaval2404.imagepicker.ImagePicker;

public class EditCard extends AppCompatActivity {
    private Card card;
    private ActionBar actionBar;
    private EditText etEditNameCard;
    private ImageView imageViewCard;
    private Button btnSelectImageCard;
    private Button btnSaveCard;
    private String imageCardPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        // Set ActionBar
        actionBar = getSupportActionBar();
        actionBar.hide();

        // Init UI
        etEditNameCard = findViewById(R.id.etEditNameCard);
        imageViewCard = findViewById(R.id.imageViewCard);
        btnSelectImageCard = findViewById(R.id.btnSelectImageCard);
        btnSaveCard = findViewById(R.id.btnSaveCard);

        // Get data from intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        card = (Card)bundle.getSerializable("Card");

        etEditNameCard.setText(card.getNameCard());
        if(card.getImageCard() == null){
            imageViewCard.setImageResource(card.getIdImage());
        }
        else{
            imageCardPath = card.getImageCard();
            imageViewCard.setImageBitmap(BitmapFactory.decodeFile(card.getImageCard()));
        }

        // Set Event
        btnSelectImageCard.setOnClickListener(v -> {
            ImagePicker.with(EditCard.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(300, 300)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        btnSaveCard.setOnClickListener(v -> {
            if(TextUtils.isEmpty(etEditNameCard.getText().toString().trim())){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.notification)
                        .setMessage(R.string.isEmptyName)
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
            Intent intentEdit = new Intent(EditCard.this, ShowCard.class);
            Bundle bundleEdit = new Bundle();
            updateCard();
            bundleEdit.putSerializable("Card", card);
            intentEdit.putExtras(bundleEdit);
            setResult(CONSTANT.RESULT_EDIT_CARD, intentEdit);
            EditCard.super.onBackPressed();
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        imageViewCard.setImageURI(uri);
        imageCardPath = uri.getPath();
    }
    public void updateCard(){
        card.setNameCard(etEditNameCard.getText().toString().trim());
        card.setImageCard(imageCardPath);
        CardDatabase.getInstance(EditCard.this).cardDAO().updateCard(card);
    }


}