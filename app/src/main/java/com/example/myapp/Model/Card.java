package com.example.myapp.Model;

import java.io.Serializable;

public class Card implements Serializable {
    private String nameCard;

    private String imageCard;
    private Integer idImage;

    public Card(String nameCard,  String imageCard) {
        this.nameCard = nameCard;
        this.imageCard = imageCard;
        this.idImage = null;
    }



    public Card(String nameCard, Integer idImage) {
        this.nameCard = nameCard;
        this.idImage = idImage;
        this.imageCard = null;
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
