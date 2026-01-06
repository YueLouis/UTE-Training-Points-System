package vn.hcmute.utetrainingpointssystem;

import android.app.Application;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.init(this);
    }
}
