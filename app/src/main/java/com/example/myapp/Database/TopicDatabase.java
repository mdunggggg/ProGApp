package com.example.myapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.myapp.Model.Topic;

@Database(entities = {Topic.class}, version = 1)
@TypeConverters({ListConvert.class})
public abstract class TopicDatabase extends RoomDatabase{
    private static final String DATABSE_TOPIC_NAME = "topic.db";
    private static TopicDatabase instance;
    public static synchronized TopicDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), TopicDatabase.class, DATABSE_TOPIC_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
    public abstract TopicDAO topicDAO();

}
