package lhy.library.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;

public class ActivityUtils {

    private final static List<Activity> activities = new LinkedList<>();

    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                activities.add(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                activities.remove(activity);
            }
        });

    }

    public static Activity getCurrentActivity() {
        if (activities.size() == 0) {
            return null;
        }
        return activities.get(activities.size() - 1);
    }

    public static void addActivity(LhyActivity activity) {
        activities.add(activity);
    }

    public static void removeActivity(LhyActivity activity) {
        activities.remove(activity);
    }


    public static void closeApplication() {
        closeActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void closeActivity() {
        for (Activity activity : activities) {
            if (activity != null) {
                activity.finish();
            }
        }
    }

    public static void finishOtherActivity(Activity nowAct) {
        for (Activity activity : activities) {
            if (activity != null && activity != nowAct) {
                activity.finish();
            }
        }
    }

    public static void finishTheActivity(Class<? extends Activity> nowAct) {
        for (Activity activity : activities) {
            if (activity != null && TextUtils.equals(activity.getClass().getName(), nowAct.getName())) {
                activity.finish();
            }
        }
    }

}
