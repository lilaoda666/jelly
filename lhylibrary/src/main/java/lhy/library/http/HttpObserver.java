package lhy.library.http;

/**
 * author: liheyu
 * date: 2019-11-06
 * email: liheyu999@163.com
 */
public abstract class HttpObserver<T> extends AbsObserver<T> {

    public static final String dialogMsg = "正在加载...";

    public HttpObserver() {
        super(dialogMsg);
    }

    public HttpObserver(String dialogMsg) {
        super(dialogMsg);
    }

    public HttpObserver(boolean showToast) {
        super(showToast);
    }

    public HttpObserver(String dialogMsg, boolean showToast) {
        super(dialogMsg, showToast);
    }


}
