package com.mtw.rkj.articlemanager.presentation.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.mtw.rkj.articlemanager.R;
import com.mtw.rkj.articlemanager.SwipeController;
import com.mtw.rkj.articlemanager.integration.AppRoomDatabase;
import com.mtw.rkj.articlemanager.integration.entities.Article;
import com.mtw.rkj.articlemanager.presentation.adapters.ArticleAdapter;
import com.mtw.rkj.articlemanager.presentation.viewModels.ArticleViewModel;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addArticle;

    private ArticleAdapter articleAdapter;
    private RecyclerView recyclerView;
    private ArticleViewModel articleViewModel;
    private SwipeController swipeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addArticle = (FloatingActionButton) findViewById(R.id.add_article);

        articleViewModel = ViewModelProviders.of(MainActivity.this).get(ArticleViewModel.class);

        recyclerView = (RecyclerView)findViewById(R.id.article_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        articleAdapter = new ArticleAdapter(MainActivity.this);

        articleViewModel.articleList.observe(MainActivity.this, articleAdapter::submitList);

        recyclerView.setAdapter(articleAdapter);

        swipeController = new SwipeController();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume(){
        super.onResume();

        addArticle.setOnClickListener((view) -> {
            Intent result = new Intent(MainActivity.this, ArticleActivity.class);
            result.putExtra("ARTICLE", new Article());
            startActivity(result);
        });

        articleAdapter.setOnActionListener(new ArticleAdapter.OnActionListener() {
            @Override
            public void onEdit(View view, Article article) {
                Intent result = new Intent(MainActivity.this, ArticleActivity.class);
                result.putExtra("ARTICLE", article);
                startActivity(result);
            }

            @Override
            public void onDelete(View view, Article article) {
                new Thread(() -> {
                    AppRoomDatabase.getDatabase(MainActivity.this).articleDao().delete(article);
                    runOnUiThread(() -> {
                        Snackbar.make(
                                    findViewById(R.id.activity_main_layout),
                                    String.format(getString(R.string.delete_success), article.getName()),
                                    Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.action_undo), (v) -> {
                                    new Thread(() -> {
                                        AppRoomDatabase.getDatabase(MainActivity.this).articleDao().upsert(article);
                                    }).start();
                                }).show();
                    });
                }).start();
            }

            @Override
            public void onFavourite(View view, Article article) {
                article.setFavourite(!article.isFavourite());
                new Thread(() -> {
                    AppRoomDatabase.getDatabase(MainActivity.this).articleDao().upsert(article);
                }).start();
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        addArticle.setOnClickListener(null);
        articleAdapter.setOnActionListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                articleViewModel.replaceQuery(MainActivity.this, s);
                articleViewModel.articleList.observe(MainActivity.this, articleAdapter::submitList);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_search:
                break;
            case R.id.action_exit:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
