package com.lhy.jelly.ui.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lhy.jelly.databinding.AbsBaseChatBinding;
import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.openim.android.sdk.models.Message;

public abstract class AbsBaseChatViewHolder extends RecyclerView.ViewHolder {

    private static final int SHOW_TIME_INTERVAL = 5 * 60 * 1000;

    protected AbsBaseChatBinding baseViewBinding;
    protected int type;
    protected ViewGroup parent;
    public long receiptTime;
    private Message currentMessage;

    public AbsBaseChatViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public AbsBaseChatViewHolder(@NonNull AbsBaseChatBinding parent, int viewType) {
        this(parent.baseRoot);
        this.parent = parent.getRoot();
        this.type = viewType;
        baseViewBinding = parent;
    }


    public void bindData(Message message, Message lastMessage) {
        currentMessage = message;
        int padding = SmartUtil.dp2px(8);
        baseViewBinding.baseRoot.setPadding(padding, padding, padding, padding);
        baseViewBinding.messageContainer.removeAllViews();
        addContainer();
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) baseViewBinding.messageBody.getLayoutParams();
        RoundedImageView fromAvatar = baseViewBinding.fromAvatar;
        RoundedImageView avatarMine = baseViewBinding.avatarMine;
        if (isReceivedMessage(message)) {
            Glide.with(fromAvatar).load(message.getSenderFaceUrl()).into(fromAvatar);
            fromAvatar.setVisibility(View.VISIBLE);
            avatarMine.setVisibility(View.GONE);
            layoutParams.horizontalBias = 0;
        } else {
            Glide.with(fromAvatar).load(message.getSenderFaceUrl()).into(avatarMine);
            fromAvatar.setVisibility(View.GONE);
            avatarMine.setVisibility(View.VISIBLE);
            layoutParams.horizontalBias = 1;
        }
        setTime(message, lastMessage);
    }

    private void setTime(Message message, Message lastMessage) {
        long createTime = message.getCreateTime() == 0 ? System.currentTimeMillis() : message.getCreateTime();
        if (lastMessage != null
                && createTime - lastMessage.getCreateTime() < SHOW_TIME_INTERVAL) {
            baseViewBinding.tvTime.setVisibility(View.GONE);
        } else {
            baseViewBinding.tvTime.setVisibility(View.VISIBLE);
            baseViewBinding.tvTime.setText(formatMillisecond(createTime));
        }
    }

    protected boolean isReceivedMessage(Message message) {
        return TextUtils.equals(message.getRecvID(), "1");
    }

    protected void addContainer() {

    }

    public ViewGroup getContainer(){
        return baseViewBinding.messageContainer;
    }


    public void bindData(Message data, @NonNull List<?> payload) {
        if (!payload.isEmpty()) {
            for (int i = 0; i < payload.size(); ++i) {
                String payloadItem = payload.get(i).toString();
            }
        }
    }

    public long getReceiptTime() {
        return receiptTime;
    }

    public void setReceiptTime(long receiptTime) {
        this.receiptTime = receiptTime;
    }

    public static String formatMillisecond(long millisecond) {
        long nowTime = System.currentTimeMillis();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(nowTime);
        int nowDay = nowCalendar.get(5);
        int nowYear = nowCalendar.get(1);
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(millisecond);
        int timeDay = timeCalendar.get(5);
        int timeYear = timeCalendar.get(1);
        Date dateTime = new Date(millisecond);
        SimpleDateFormat fullTimeFormat;
        if (timeYear != nowYear) {
            fullTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        } else if (timeDay != nowDay) {
            fullTimeFormat = new SimpleDateFormat("MM-dd HH:mm");
        } else {
            fullTimeFormat = new SimpleDateFormat("HH:mm");
        }

        return fullTimeFormat.format(dateTime);
    }
}
