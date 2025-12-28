package vn.hcmute.utetrainingpointssystem;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.model.category.EventCategoryDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.EventCategoryApi;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SMOKE_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventCategoryApi api = RetrofitClient.getClient().create(EventCategoryApi.class);

        api.getAll().enqueue(new Callback<List<EventCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<EventCategoryDTO>> call, Response<List<EventCategoryDTO>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "HTTP " + response.code() + " " + response.message());
                    return;
                }

                List<EventCategoryDTO> data = response.body();
                int n = (data == null) ? 0 : data.size();

                Toast.makeText(MainActivity.this, "OK: categories=" + n, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "OK categories size=" + n);

                if (n > 0) Log.d(TAG, "First=" + data.get(0).name);
            }

            @Override
            public void onFailure(Call<List<EventCategoryDTO>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "FAIL: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "FAIL", t);
            }
        });
    }
}
