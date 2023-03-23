package com.example.myapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.Adapter.TopicAdapter;
import com.example.myapp.Controller.CONSTANT;
import com.example.myapp.Controller.RecyclerItemClickListener;
import com.example.myapp.Database.CardDatabase;
import com.example.myapp.Database.TopicDatabase;
import com.example.myapp.Model.Card;
import com.example.myapp.Model.Topic;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayActivity extends AppCompatActivity {

    private List<Topic> topics = new ArrayList<>();
    private TopicAdapter topicAdapter;
    private TextToSpeech textToSpeech;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private LinearLayout frameLayout_container;
    private FrameLayout fragment_container;
    private ImageButton eraseBar;
    private ImageButton speakBar;
    private BarFragment barFragment;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;


    // Bottom Sheet
    private Button useBottomSheet;
    private LinearLayout layoutBottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageButton btnBar;
    private Boolean stateBar;

    // Seartch
    private EditText etSearch;



    // ActivityResultLauncher: get information from child activity to parent activity
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == CONSTANT.RESULT_ADD_TOPIC) {
//            Intent intent = result.getData();
//            Topic topic = (Topic) intent.getSerializableExtra("data");
//            topics.add(topic);
//            topicAdapter.setData(topics);
              setDataTopic();
        }
        else if(result.getResultCode() == CONSTANT.SHOW_CARD_ACTIVITY) {
            Intent intent = result.getData();
            Bundle bundle = intent.getExtras();
//            List<Card>cards = (List<Card>) intent.getSerializableExtra("Bar List Card");
            barFragment.setCardBar((List<Card>) intent.getSerializableExtra("Bar List Card"));
            stateBar = bundle.getBoolean("State Bar");
//            topics.get(bundle.getInt("Position")).setCards((List<Card>) intent.getSerializableExtra("Topic List Card"));
//            changeStateBar();
                setDataTopic();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        // Delete Data
        TopicDatabase.getInstance(this).topicDAO().deleteAllTopic();
        CardDatabase.getInstance(this).cardDAO().deleteAllCard();

        // Bottom Sheet
        stateBar = false;
        layoutBottomSheet = findViewById(R.id.bottom_sheet_layout);
        btnBar = findViewById(R.id.btnBar);
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
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


        // Navigation Drawer
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Fragment for card bar

        fragment_container = findViewById(R.id.fragment_container);
        barFragment = new BarFragment();
        replaceFragment(barFragment);



        // Original Data
        dataInitialize();

        // Set adapter and Touch listener for recycler view
        recyclerView = findViewById(R.id.rcv_items);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(PlayActivity.this, "You clicked " + topics.get(position).getNameTopic(), Toast.LENGTH_LONG).show();
                speak(topics.get(position).getNameTopic());
                goToShowCardActivity(topics.get(position));

            }
            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        topicAdapter = new TopicAdapter(getApplicationContext());
        recyclerView.setAdapter(topicAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        topics = TopicDatabase.getInstance(getApplicationContext()).topicDAO().getlistTopic();
        topicAdapter.setData(topics);

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

       // Add topic
        btnAdd = findViewById(R.id.addButton);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddActivity();
            }
        });

        // Erase Bar;
        eraseBar = findViewById(R.id.eraseBar);
        eraseBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barFragment.erase();
            }
        });

        // Speak bar
        speakBar = findViewById(R.id.speakBar);
        speakBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barFragment.speak();
            }
        });



        // Search

        etSearch = findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchTopic();
                }
                return false;
            }
        });
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_actions, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
      //  Toast.makeText(this, "HAHHAHA", Toast.LENGTH_SHORT).show();


        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void goToAddActivity() {
        Intent intent = new Intent(PlayActivity.this, AddActivity.class);
        activityResultLauncher.launch(intent);
    }
    private void goToShowCardActivity(Topic topic) {
        Intent intent = new Intent(PlayActivity.this, ShowTopicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Topic", topic);
        bundle.putBoolean("State Bar", stateBar);
        bundle.putSerializable("Bar List Card", (Serializable) barFragment.getCardBar());
        intent.putExtras(bundle);
        activityResultLauncher.launch(intent);
    }

    public void speak(String sentence) {
        textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null);
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
    public void setDataTopic(){
        if(topicAdapter == null)
            return;
        topics.clear();
        topics = TopicDatabase.getInstance(getApplicationContext()).topicDAO().getlistTopic();
        topicAdapter.setData(topics);
    }
    public void dataInitialize() {
        List<Card> cards = new ArrayList<>();

        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Feelings", null, R.drawable.feelings));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Feelings", null, R.drawable.feelings));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Happy",null,  R.drawable.happy));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Angry", null, R.drawable.angry));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Bored",null,  R.drawable.bored));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Scared",null,  R.drawable.scared));


        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Sport", null, R.drawable.sports));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Football", null, R.drawable.football));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Badminton",null,  R.drawable.badminton));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Basketball", null, R.drawable.basketball));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Swimming",null,  R.drawable.swimming));


        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Food",null,  R.drawable.food));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Pasta",null,  R.drawable.pasta));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Milk",null,  R.drawable.milk));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Orange juice", null, R.drawable.orange_juice));
        CardDatabase.getInstance(getApplicationContext()).cardDAO().insertCard(new Card("Pizza", null, R.drawable.pizza));


        cards = CardDatabase.getInstance(getApplicationContext()).cardDAO().getlistCard();

       // System.out.println("HAHAHHA" + cards.size());
        topics.add(new Topic("Feelings", "Some human emotional states", null,R.drawable.feelings));
        TopicDatabase.getInstance(getApplicationContext()).topicDAO().insertTopic(topics.get(0));
        Topic topic = TopicDatabase.getInstance(getApplicationContext()).topicDAO().getlistTopic().get(0);
        for(int i = 0 ; i < 5; ++i){
            topic.addCard(cards.get(i).getId());
        }
        update(topic);

        topics.add(new Topic("Sport", "Some human activities", null,R.drawable.sports));
        TopicDatabase.getInstance(getApplicationContext()).topicDAO().insertTopic(topics.get(1));
        topic = TopicDatabase.getInstance(getApplicationContext()).topicDAO().getlistTopic().get(1);
        for(int i = 5 ; i < 10; ++i){
            topic.addCard(cards.get(i).getId());
        }
        update(topic);

        topics.add(new Topic("Food", "Some human food",null, R.drawable.food));
        TopicDatabase.getInstance(getApplicationContext()).topicDAO().insertTopic(topics.get(2));
        topic = TopicDatabase.getInstance(getApplicationContext()).topicDAO().getlistTopic().get(2);
        for(int i = 10 ; i < 15; ++i){
            topic.addCard(cards.get(i).getId());
        }
        update(topic);


    }

    void update(Topic topic){
        TopicDatabase.getInstance(getApplicationContext()).topicDAO().updateTopic(topic);
        setDataTopic();
    }


    private void searchTopic() {
        String search = etSearch.getText().toString().trim();
        topics.clear();
        topics = TopicDatabase.getInstance(getApplicationContext()).topicDAO().searchTopic(search);
        topicAdapter.setData(topics);
        hideKeyboard();
    }
    public void hideKeyboard(){
        try{
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


}