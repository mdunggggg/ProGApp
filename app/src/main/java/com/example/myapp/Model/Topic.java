package com.example.myapp.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Topic implements Serializable {
    private String nameTopic;
    private String describeTopic;
    private String imageTopic;
    private Integer idImage;
    private List<Card> cards;

    public Topic(String nameTopic,String describeTopic,  String imageTopic) {
        this.nameTopic = nameTopic;
        this.imageTopic = imageTopic;
        this.describeTopic = describeTopic;
        this.idImage = null;
        if(cards == null) cards = new ArrayList<>();
    }



    public Topic(String nameTopic, String describeTopic, Integer idImage) {
        this.nameTopic = nameTopic;
        this.idImage = idImage;
        this.describeTopic = describeTopic;
        this.imageTopic = null;
        if(cards == null) cards = new ArrayList<>();
    }

    public String getDescribeTopic() {
        return describeTopic;
    }

    public void setDescribeTopic(String describeTopic) {
        this.describeTopic = describeTopic;
    }

    public String getNameTopic() {
        return nameTopic;
    }

    public void setNameTopic(String nameCard) {
        this.nameTopic = nameCard;
    }

    public String getImageTopic() {
        return imageTopic;
    }

    public void setImageTopic(String imageCard) {
        this.imageTopic = imageCard;
    }
    public Integer getIdImage() {
        return idImage;
    }

    public void setIdImage(Integer idImage) {
        this.idImage = idImage;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
    public void addCard(Card card){
        this.cards.add(card);
    }
}
