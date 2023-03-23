package com.example.myapp.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapp.Model.Card;
import com.example.myapp.Model.Topic;

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
    @Query("SELECT * FROM card WHERE nameCard LIKE '%' || :name || '%'")
    List<Card>searchCard(String name);
    @Update
    void updateCard(Card card);
}
