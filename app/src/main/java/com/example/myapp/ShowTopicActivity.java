package com.example.myapp;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myapp.Adapter.CardAdapter;
import com.example.myapp.Controller.CONSTANT;
import com.example.myapp.Controller.RecyclerItemClickListener;
import com.example.myapp.Model.Card;
import com.example.myapp.Model.Topic;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowTopicActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private RelativeLayout frameCardLayout_container;
    private FrameLayout fragmentCard_container;

    private FloatingActionButton btnAddCard;
    private BarFragment barFragment;
    private List<Card> cardTopic;
    private List<Card> cardBar;
    private Button eraseCardBar;
    private Button speakCardBar;
    private int position;

    private TextToSpeech textToSpeech;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    //Toast.makeText(PlayActivity.this, "You clicked " + result.getData().getStringExtra("data"), Toast.LENGTH_LONG).show();
                    Intent intent = result.getData();
                    Card card = (Card) intent.getSerializableExtra("data");
                    cardTopic.add(card);
                    cardAdapter.setData(cardTopic);
                }
            });

    OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {

        @Override
        public void handleOnBackPressed() {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("Bar List Card", (Serializable) cardBar);
            bundle.putSerializable("Topic List Card", (Serializable) cardTopic);
            bundle.putInt("Position", position);
            //intent.putExtra("data", (Serializable) barFragment.getCardBar());
            intent.putExtras(bundle);
            setResult(CONSTANT.SHOW_CARD_ACTIVITY, intent);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_topic);

        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = bundle.getInt("Position");
        cardBar = (List<Card>) bundle.getSerializable("Bar List Card");
        cardTopic = (List<Card>) bundle.getSerializable("Topic List Card");


        recyclerView = findViewById(R.id.rcv_cards);
        cardAdapter = new CardAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(cardAdapter);
        cardAdapter.setData(cardTopic);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(ShowTopicActivity.this, "You clicked " + cardTopic.get(position).getNameCard(), Toast.LENGTH_LONG).show();
                        speak(cardTopic.get(position).getNameCard());
                        barFragment.add(cardTopic.get(position));
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));


        // Convert text to speech
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        //Toast.makeText(PlayActivity.this, "This language is not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(PlayActivity.this, "Succes", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Fragment
        frameCardLayout_container = findViewById(R.id.frameCardLayout_container);
        fragmentCard_container = findViewById(R.id.fragment_container);
        barFragment = new BarFragment(cardBar);
        replaceFragment(barFragment);


        // Add Card
        btnAddCard = findViewById(R.id.btnAddCard);
        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowTopicActivity.this, AddCardActivity.class);
                activityResultLauncher.launch(intent);
            }
        });

        // Erase Card
        eraseCardBar = findViewById(R.id.eraseCardBar);
        eraseCardBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barFragment.erase();
            }
        });

        // Speak Card
        speakCardBar = findViewById(R.id.speakCardBar);
        speakCardBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barFragment.speak();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void speak(String sentence) {
        textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null);
    }

}