package com.mtw.rkj.articlemanager.presentation.adapters;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtw.rkj.articlemanager.R;
import com.mtw.rkj.articlemanager.integration.entities.Article;
import com.mtw.rkj.articlemanager.presentation.activities.ArticleActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ArticleAdapter extends PagedListAdapter<Article, ArticleAdapter.ArticleViewHolder> {

    private Context context;
    private OnActionListener actionListener;

    public ArticleAdapter(Context context){
        super(Article.DIFF_CALLBACK);
        this.context = context;
    }

    public void setOnActionListener(OnActionListener clickListener){
        this.actionListener = clickListener;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ArticleViewHolder(
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(
                                R.layout.article_card, viewGroup,
                                false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = getItem(position);
        if (article != null) {
            Picasso.with(context).load(
                    new File(
                            context.getDir(ArticleActivity.IMAGE_FOLDER, Context.MODE_PRIVATE),
                            article.getId() + ArticleActivity.IMAGE_EXTENSION))
                    .error(R.drawable.no_image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(500, 500)
                    .centerCrop()
                    .into(holder.foregroundArticleImage);

            holder.foregroundBarcode.setText(article.getBarcode());
            holder.foregroundName.setText(article.getName());
            holder.foregroundDescription.setText(article.getDescription());
            holder.foregroundFavouriteButton.setImageResource(
                    (article.isFavourite()) ? android.R.drawable.star_big_on : android.R.drawable.star_big_off);
            holder.backgroundEditButton.setClickable(false);
            holder.backgroundDeleteButton.setClickable(false);
        }
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        public CardView foreground;
        ImageView foregroundArticleImage;
        TextView foregroundBarcode;
        TextView foregroundName;
        TextView foregroundDescription;
        public ImageButton foregroundFavouriteButton;
        RelativeLayout background;
        public ImageButton backgroundDeleteButton;
        public ImageButton backgroundEditButton;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            foreground = (CardView)itemView.findViewById(R.id.view_foreground);
            foregroundArticleImage = (ImageView)itemView.findViewById(R.id.article_image);
            foregroundBarcode = (TextView)itemView.findViewById(R.id.article_barcode);
            foregroundName = (TextView)itemView.findViewById(R.id.article_name);
            foregroundDescription = (TextView)itemView.findViewById(R.id.article_description);
            foregroundFavouriteButton = (ImageButton)itemView.findViewById(R.id.favourite_button);
            foregroundFavouriteButton.setOnClickListener(view -> {
                if (actionListener != null){
                    actionListener.onFavourite(view, getItem(getLayoutPosition()));
                }
            });
            background = (RelativeLayout)itemView.findViewById(R.id.view_background);
            backgroundEditButton = (ImageButton)itemView.findViewById(R.id.edit_button);
            backgroundDeleteButton = (ImageButton)itemView.findViewById(R.id.delete_button);
            backgroundEditButton.setOnClickListener(view -> {
                if (actionListener != null) {
                    actionListener.onEdit(view, getItem(getLayoutPosition()));
                }
            });
            backgroundDeleteButton.setOnClickListener(view -> {
                if (actionListener != null) {
                    actionListener.onDelete(view, getItem(getLayoutPosition()));
                }
            });
        }
    }

    public interface OnActionListener {
        void onEdit(View view, Article article);
        void onDelete(View view, Article article);
        void onFavourite(View view, Article article);
    }
}
