package com.example.myapp.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapp.Model.Topic;

import java.util.List;

@Dao
public interface TopicDAO {
    @Insert
    void insertTopic(Topic topic);
    @Query("SELECT * FROM topic")
    List<Topic> getlistTopic();
    @Delete
    void deleteTopic(Topic topic);
    @Query("DELETE FROM topic")
    void deleteAllTopic();
    @Query("SELECT * FROM topic WHERE id = :id")
    Topic getTopicById(int id);
    @Update
    void updateTopic(Topic topic);
    @Query("SELECT * FROM topic WHERE nameTopic LIKE '%' || :name || '%'")
    List<Topic>searchTopic(String name);
}
