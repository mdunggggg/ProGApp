package com.example.myapp.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity(tableName = "card")
public class Card implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nameCard;

    private String imageCard;
    private Integer idImage;

    public Card(String nameCard,  String imageCard, Integer idImage) {
        this.nameCard = nameCard;
        this.imageCard = imageCard;
        this.idImage = idImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameCard() {
        return nameCard;
    }

    public void setNameCard(String nameCard) {
        this.nameCard = nameCard;
    }

    public String getImageCard() {
        return imageCard;
    }

    public void setImageCard(String imageCard) {
        this.imageCard = imageCard;
    }
    public Integer getIdImage() {
        return idImage;
    }

    public void setIdImage(Integer idImage) {
        this.idImage = idImage;
    }
}
