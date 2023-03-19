package com.example.myapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.Adapter.BarAdapter;
import com.example.myapp.Model.Card;
import com.example.myapp.Model.Topic;

import java.util.ArrayList;
import java.util.List;

public class BarFragment extends Fragment{

    private List<Card> cardsBar;
    private RecyclerView recyclerView;
    private BarAdapter barAdapter;
    private Boolean initData = true;



    public BarFragment() {
        // Required empty public constructor
    }
    public BarFragment(List<Card>cards){
        this.cardsBar = cards;
        initData = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      //  fakeData();

//        recyclerView = view.findViewById(R.id.rcv_itemsBar);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setHasFixedSize(true);
//        BarAdapter adapter = new BarAdapter(getContext());
//        recyclerView.setAdapter(adapter);
//        adapter.setData(tocpicBar);
        recyclerView = view.findViewById(R.id.rcv_itemsBar);
        barAdapter = new BarAdapter(getContext());


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(barAdapter);
        if(initData == true)
            dataInitialize();
        barAdapter.setData(cardsBar);


    }
    public void erase(){
        barAdapter.eraseBar();

    }
    void add(Card card){
        barAdapter.add(card);
    }
    void speak(){
        barAdapter.speakBar();
    }

    public List<Card> getCardBar() {
        return cardsBar;
    }

    public void setCardBar(List<Card> cardBar) {
        this.cardsBar = cardBar;
        barAdapter.setCardsBar(cardBar);

    }
    private void dataInitialize(){
        cardsBar = new ArrayList<>();
        cardsBar.add(new Card("Happy", null, R.drawable.happy));
        cardsBar.add(new Card("Sport", null,R.drawable.sports));
        cardsBar.add(new Card("Scared",null, R.drawable.scared));
        cardsBar.add(new Card("Pasta",null, R.drawable.pasta));
        cardsBar.add(new Card("Basketball",null, R.drawable.basketball));
        cardsBar.add(new Card("Orange juice",null, R.drawable.orange_juice));
    }
}