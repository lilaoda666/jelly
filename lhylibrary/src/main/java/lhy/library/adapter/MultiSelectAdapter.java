package lhy.library.adapter;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lhy.lhylibrary.R;
import lhy.library.utils.ToastUtils;


public class MultiSelectAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private final Map<Integer, Boolean> mSelectedMap = new HashMap<>();

    public MultiSelectAdapter() {
        super(R.layout.item_select);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String t) {
        int itemPosition = getItemPosition(t);
        CheckBox checkBox = holder.getView(R.id.checkbox);
        Boolean aBoolean = mSelectedMap.get(itemPosition);
        checkBox.setChecked(aBoolean != null && aBoolean);
        holder.getView(R.id.fl_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = checkBox.isChecked();
                if(checked){
                    mSelectedMap.remove(itemPosition);
                }else {
                    mSelectedMap.put(itemPosition,true);
                }

                notifyItemChanged(itemPosition);
                ToastUtils.show(JSON.toJSONString(mSelectedMap.keySet()));
            }

        });
    }

    public void setSelectedPosition(int position) {
        mSelectedMap.put(position, true);
    }

    public Set<Integer> getSelectedPosition() {
        return mSelectedMap.keySet();
    }
}
