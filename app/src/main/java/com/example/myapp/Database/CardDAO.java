package com.example.myapp.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapp.Model.Card;

import java.util.List;

@Dao
public interface CardDAO {
    @Insert
    void insertCard(Card card);
    @Query("SELECT * FROM card")
    List<Card> getlistCard();
    @Query("SELECT * FROM card WHERE id = :id")
    Card getCardById(int id);
    @Query("DELETE FROM card")
    void deleteAllCard();
    @Insert
    long insertCardReturnId(Card card);
}
