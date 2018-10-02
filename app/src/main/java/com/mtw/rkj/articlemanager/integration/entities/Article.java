package com.mtw.rkj.articlemanager.integration.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.text.TextUtils;

import java.io.Serializable;

@Entity
public class Article implements Serializable{

    @PrimaryKey(autoGenerate = true) @NonNull
    private long id;

    @ColumnInfo(name = "barcode")
    private String barcode;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "format")
    private String barcodeFormat;

    @ColumnInfo(name = "description")
    private String description;

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @ColumnInfo(name = "favourite")
    private boolean favourite = false;

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcodeFormat() {
        return barcodeFormat;
    }

    public void setBarcodeFormat(String barcodeFormat) {
        this.barcodeFormat = barcodeFormat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean contains(String s){
        boolean contains = false;
        if (!TextUtils.isEmpty(barcode)) { contains = contains || barcode.toUpperCase().contains(s); }
        if (!TextUtils.isEmpty(name)) { contains = contains || name.toUpperCase().contains(s); }
        return contains;
    }

    public static DiffUtil.ItemCallback<Article> DIFF_CALLBACK = new DiffUtil.ItemCallback<Article>() {
        @Override
        public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.equals(newItem);
        }
    };

}
