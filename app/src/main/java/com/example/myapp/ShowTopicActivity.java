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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myapp.Adapter.CardAdapter;
import com.example.myapp.Controller.CONSTANT;
import com.example.myapp.Controller.RecyclerItemClickListener;
import com.example.myapp.Database.CardDatabase;
import com.example.myapp.Database.TopicDatabase;
import com.example.myapp.Model.Card;
import com.example.myapp.Model.Topic;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
    private List<Integer> cardTopic;
    private List<Card> cardBar;
    private Button eraseCardBar;
    private Button speakCardBar;
    private int position;

    private TextToSpeech textToSpeech;

    // Bottom Sheet
    private Button useBottomSheet;
    private LinearLayout layoutBottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageButton btnBar;
    private Boolean stateBar;
    private Topic topic;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    long id = intent.getLongExtra("id", 0);
                    System.out.println("ID after: " + id);
                    topic.addCard((int) id);
                    TopicDatabase.getInstance(this).topicDAO().updateTopic(topic);
                    setData();

                }
            });

    OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {

        @Override
        public void handleOnBackPressed() {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("Bar List Card", (Serializable) cardBar);
            bundle.putBoolean("State Bar", stateBar);
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

        // Receive data from PlayActivity

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        // position = bundle.getInt("Position");
        topic = (Topic) bundle.getSerializable("Topic");
        cardBar = (List<Card>) bundle.getSerializable("Bar List Card");
        cardTopic = topic.getCards();
        stateBar = bundle.getBoolean("State Bar");
        // Bottom Sheet

        layoutBottomSheet = findViewById(R.id.bottom_sheet_layout);
        btnBar = findViewById(R.id.btnBar);
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        changeStateBar();
        btnBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btnBar.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                    stateBar = true;
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    btnBar.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
                    stateBar = false;
                }
            }
        });






        // Set adapter for RecyclerView

        recyclerView = findViewById(R.id.rcv_items);
        cardAdapter = new CardAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(cardAdapter);
        cardAdapter.setData(cardTopic);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Card card = CardDatabase.getInstance(ShowTopicActivity.this).cardDAO().getCardById(cardTopic.get(position));
                        Toast.makeText(ShowTopicActivity.this, "You clicked " + card.getNameCard(), Toast.LENGTH_LONG).show();
                        speak(card.getNameCard());
                        barFragment.add(card);
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
        fragmentCard_container = findViewById(R.id.fragment_container);
        barFragment = new BarFragment(cardBar);
        replaceFragment(barFragment);


        // Add Card
        btnAddCard = findViewById(R.id.addButton);
        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowTopicActivity.this, AddCardActivity.class);
                activityResultLauncher.launch(intent);
            }
        });

        // Erase Card
        eraseCardBar = findViewById(R.id.eraseBar);
        eraseCardBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barFragment.erase();
            }
        });

        // Speak Card
        speakCardBar = findViewById(R.id.speakBar);
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


    public void changeStateBar() {
        if (stateBar) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            btnBar.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);

        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            btnBar.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
        }
    }

    public void speak(String sentence) {
        textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    public void setData(){
        if(cardAdapter == null)
            return;
        cardTopic = topic.getCards();
        cardAdapter.setData(cardTopic);
    }



}