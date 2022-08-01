package com.lhy.jelly.utils;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

public class TextClickSpan extends ClickableSpan {
    @Override
    public void onClick(@NonNull View widget) {
        clickText(widget);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.bgColor=0;
    }

    public void clickText(@NonNull View widget) {

    }
}
