package com.example.myapp;

import static java.lang.Math.abs;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kotlin.Suppress;

public class PlayActivity extends AppCompatActivity {
    private Boolean isMoving;
    private float xStart;
    private float yStart;
    private DisplayMetrics displayMetrics;
    private float screenHeight;
    private float screenWidth;
    private ActionBar actionBar;
    private SpannableString spannableString;
    private List<Topic> topics = new ArrayList<>();
    private TopicAdapter topicAdapter;
    private TextToSpeech textToSpeech;
    private RecyclerView recyclerView;
    private ImageButton btnAdd;
    private FrameLayout fragment_container;
    private ImageButton eraseBar;
    private ImageButton speakBar;
    private BarFragment barFragment;


    // Navigation Drawer
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;


    // Bottom Sheet
    private LinearLayout layoutBottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageButton btnBar;
    private Boolean stateBar;

    // Search
    private EditText etSearch;

    // PopupWindow
   private PopupWindow popupWindow;
   private Animation animation;
    private LayoutInflater layoutInflater;
    private View popupView;



    // ActivityResultLauncher: get information from child activity to parent activity
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == CONSTANT.RESULT_ADD_TOPIC) {
              setDataTopic();
        }
        else if(result.getResultCode() == CONSTANT.SHOW_CARD_ACTIVITY) {
            Intent intent = result.getData();
            Bundle bundle = intent.getExtras();
            barFragment.setCardBar((List<Card>) intent.getSerializableExtra("Bar List Card"));
            stateBar = bundle.getBoolean("State Bar");
            changeStateBar();
            setDataTopic();
        }
        else if(result.getResultCode() == CONSTANT.RESULT_EDIT_TOPIC){
            setDataTopic();
        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

//        // Delete Data
        //TopicDatabase.getInstance(this).topicDAO().deleteAllTopic();
        //CardDatabase.getInstance(this).cardDAO().deleteAllCard();

        // Create PopupWindow
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_popup);
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.main_popup, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupView.startAnimation(animation);




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


        // ActionBar
        actionBar = getSupportActionBar();
        spannableString = new SpannableString("LearnTogether");
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.0f), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(spannableString);

        // Navigation Drawer

        drawerLayout = findViewById(R.id.activity_main_drawer);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Vietnamese:
                        changeLanguage("vi");
                        break;
                    case R.id.English:
                        changeLanguage("en");
                        break;
                }
                return false;
            }
        });





        // Fragment for card bar

        fragment_container = findViewById(R.id.fragment_container);
        barFragment = new BarFragment();
        replaceFragment(barFragment);


//        // Original Data
//        dataInitialize();

        // Set adapter and Touch listener for recycler view
        recyclerView = findViewById(R.id.rcv_items);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(PlayActivity.this, topics.get(position).getNameTopic(), Toast.LENGTH_LONG).show();
                speak(topics.get(position).getNameTopic());
                goToShowCardActivity(topics.get(position));

            }
            @Override
            public void onLongItemClick(View view, int position) {
                speak(topics.get(position).getNameTopic());
                popupView.startAnimation(animation);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                ImageView imageView = popupView.findViewById(R.id.imgTopic);
                TextView textView = popupView.findViewById(R.id.nameTopic);
                if(topics.get(position).getIdImage() != null)
                    imageView.setImageResource(topics.get(position).getIdImage());
                else
                    imageView.setImageBitmap(BitmapFactory.decodeFile(topics.get(position).getImageTopic()));
                textView.setText(topics.get(position).getNameTopic());
                ImageButton delete = popupView.findViewById(R.id.deleteTopic);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Integer>list = topics.get(position).getCards();
                        Card card = null;
                        for(int i = 0 ; i < list.size(); ++i){
                            card = CardDatabase.getInstance(getApplicationContext()).cardDAO().getCardById(list.get(i));
                            CardDatabase.getInstance(getApplicationContext()).cardDAO().deleteCard(card);
                        }
                        TopicDatabase.getInstance(getApplicationContext()).topicDAO().deleteTopic(topics.get(position));
                        setDataTopic();
                        popupWindow.dismiss();
                    }
                });

                ImageButton edit = popupView.findViewById(R.id.editTopic);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), EditTopicActivity.class);
                        intent.putExtra("Topic", topics.get(position));
                        activityResultLauncher.launch(intent);
                        popupWindow.dismiss();
                    }
                });
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popupWindow.dismiss();
                    }
                });
            }
        }));
        topicAdapter = new TopicAdapter(getApplicationContext());
        recyclerView.setAdapter(topicAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        if(TopicDatabase.getInstance(getApplicationContext()).topicDAO().countTopic() == 0){
            dataInitialize();
        }
        setDataTopic();

        // Convert text to speech
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                       // Toast.makeText(PlayActivity.this, "This language is not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(PlayActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

       // Add topic
        btnAdd = findViewById(R.id.addButton);
        isMoving = false;
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels - 100;
        screenWidth = displayMetrics.widthPixels;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMoving)
                    goToAddActivity();
            }
        });

        btnAdd.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
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
                        if (abs(xDiff) > CONSTANT.LIMIT_BUTTON_MOVE || abs(yDiff) > CONSTANT.LIMIT_BUTTON_MOVE) {
                            isMoving = true;
                        }

                        if (isMoving) {
                            float newX = btnAdd.getX() + (xDiff - xStart);
                            float newY = btnAdd.getY() + (yDiff - yStart);

                            if (newX < 0) {
                                newX = 0;
                            }
                            if (newX > (screenWidth - btnAdd.getWidth())) {
                                newX = screenWidth - btnAdd.getWidth();
                            }
                            if (newY < 0) {
                                newY = 0;
                            }
                            if (newY > (screenHeight - btnAdd.getHeight())) {
                                newY = screenHeight - btnAdd.getHeight();
                            }
                            btnAdd.setX(newX);
                            btnAdd.setY(newY);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!isMoving) {
                            goToAddActivity();
                        }
                        return true;
                    default:
                        return false;
                }
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


    //Navigation

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }




    // Change Language
    public void changeLanguage(String languageCode){
        Locale locale = new Locale(languageCode);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        recreate();
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