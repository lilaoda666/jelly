package lhy.library.http;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;


public class HttpManager {

    private static HttpManager instance = null;

    private final Retrofit.Builder mRetrofitBuilder;

    private HttpManager() {
        mRetrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(OkHttpManager.getInstance().getHttp());
    }

    public static HttpManager getInstance() {
        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null) {
                    instance = new HttpManager();
                }
            }
        }
        return instance;
    }

    public Retrofit.Builder getRetrofitBuilder() {
        return mRetrofitBuilder;
    }

    /**
     * 创建基于公共URL的服务
     */
    public <T> T createService(final Class<T> service) {
        return mRetrofitBuilder.build().create(service);
    }

    /**
     * 创建基于特定URL的服务
     */
    public <T> T createService(String baseUrl, final Class<T> service) {
        return mRetrofitBuilder.baseUrl(baseUrl).build().create(service);
    }

}
