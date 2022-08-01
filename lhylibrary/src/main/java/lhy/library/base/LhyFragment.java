package lhy.library.base;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import autodispose2.AutoDispose;
import autodispose2.AutoDisposeConverter;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import dagger.android.support.AndroidSupportInjection;
import lhy.library.di.Injectable;

/**
 * Created by Liheyu on 2017/4/26.
 * Email:liheyu999@163.com
 */

public abstract class LhyFragment extends Fragment implements Injectable {

    private static final String STATUS_IS_HIDDEN = "STATUS_IS_HIDDEN";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isHidden = savedInstanceState.getBoolean(STATUS_IS_HIDDEN);
            if (isHidden) {
                getParentFragmentManager().beginTransaction().hide(this).commit();
            } else {
                getParentFragmentManager().beginTransaction().show(this).commit();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATUS_IS_HIDDEN, isHidden());
    }

    protected <T> AutoDisposeConverter<T> autoDispose() {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this));
    }
}
