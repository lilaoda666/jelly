package com.lhy.jelly.base;

import android.graphics.Color;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.lhy.jelly.R;

import lhy.library.base.LhyActivity;
import lhy.library.utils.StatusBarUtil;

public abstract class BaseActivity extends LhyActivity {

    protected void initToolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void setStatusBar() {
        StatusBarUtil.setTransparentForImageView(this);
    }
}
