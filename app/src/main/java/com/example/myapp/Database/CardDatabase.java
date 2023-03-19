package com.example.myapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapp.Model.Card;
import com.example.myapp.Model.Topic;

@Database(entities = {Card.class}, version = 1)
public abstract class CardDatabase extends RoomDatabase {
    private static final String DATABSE_CARD_NAME = "card.db";
    private static CardDatabase instance;
    public static synchronized CardDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), CardDatabase.class, DATABSE_CARD_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
    public abstract CardDAO cardDAO();
}
