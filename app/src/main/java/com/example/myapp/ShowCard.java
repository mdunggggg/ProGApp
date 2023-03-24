package com.example.myapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapp.Controller.CONSTANT;
import com.example.myapp.Database.CardDatabase;
import com.example.myapp.Model.Card;
import com.example.myapp.Model.Topic;

import java.util.List;

public class ShowCard extends AppCompatActivity {
    private ActionBar actionBar;
    private ImageButton btnBack;
    private TextView tvCard;
    private ImageView imgCard;
    private ImageButton btnEdit;
    private Card card;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == CONSTANT.RESULT_EDIT_CARD) {
                    Intent intentResult = result.getData();
                    Bundle bundle = intentResult.getExtras();
                    card = (Card) bundle.getSerializable("Card");
                    setData();
                }

            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_card);

        // Set ActionBar
        actionBar = getSupportActionBar();
        actionBar.hide();

        // Init UI

        btnBack = findViewById(R.id.btnBack);
        tvCard = findViewById(R.id.tvCard);
        imgCard = findViewById(R.id.imgCard);
        btnEdit = findViewById(R.id.btnEdit);

        // Get data from intent

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        card = (Card)bundle.getSerializable("Card");
        setData();

        // Back
        btnBack.setOnClickListener(v -> {
            setResult(CONSTANT.RESULT_SHOW_CARD, intent);
            ShowCard.super.onBackPressed();
        });

        // Edit Card
        btnEdit.setOnClickListener(v -> {
            Intent intentEdit = new Intent(ShowCard.this, EditCard.class);
            Bundle bundleEdit = new Bundle();
            bundleEdit.putSerializable("Card", card);
            intentEdit.putExtras(bundleEdit);
            activityResultLauncher.launch(intentEdit);
        });

    }
    public void setData(){
        tvCard.setText(card.getNameCard());
        if(card.getImageCard() == null && card.getIdImage() == null)
            return;
        if(card.getImageCard() == null){
            imgCard.setImageResource(card.getIdImage());
        }
        else{
            imgCard.setImageBitmap(BitmapFactory.decodeFile(card.getImageCard()));
        }
    }
}