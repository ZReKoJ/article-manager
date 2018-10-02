package com.mtw.rkj.articlemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageManager {

    private String fileName;
    private String directoryName;

    private boolean noImage = false;

    private Context context;

    public ImageManager(Context context){
        this.context = context;
    }

    public ImageManager setFileName(String fileName){
        this.fileName = fileName;
        return this;
    }

    public ImageManager setDirectoryName(String directoryName){
        this.directoryName = directoryName;
        return this;
    }

    public ImageManager noImage(boolean noImage){
        this.noImage = noImage;
        return this;
    }

    @NonNull
    public File createFile() {
        File directory;
        directory = context.getDir(directoryName, Context.MODE_PRIVATE);

        if(!directory.exists() && !directory.mkdirs()){
            Log.e("ImageManager","Error creating directory " + directory);
        }

        return new File(directory, fileName);
    }

    public void save(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
/*
    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (noImage){
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
        }

        return null;
    }*/

}
