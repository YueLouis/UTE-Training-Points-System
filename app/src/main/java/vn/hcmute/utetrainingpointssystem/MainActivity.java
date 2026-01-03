package vn.hcmute.utetrainingpointssystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import vn.hcmute.utetrainingpointssystem.ui.event.AdminEventListActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, AdminEventListActivity.class));
        finish();
    }
}
