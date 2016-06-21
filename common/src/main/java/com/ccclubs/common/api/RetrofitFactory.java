package com.ccclubs.common.api;

import com.ccclubs.common.support.ConfigurationHelper;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 用于获取配置好的retrofit对象, 通过设置{@link ConfigurationHelper#enableLoggingNetworkParams()}来启用网络请求
 * 参数与相应结果.
 * <br/>
 * TODO:<ul><li>1、如果有多个baseUrl</li><li>2、需要定制化OkHttpclent，如加入session id 等</li></ul>
 */
public class RetrofitFactory {
  //private static Retrofit retrofit;
  private static String baseUrl;

  public static void setBaseUrl(String url) {
    baseUrl = url;
  }

  /**
   * 获取配置好的retrofit对象来生产Manager对象
   */
  public static Retrofit getRetrofit() {
    return getRetrofit(GsonConverterFactory.create());
  }

  /**
   * 获取配置好的retrofit对象来生产Manager对象
   */
  public static Retrofit getRetrofit(Converter.Factory factory) {
    //if (retrofit == null) {
    if (baseUrl == null || baseUrl.length() <= 0) {
      throw new IllegalStateException("请在调用getFactory之前先调用setBaseUrl");
    }

    Retrofit.Builder builder = new Retrofit.Builder();

    builder.baseUrl(baseUrl)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(factory);

    if (ConfigurationHelper.isShowNetworkParams()) {
      OkHttpClient client =
          new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor()).build();

      builder.client(client);
    }

    return builder.build();
    //}

    //return retrofit;
  }

  public static Retrofit getRetrofit(Interceptor interceptor, Converter.Factory factory) {
    //if (retrofit == null) {
    if (baseUrl == null || baseUrl.length() <= 0) {
      throw new IllegalStateException("请在调用getFactory之前先调用setBaseUrl");
    }

    Retrofit.Builder builder = new Retrofit.Builder();

    builder.baseUrl(baseUrl)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(factory);

    if (ConfigurationHelper.isShowNetworkParams()) {
      OkHttpClient client =
          new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor()).build();

      builder.client(client);
    }

    return builder.build();
    //}

    //return retrofit;
  }
}
