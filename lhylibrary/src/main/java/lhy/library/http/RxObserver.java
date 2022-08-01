package lhy.library.http;

/**
 * author: liheyu
 * date: 2019-11-06
 * email: liheyu999@163.com
 */
public abstract class RxObserver<T> extends AbsObserver<T> {

    public RxObserver() {
        super(true);
    }

    public RxObserver(String dialogMsg) {
        super(dialogMsg);
    }

    public RxObserver(boolean showToast) {
        super(showToast);
    }

    public RxObserver(String dialogMsg, boolean showToast) {
        super(dialogMsg, showToast);
    }


}
