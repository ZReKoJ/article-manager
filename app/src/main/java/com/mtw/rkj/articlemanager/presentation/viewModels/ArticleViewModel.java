package com.mtw.rkj.articlemanager.presentation.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.mtw.rkj.articlemanager.integration.AppRoomDatabase;
import com.mtw.rkj.articlemanager.integration.daos.ArticleDao;
import com.mtw.rkj.articlemanager.integration.entities.Article;

public class ArticleViewModel extends AndroidViewModel {

    private ArticleDao articleDao;

    public LiveData<PagedList<Article>> articleList;

    public ArticleViewModel(Application application){
        super(application);
        this.articleDao = AppRoomDatabase.getDatabase(application.getApplicationContext()).articleDao();
        setArticleList("");
    }

    private void setArticleList(String pattern){
        articleList = new LivePagedListBuilder<>(
                this.articleDao.getAllFiltered(pattern),
                new PagedList.Config.Builder()
                        .setPageSize(10)
                        .setPrefetchDistance(10)
                        .setEnablePlaceholders(true)
                        .build())
                .setInitialLoadKey(0)
                .build();
    }

    public void replaceQuery(LifecycleOwner lifecycleOwner, String pattern){
        articleList.removeObservers(lifecycleOwner);
        setArticleList(pattern);
    }
}
