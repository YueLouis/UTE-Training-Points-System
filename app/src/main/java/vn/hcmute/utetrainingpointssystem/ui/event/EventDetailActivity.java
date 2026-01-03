package vn.hcmute.utetrainingpointssystem.ui.event;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventManageViewModel;

public class EventDetailActivity extends AppCompatActivity {

    private EventManageViewModel vm;
    private long eventId;

    private ImageView imgBanner;
    private TextView tvTitle, tvStatus, tvTime, tvLocation, tvDesc, tvRegisterCount;

    private EventDTO loaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventId = getIntent().getLongExtra("eventId", -1);
        if (eventId == -1) { finish(); return; }

        imgBanner = findViewById(R.id.imgBanner);
        tvTitle = findViewById(R.id.tvTitle);
        tvStatus = findViewById(R.id.tvStatus);
        tvTime = findViewById(R.id.tvTime);
        tvLocation = findViewById(R.id.tvLocation);
        tvDesc = findViewById(R.id.tvDesc);
        tvRegisterCount = findViewById(R.id.tvRegisterCount);

        vm = new ViewModelProvider(this).get(EventManageViewModel.class);

        vm.getEventDetailState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                loaded = ((ResultState.Success<EventDTO>) state).data;
                if (loaded == null) return;

                tvTitle.setText(nz(loaded.title));
                tvStatus.setText(nz(loaded.status));

                String start = formatIsoToLocal(nz(loaded.startTime));
                String end = formatIsoToLocal(nz(loaded.endTime));
                tvTime.setText(start + "  →  " + end);

                tvLocation.setText(nz(loaded.location));
                tvDesc.setText(nz(loaded.description));

                // Banner: nếu em đã có Glide thì dùng Glide, không thì tạm để trống
                // Glide.with(this).load(loaded.bannerUrl).into(imgBanner);

                // gọi đếm đăng ký
                vm.loadRegistrationCount(eventId);

            } else if (state instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) state).message, Toast.LENGTH_SHORT).show();
            }
        });

        vm.getRegistrationCountState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                Integer count = ((ResultState.Success<Integer>) state).data;
                int c = (count == null) ? 0 : count;

                Integer max = (loaded == null) ? null : loaded.maxParticipants;
                String maxText = (max == null) ? "?" : String.valueOf(max);

                tvRegisterCount.setText("Đã đăng ký: " + c + " / " + maxText);

            } else if (state instanceof ResultState.Error) {
                // Nếu endpoint registration đang sai -> sẽ vào đây
                tvRegisterCount.setText("Đã đăng ký: ? / " + ((loaded != null && loaded.maxParticipants != null) ? loaded.maxParticipants : "?"));
            }
        });

        vm.loadEventById(eventId);
    }

    private String nz(String s) { return s == null ? "" : s; }

    // ISO "2026-01-03T14:49:41.773Z" -> dd/MM/yyyy HH:mm (giờ VN)
    private String formatIsoToLocal(String iso) {
        if (iso == null || iso.isEmpty()) return "";
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            in.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = in.parse(iso);

            SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            // out default dùng timezone máy (VN) -> ok
            return (d == null) ? iso : out.format(d);
        } catch (ParseException e) {
            return iso;
        }
    }
}
