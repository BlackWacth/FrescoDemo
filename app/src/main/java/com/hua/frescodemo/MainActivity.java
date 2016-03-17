package com.hua.frescodemo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hua.frescodemo.application.MApplication;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mImg, mGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImg = (Button) findViewById(R.id.btn_img);
        mGif = (Button) findViewById(R.id.btn_gif);

        mImg.setOnClickListener(this);
        mGif.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_img:
                mStartActivity(ShowImageActivity.class, MApplication.SHOW_IMAGE);
                break;

            case R.id.btn_gif:
                mStartActivity(ShowImageActivity.class, MApplication.SHOW_GIF);
                break;
        }
    }

    public void mStartActivity(Class<?> cls, String data) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(MApplication.SHOW_TYPE, data);
        startActivity(intent);
    }
}
