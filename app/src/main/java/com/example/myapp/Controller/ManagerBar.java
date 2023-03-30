package com.example.myapp.Controller;

import com.example.myapp.Model.Card;

import java.util.List;

public class ManagerBar {
    private List<Card> topicList;
    public static ManagerBar instance;
    private ManagerBar() {
    }
    public static ManagerBar getInstance() {
        if (instance == null) {
            instance = new ManagerBar();
        }
        return instance;
    }
}
