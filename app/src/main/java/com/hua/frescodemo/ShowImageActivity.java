package com.hua.frescodemo;



import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hua.frescodemo.model.ImageModel;
import com.hua.frescodemo.model.Images;

import java.util.ArrayList;
import java.util.List;

public class ShowImageActivity extends AppCompatActivity {

    private GridView mGridView;
    private List<ImageModel> mListImg, mListGif;
    private int mGridColumnWidth, mGridSpacing;
    private GridAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
    }

    private void initData() {
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

        mAdapter = new GridAdapter(this, mListImg);

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int numColums = (int) Math.floor(mGridView.getWidth() / (mGridColumnWidth + mGridSpacing));
                if(numColums > 0) {
                    int columnWidth = mGridView.getWidth() / numColums - mGridSpacing;
                    mAdapter.setItemHeight(columnWidth);
                }
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mGridView.setAdapter(mAdapter);
    }

    public void loadImage(SimpleDraweeView draweeView, String url) {
        Uri uri = Uri.parse(url);
        draweeView.setImageURI(uri);
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
                draweeView.setLayoutParams(params);

            }else {
                draweeView = (SimpleDraweeView) convertView;
            }
            if(draweeView.getLayoutParams().height != mItemHeight) {
                draweeView.setLayoutParams(params);
            }
            loadImage(draweeView, mList.get(position).getThumb());
            return draweeView;
        }

        public void setItemHeight(int height) {
            this.mItemHeight = height;
            params = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, mItemHeight);
            notifyDataSetChanged();
        }
    }
}
