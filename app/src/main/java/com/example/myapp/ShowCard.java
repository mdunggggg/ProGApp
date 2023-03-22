package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapp.Model.Card;
import com.example.myapp.Model.Topic;

import java.util.List;

public class ShowCard extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvCard;
    private ImageView imgCard;
    private Card card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_card);

        // Init UI
        btnBack = findViewById(R.id.btnBack);
        tvCard = findViewById(R.id.tvCard);
        imgCard = findViewById(R.id.imgCard);

        // Get data from intent

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        card = (Card)bundle.getSerializable("Card");
        tvCard.setText(card.getNameCard());
        if(card.getImageCard() == null){
            imgCard.setImageResource(card.getIdImage());
        }
        else{
            imgCard.setImageBitmap(BitmapFactory.decodeFile(card.getImageCard()));
        }



    }
}