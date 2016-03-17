package com.hua.frescodemo;



import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.hua.frescodemo.application.MApplication;
import com.hua.frescodemo.model.ImageModel;
import com.hua.frescodemo.model.Images;

import java.util.ArrayList;
import java.util.List;

public class ShowImageActivity extends AppCompatActivity {

    private GridView mGridView;
    private List<ImageModel> mListImg, mListGif;
    private int mGridColumnWidth, mGridSpacing;
    private GridAdapter mAdapter;
    private SimpleDraweeView mExpandDrawee;
    private AnimatorSet mAnimatorSet;
    private Rect startRect, endRect;
    private Point globalPoint;
    private int duration;
    private boolean isGif = false;
    private boolean isExpand = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.grid_view);
        mExpandDrawee = (SimpleDraweeView) findViewById(R.id.expand_drawee);
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setFadeDuration(500)
                .setProgressBarImage(new ProgressBarDrawable())
                .setBackground(getResources().getDrawable(R.color.black_background))
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        mExpandDrawee.setHierarchy(hierarchy);

    }

    private void initData() {
        String type = getIntent().getStringExtra(MApplication.SHOW_TYPE);
        if(type.equals(MApplication.SHOW_IMAGE)) {
            isGif = false;
        }else {
            isGif = true;
        }
        duration = 500;
        mGridColumnWidth = (int) getResources().getDimensionPixelSize(R.dimen.grid_view_column_width);
        mGridSpacing = (int) getResources().getDimensionPixelSize(R.dimen.grid_view_spacing);
        if(mListImg == null) {
            mListImg = new ArrayList<>();
            for(int i = 0; i < Images.IMAGE_THUMBS.length; i++) {
                mListImg.add(new ImageModel(Images.IMAGE_THUMBS[i], Images.IMAGE_EXPAND[i]));
            }
        }
        if(mListGif == null) {
            mListGif = new ArrayList<>();
            for(int i = 0; i < Images.IMAGES_GIF.length; i++) {
                mListGif.add(new ImageModel(Images.IMAGES_GIF[i], Images.IMAGES_GIF[i]));
            }
        }
        if(!isGif) {
            mAdapter = new GridAdapter(this, mListImg);
        }else{
            mAdapter = new GridAdapter(this, mListGif);
        }

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int numColums = (int) Math.floor(mGridView.getWidth() / (mGridColumnWidth + mGridSpacing));
                if (numColums > 0) {
                    int columnWidth = mGridView.getWidth() / numColums - mGridSpacing;
                    mAdapter.setItemHeight(columnWidth);
                }
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = ((ImageModel)parent.getAdapter().getItem(position)).getExpand();
                showImage((SimpleDraweeView) view, url);
            }
        });
    }

    public void showImage(final SimpleDraweeView thumb, String url) {
        if(mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }

        loadImage(mExpandDrawee, url, true);

        startRect = new Rect();
        endRect = new Rect();
        globalPoint = new Point();

        thumb.getGlobalVisibleRect(startRect);
        findViewById(R.id.drawee_container).getGlobalVisibleRect(endRect, globalPoint);
//        mExpandDrawee.getGlobalVisibleRect(endRect, globalPoint);

        startRect.offset(-globalPoint.x, -globalPoint.y);
        endRect.offset(-globalPoint.x, -globalPoint.y);

        final float scale;
        if(((float)endRect.width() / endRect.height()) > ((float)startRect.width() / startRect.height())) {
            scale = (float)startRect.height() / endRect.height();
            int deltaWidth = (int) ((endRect.width() * scale - startRect.width()) / 2);
            startRect.left -= deltaWidth;
            startRect.right += deltaWidth;
        }else {
            scale = (float)startRect.width() / endRect.width();
            int deltaHeight = (int) ((endRect.height() * scale - startRect.height()) / 2);
            startRect.top -= deltaHeight;
            startRect.bottom += deltaHeight;
        }

        mExpandDrawee.setPivotX(0);
        mExpandDrawee.setPivotY(0);

        thumb.setAlpha(0f);
        mExpandDrawee.setVisibility(View.VISIBLE);

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(mExpandDrawee, View.X, startRect.left, endRect.left))
           .with(ObjectAnimator.ofFloat(mExpandDrawee, View.Y, startRect.top, endRect.top))
           .with(ObjectAnimator.ofFloat(mExpandDrawee, View.SCALE_X, scale, 1f))
           .with(ObjectAnimator.ofFloat(mExpandDrawee, View.SCALE_Y, scale, 1f));
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(duration);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorSet = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimatorSet = null;
            }
        });
        mAnimatorSet = set;
        set.start();

        mExpandDrawee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnimatorSet != null) {
                    mAnimatorSet.cancel();
                }
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator.ofFloat(mExpandDrawee, View.X, startRect.left))
                        .with(ObjectAnimator.ofFloat(mExpandDrawee, View.Y, startRect.top))
                        .with(ObjectAnimator.ofFloat(mExpandDrawee, View.SCALE_X, scale))
                        .with(ObjectAnimator.ofFloat(mExpandDrawee, View.SCALE_Y, scale));
                set.setInterpolator(new AccelerateDecelerateInterpolator());
                set.setDuration(duration);
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mAnimatorSet = null;
                        thumb.setAlpha(1f);
                        mExpandDrawee.setVisibility(View.GONE);
                        if(isGif){
                            Animatable animatable = mExpandDrawee.getController().getAnimatable();
                            if(animatable != null) {
                                animatable.stop();
                            }
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mAnimatorSet = null;
                        thumb.setAlpha(1f);
                        mExpandDrawee.setVisibility(View.GONE);
                        if(isGif){
                            Animatable animatable = mExpandDrawee.getController().getAnimatable();
                            if(animatable != null) {
                                animatable.stop();
                            }
                        }
                    }
                });
                mAnimatorSet = set;
                set.start();
            }
        });

    }

    public void loadImage(SimpleDraweeView draweeView, String url, final boolean isExpand) {
        Uri uri = Uri.parse(url);

        if(!isGif) {
            draweeView.setImageURI(uri);
        }else{
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setAutoPlayAnimations(false)
                    .setControllerListener(new BaseControllerListener<ImageInfo>(){
                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                            if(animatable != null && isExpand) {
                                animatable.start();
                            }
                        }

                        @Override
                        public void onRelease(String id) {
                            super.onRelease(id);
                        }
                    })
                    .build();

            draweeView.setController(controller);
        }
    }

    class GridAdapter extends BaseAdapter{
        private Context mContext;
        private List<ImageModel> mList;
        private int mItemHeight = 0;

        private GridView.LayoutParams params;

        public GridAdapter(Context mContext, List<ImageModel> mList) {
            this.mContext = mContext;
            this.mList = mList;
            params = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SimpleDraweeView draweeView = null;
            if(convertView == null) {
                draweeView = new SimpleDraweeView(mContext);
                GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                        .setPlaceholderImage(getResources().getDrawable(R.color.image_placeholder))
                        .setProgressBarImage(new ProgressBarDrawable())
                        .build();
                draweeView.setLayoutParams(params);
                draweeView.setHierarchy(hierarchy);

            }else {
                draweeView = (SimpleDraweeView) convertView;
            }
            if(draweeView.getLayoutParams().height != mItemHeight) {
                draweeView.setLayoutParams(params);
            }
            loadImage(draweeView, mList.get(position).getThumb(), false);
            return draweeView;
        }

        public void setItemHeight(int height) {
            this.mItemHeight = height;
            params = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, mItemHeight);
            notifyDataSetChanged();
        }
    }
}
