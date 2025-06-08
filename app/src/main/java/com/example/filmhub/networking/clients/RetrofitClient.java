package com.example.filmhub.networking.clients;

import com.example.filmhub.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Kelas ini bertanggung jawab untuk membuat satu instance Retrofit (Singleton)
 * yang akan digunakan di seluruh aplikasi untuk melakukan panggilan API.
 */
public class RetrofitClient {

    // Base URL dari TMDb API
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    // Instance Retrofit yang akan kita gunakan (awalnya null)
    private static Retrofit retrofit = null;

    /**
     * Metode untuk mendapatkan instance Retrofit.
     * Jika instance belum ada, metode ini akan membuatnya.
     * Jika sudah ada, metode ini akan mengembalikan instance yang sama.
     *
     * @return instance Retrofit
     */
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // 1. Membuat Logging Interceptor untuk debugging
            // Ini akan mencetak request dan response body ke Logcat
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Membuat OkHttpClient dan menambahkan Interceptor HANYA untuk mode DEBUG
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                // Menambahkan interceptor jika aplikasi dalam mode debug
                httpClient.addInterceptor(loggingInterceptor);
            }

            // 3. Membuat instance Retrofit dengan Builder
            retrofit = new Retrofit.Builder()
                    // Menetapkan URL dasar untuk semua panggilan API
                    .baseUrl(BASE_URL)
                    // Menambahkan converter factory untuk mengubah JSON menjadi objek Java (POJO)
                    .addConverterFactory(GsonConverterFactory.create())
                    // Menggunakan OkHttpClient yang sudah dikonfigurasi
                    .client(httpClient.build())
                    // Membangun objek Retrofit
                    .build();
        }
        return retrofit;
    }
}
