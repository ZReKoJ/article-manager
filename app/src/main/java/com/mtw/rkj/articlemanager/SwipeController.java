package com.mtw.rkj.articlemanager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.mtw.rkj.articlemanager.presentation.adapters.ArticleAdapter;

enum SwipeState {
    GONE,
    LEFT,
    RIGHT
}

public class SwipeController extends ItemTouchHelper.Callback {

    private boolean swipeBack;
    private SwipeState swipeState = SwipeState.GONE;
    private final int leftSwipeWidth = 300;
    private final int rightSwipeWidth = 512;
    private ImageButton favouriteButton;

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection){
        if (swipeBack) {
            swipeBack = false;
            if (swipeState == SwipeState.RIGHT){
                favouriteButton.performClick();
            }
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        View foreground = ((ArticleAdapter.ArticleViewHolder)viewHolder).foreground;
        ((ArticleAdapter.ArticleViewHolder) viewHolder).backgroundEditButton.setClickable(false);
        ((ArticleAdapter.ArticleViewHolder) viewHolder).backgroundDeleteButton.setClickable(false);
        favouriteButton = ((ArticleAdapter.ArticleViewHolder) viewHolder).foregroundFavouriteButton;
        float ddX = dX, ddY = dY;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            swipeBack = isCurrentlyActive;
            if (swipeBack) {
                swipeState = (dX > rightSwipeWidth) ? SwipeState.RIGHT : (dX < -leftSwipeWidth) ? SwipeState.LEFT : SwipeState.GONE;
            }
            switch (swipeState) {
                case RIGHT:
                    break;
                case LEFT:
                    ddX -= leftSwipeWidth;
                    ((ArticleAdapter.ArticleViewHolder) viewHolder).backgroundEditButton.setClickable(true);
                    ((ArticleAdapter.ArticleViewHolder) viewHolder).backgroundDeleteButton.setClickable(true);
                    break;
                case GONE:
                    break;
            }
        }
        getDefaultUIUtil().onDraw(c, recyclerView, foreground, ddX, ddY, actionState, isCurrentlyActive);
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (dX > 0) {
            swipeRight(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    private void swipeRight(@NonNull Canvas c,
                           @NonNull RecyclerView recyclerView,
                           @NonNull RecyclerView.ViewHolder viewHolder,
                           float dX, float dY,
                           int actionState, boolean isCurrentlyActive) {
        Paint p = new Paint();
        View view = viewHolder.itemView;

        RectF background = new RectF(
                view.getLeft() + view.getPaddingLeft(),
                view.getTop() + view.getPaddingTop(),
                view.getRight() - view.getPaddingRight(),
                view.getBottom() - view.getPaddingBottom());
        p.setColor(-(Math.min((int) dX, rightSwipeWidth) / 2));
        c.drawRect(background, p);

        String text = view.getContext().getString(R.string.swipe_right_text);
        float textSize = 60;
        p.setColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
        p.setTextSize(textSize);
        c.drawText(text, dX, background.centerY() + (textSize / 2), p);
    }
}
