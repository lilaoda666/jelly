package lhy.library.adapter;

import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import lhy.lhylibrary.R;
import lhy.library.utils.ToastUtils;


public class SingleSelectAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private int mPosition = 0;
    private boolean mustSelect = true;

    public SingleSelectAdapter() {
        super(R.layout.item_select);
    }

    public SingleSelectAdapter(boolean mustSelect) {
        super(R.layout.item_select);
        this.mustSelect = mustSelect;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String t) {
        int itemPosition = getItemPosition(t);
        CheckBox checkBox = holder.getView(R.id.checkbox);
        checkBox.setChecked(mPosition == itemPosition);
        holder.getView(R.id.fl_check).setOnClickListener(v -> {
            if (mPosition != itemPosition) {
                setSelectedPosition(itemPosition);
            } else {
                if (!mustSelect) {
                    setSelectedPosition(-1);
                }
            }
            ToastUtils.show(String.valueOf(mPosition));
        });
    }

    public void setSelectedPosition(int position) {
        int tem = mPosition;
        mPosition = position;
        notifyItemChanged(tem);
        notifyItemChanged(mPosition);
    }

    public int getSelectedPosition() {
        return mPosition;
    }

}
