package com.mtw.rkj.articlemanager.integration;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mtw.rkj.articlemanager.integration.daos.ArticleDao;
import com.mtw.rkj.articlemanager.integration.entities.Article;

@Database(entities = {Article.class}, version = 4)
public abstract class AppRoomDatabase extends RoomDatabase {

    public abstract ArticleDao articleDao();

    private static AppRoomDatabase INSTANCE;

    public static AppRoomDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (AppRoomDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppRoomDatabase.class, "app_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
