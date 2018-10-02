package com.mtw.rkj.articlemanager.presentation.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mtw.rkj.articlemanager.ImageManager;
import com.mtw.rkj.articlemanager.PermissionsRequest;
import com.mtw.rkj.articlemanager.R;
import com.mtw.rkj.articlemanager.integration.AppRoomDatabase;
import com.mtw.rkj.articlemanager.integration.entities.Article;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ArticleActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_PERMISSION = 1777;
    private static final int SCAN_BARCODE_REQUEST = 49374;

    public static final String IMAGE_EXTENSION = ".jpg";
    public static final String IMAGE_FOLDER = "article_image";

    private Article article;

    private ImageView articleImage;
    private FloatingActionButton saveButton;

    private TextView articleBarcode;
    private TextInputLayout articleBarcodeInput;
    private TextView articleName;
    private TextInputLayout articleNameInput;
    private TextView articleDescription;
    private TextInputLayout articleDescriptionInput;

    private ImageButton scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        articleImage = (ImageView)findViewById(R.id.article_image);

        articleBarcode = (TextInputEditText)findViewById(R.id.article_barcode);
        articleBarcode.setEnabled(false);
        articleBarcodeInput = (TextInputLayout)findViewById(R.id.article_barcode_input);

        articleName = (TextInputEditText)findViewById(R.id.article_name);
        articleNameInput = (TextInputLayout)findViewById(R.id.article_name_input);

        articleDescription = (TextInputEditText)findViewById(R.id.article_description);
        articleDescriptionInput = (TextInputLayout)findViewById(R.id.article_description_input);

        article = new Article();

        loadArticle((Article)getIntent().getSerializableExtra("ARTICLE"));

        if (!TextUtils.isEmpty(article.getBarcode())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Article dataArticle = AppRoomDatabase.getDatabase(ArticleActivity.this).articleDao().findByBarcode(article.getBarcode());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadArticle(dataArticle);
                        }
                    });
                }
            }).start();
        }

        saveButton = (FloatingActionButton) findViewById(R.id.article_save);
        scanButton = (ImageButton)findViewById(R.id.article_barcode_scan);
    }

    /**
     * Registering listeners
     */
    @Override
    protected void onResume() {
        super.onResume();

        articleImage.setOnClickListener((view) -> {
                if (new PermissionsRequest(ArticleActivity.this, CAMERA_PERMISSION,
                        new String[]{
                            Manifest.permission.CAMERA
                        })
                        .always()
                        .addReAskText(getString(R.string.app_name) + " needs permission for capture image")
                        .ask()){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            });

        saveButton.setOnClickListener((view) -> {
            saveArticle();
        });

        scanButton.setOnClickListener((view) -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(ArticleActivity.this);
            intentIntegrator.initiateScan();
        });
    }

    /**
     * Unregistering listeners
     */
    @Override
    protected void onPause(){
        super.onPause();
        articleImage.setOnClickListener(null);
        saveButton.setOnClickListener(null);
        scanButton.setOnClickListener(null);
    }

    private void loadArticle(Article article){
        if (article != null) {
            this.article = article;

            Picasso.with(ArticleActivity.this).load(
                    new File(
                            ArticleActivity.this.getDir(ArticleActivity.IMAGE_FOLDER, Context.MODE_PRIVATE),
                            article.getId() + ArticleActivity.IMAGE_EXTENSION))
                    .error(R.drawable.no_image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(articleImage);
            articleBarcode.setText(article.getBarcode());
            articleName.setText(article.getName());
            articleDescription.setText(article.getDescription());
        }
    }

    private void saveArticle(){
        article.setName(articleName.getText().toString());

        if (TextUtils.isEmpty(article.getName())){
            Toast.makeText(getApplicationContext(),
                    getString(R.string.save_fail_no_name),
                    Toast.LENGTH_LONG)
                    .show();
        }
        else {
            article.setDescription(articleDescription.getText().toString());

            new Thread(() -> {
                long id = AppRoomDatabase.getDatabase(ArticleActivity.this).articleDao().upsert(article);
                article.setId(id);
                runOnUiThread(() -> {
                    new ImageManager(ArticleActivity.this)
                            .setFileName(article.getId() + IMAGE_EXTENSION)
                            .setDirectoryName(IMAGE_FOLDER)
                            .noImage(true)
                            .save(((BitmapDrawable) articleImage.getDrawable()).getBitmap());

                    Toast.makeText(getApplicationContext(),
                            String.format(getString(R.string.upsert_success),
                                    article.getName()),
                            Toast.LENGTH_LONG)
                            .show();
                });
            }).start();
        }
    }

    private void deleteArticle(){
        if (article.getId() <= 0){
            Toast.makeText(getApplicationContext(),
                    getString(R.string.delete_fail_not_created),
                    Toast.LENGTH_LONG)
                    .show();
        }
        else {
            new Thread(() -> {
                AppRoomDatabase.getDatabase(ArticleActivity.this).articleDao().delete(article);
                runOnUiThread(() -> {
                    Snackbar.make(
                        findViewById(R.id.activity_article_layout),
                        String.format(getString(R.string.delete_success), article.getName()),
                        Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.action_undo), (v) -> {
                            new Thread(() -> {
                                AppRoomDatabase.getDatabase(ArticleActivity.this).articleDao().upsert(article);
                            }).start();
                        }).show();
                });
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK){
                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                    articleImage.setImageBitmap(bitmap);
                }
                break;
            case SCAN_BARCODE_REQUEST:
                if (resultCode == RESULT_OK) {
                    IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

                    if (intentResult != null) {
                        article.setBarcode(data.getStringExtra("SCAN_RESULT"));
                        article.setBarcodeFormat(data.getStringExtra("SCAN_RESULT_FORMAT"));
                        articleBarcode.setText(article.getBarcode());
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode){
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(
                    ArticleActivity.this.
                            getCurrentFocus()
                            .getWindowToken(),
                    0);
        switch (item.getItemId()){
            case R.id.action_add:
                loadArticle(new Article());
                break;
            case R.id.action_delete:
                deleteArticle();
                break;
            case R.id.action_save:
                saveArticle();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
