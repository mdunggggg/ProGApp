package com.example.myapp;

import static java.lang.Math.abs;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    private Boolean isMoving;
    private float xStart;
    private float yStart;
    private DisplayMetrics displayMetrics;
    private float screenHeight;
    private float screenWidth;
    private ActionBar actionBar;
    private Drawable drawable;
    private SpannableString spannableString;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private RelativeLayout frameCardLayout_container;
    private FrameLayout fragmentCard_container;

    private ImageButton btnAddCard;
    private BarFragment barFragment;
    private List<Integer> cardTopic;
    private List<Card> cardBar;
    private ImageButton eraseCardBar;
    private ImageButton speakCardBar;
    private int position;

    private TextToSpeech textToSpeech;

    // Bottom Sheet
    private Button useBottomSheet;
    private LinearLayout layoutBottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageButton btnBar;
    private Boolean stateBar;
    private Topic topic;
    private EditText etSearch;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == CONSTANT.RESULT_ADD_CARD) {
                    Intent intent = result.getData();
                    long id = intent.getLongExtra("id", 0);
                    topic.addCard((int) id);
                    TopicDatabase.getInstance(this).topicDAO().updateTopic(topic);
                    setData();
                }
                else if(result.getResultCode() == CONSTANT.RESULT_SHOW_CARD){
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

        // Set action bar
        actionBar = getSupportActionBar();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        spannableString = new SpannableString(topic.getNameTopic());
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawable = getResources().getDrawable(R.drawable.back_button);
        getSupportActionBar().setHomeAsUpIndicator(drawable);
        getSupportActionBar().setTitle(spannableString);



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
                        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                            speak(card.getNameCard());
                            barFragment.add(card);
                        }
                        else{
                            goToShowCard(card);
                        }

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
        isMoving = false;
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels - 100;
        screenWidth = displayMetrics.widthPixels;
        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddCard();
            }
        });
        btnAddCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMoving = false;
                        xStart = event.getX();
                        yStart = event.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float xDiff = event.getX();
                        float yDiff = event.getY();
                        if (abs(xDiff) > 10 || abs(yDiff) > 10) {
                            isMoving = true;
                        }
                        if (isMoving) {
                            float newX = btnAddCard.getX() + (xDiff - xStart);
                            float newY = btnAddCard.getY() + (yDiff - yStart);

                            if (newX < 0) {
                                newX = 0;
                            }
                            if (newX > (screenWidth - btnAddCard.getWidth())) {
                                newX = screenWidth - btnAddCard.getWidth();
                            }
                            if (newY < 0) {
                                newY = 0;
                            }
                            if (newY > (screenHeight - btnAddCard.getHeight())) {
                                newY = screenHeight - btnAddCard.getHeight();
                            }
                            btnAddCard.setX(newX);
                            btnAddCard.setY(newY);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!isMoving) {
                            goToAddCard();
                        }
                        return true;
                    default:
                        return false;
                }
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


        // Search Card

        etSearch = findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchCard();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:break;
        }

        return super.onOptionsItemSelected(item);
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

    public void goToShowCard(Card card){
        Intent intent = new Intent(ShowTopicActivity.this, ShowCard.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Card", card);
        intent.putExtras(bundle);
        activityResultLauncher.launch(intent);
    }
    public void goToAddCard(){
        Intent intent = new Intent(ShowTopicActivity.this, AddCardActivity.class);
        activityResultLauncher.launch(intent);
    }
    private void searchCard() {
        String search = etSearch.getText().toString().trim();
        if(search.equals("")){
            setData();
            return;
        }
        List<Card> cards = CardDatabase.getInstance(this).cardDAO().searchCard(search);
        List<Integer> cardInteger = new ArrayList<>();
        for(int i = 0 ; i < cards.size(); i++){
            cardInteger.add(cards.get(i).getId());
        }
        cardAdapter.setData(cardInteger);

    }


}