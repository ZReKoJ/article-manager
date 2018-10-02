package com.mtw.rkj.articlemanager.integration.daos;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.mtw.rkj.articlemanager.integration.entities.Article;

import java.util.List;


@Dao
public interface ArticleDao {

    @Query("SELECT * FROM article")
    DataSource.Factory<Integer, Article> getAll();

    @Query("SELECT * FROM article WHERE " +
            "barcode LIKE '%' || :pattern || '%' OR " +
            "name LIKE '%' || :pattern || '%' OR " +
            "description LIKE '%' || :pattern || '%' " +
            "ORDER BY favourite DESC")
    DataSource.Factory<Integer, Article> getAllFiltered(String pattern);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long upsert(Article article);

    @Query("SELECT * FROM article WHERE id = :id")
    Article findById(@NonNull  long id);

    @Query("SELECT * FROM article WHERE barcode = :barcode")
    Article findByBarcode(String barcode);

    @Delete
    void delete(Article article);
}
