package com.example.myapp.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@Entity(tableName = "topic")
public class Topic implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nameTopic;
    private String describeTopic;
    private String imageTopic;
    private Integer idImage;
    private List<Integer> cards;

    public Topic(String nameTopic,String describeTopic,  String imageTopic, Integer idImage) {
        this.nameTopic = nameTopic;
        this.imageTopic = imageTopic;
        this.describeTopic = describeTopic;
        this.idImage = idImage;
        if(cards == null) cards = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }
    public void addCard(Integer card){
        this.cards.add(card);
    }
}
