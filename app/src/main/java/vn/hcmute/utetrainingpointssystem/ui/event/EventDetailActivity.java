package vn.hcmute.utetrainingpointssystem.ui.event;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

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
    private TextView tvSurvey;

    private EventDTO loaded;

    private final TimeZone VN_TZ = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    private final TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");

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
        tvSurvey = findViewById(R.id.tvSurvey);

        vm = new ViewModelProvider(this).get(EventManageViewModel.class);

        // participants
        Button btnParticipants = findViewById(R.id.btnParticipants);
        if (btnParticipants != null) {
            btnParticipants.setOnClickListener(v -> {

                // lấy adminId từ prefs (nếu có). Nếu chưa có vẫn ok, Participants sẽ popup nhập.
                SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                long adminId = prefs.getLong("userId", 0L);

                Intent i = new Intent(this, EventParticipantsActivity.class);
                i.putExtra("eventId", eventId);
                i.putExtra("adminId", adminId);
                startActivity(i);
            });
        }

        vm.getEventDetailState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                loaded = ((ResultState.Success<EventDTO>) state).data;
                if (loaded == null) return;

                tvTitle.setText(nz(loaded.title));

                String clientStatus = computeClientStatus(loaded);
                tvStatus.setText(clientStatus);

                String start = formatToVN(nz(loaded.startTime));
                String end = formatToVN(nz(loaded.endTime));
                tvTime.setText(start + "  →  " + end);

                tvLocation.setText(nz(loaded.location));
                tvDesc.setText(nz(loaded.description));

                if (imgBanner != null) {
                    Glide.with(this).load(loaded.bannerUrl).into(imgBanner);
                }

                // survey
                String sUrl = (loaded.surveyUrl == null) ? "" : loaded.surveyUrl.trim();
                if (tvSurvey != null) {
                    if (TextUtils.isEmpty(sUrl)) {
                        tvSurvey.setVisibility(View.GONE);
                    } else {
                        tvSurvey.setVisibility(View.VISIBLE);
                        tvSurvey.setText("Survey: " + sUrl);
                        Linkify.addLinks(tvSurvey, Linkify.WEB_URLS);
                    }
                }

                vm.loadRegistrationCount(eventId);

            } else if (state instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) state).message, Toast.LENGTH_SHORT).show();
            }
        });

        vm.getRegistrationCountState().observe(this, state -> {
            if (tvRegisterCount == null) return;

            if (state instanceof ResultState.Success) {
                Integer count = ((ResultState.Success<Integer>) state).data;
                int c = (count == null) ? 0 : count;

                Integer max = (loaded == null) ? null : loaded.maxParticipants;
                String maxText = (max == null) ? "?" : String.valueOf(max);

                tvRegisterCount.setText("Đã đăng ký: " + c + " / " + maxText);
            } else {
                String maxText = (loaded != null && loaded.maxParticipants != null)
                        ? String.valueOf(loaded.maxParticipants)
                        : "?";
                tvRegisterCount.setText("Đã đăng ký: ? / " + maxText);
            }
        });

        vm.loadEventById(eventId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vm != null && eventId != -1) vm.loadEventById(eventId);
    }

    private String nz(String s) { return s == null ? "" : s; }

    private String computeClientStatus(EventDTO e) {
        Date start = parseIsoApiSmart(e.startTime);
        Date end = parseIsoApiSmart(e.endTime);
        Date deadline = parseIsoApiSmart(e.registrationDeadline);

        long now = System.currentTimeMillis();

        if (end != null && now >= end.getTime()) return "ENDED";
        if (start != null && end != null && now >= start.getTime() && now < end.getTime()) return "ONGOING";

        if (deadline != null && now <= deadline.getTime()) return "OPEN_FOR_REGISTRATION";
        return "CLOSED";
    }

    private String formatToVN(String iso) {
        Date d = parseIsoApiSmart(iso);
        if (d == null) return iso;

        SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        out.setTimeZone(VN_TZ);
        return out.format(d);
    }

    private Date parseIsoApiSmart(String iso) {
        if (iso == null) return null;
        iso = iso.trim();
        if (iso.isEmpty()) return null;

        String[] patterns = new String[] {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss"
        };

        for (String p : patterns) {
            try {
                SimpleDateFormat in = new SimpleDateFormat(p, Locale.US);
                in.setLenient(false);
                in.setTimeZone(UTC_TZ);
                Date d = in.parse(iso);
                if (d != null) return d;
            } catch (Exception ignore) {}
        }
        return null;
    }
}
