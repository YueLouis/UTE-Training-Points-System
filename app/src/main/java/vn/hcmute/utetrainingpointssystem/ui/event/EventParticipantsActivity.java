package vn.hcmute.utetrainingpointssystem.ui.event;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationDTO;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventManageViewModel;

public class EventParticipantsActivity extends AppCompatActivity {

    private static final String TAG = "Participants";

    private long eventId;
    private long adminId; // admin đang đăng nhập
    private EventManageViewModel vm;

    private EditText edtStudentId;
    private ImageView btnClear;
    private TextView tvCount;
    private RecyclerView rv;

    private EventParticipantsAdapter adapter;
    private List<EventRegistrationDTO> all = new ArrayList<>();

    private SharedPreferences prefs;

    // ============= ADMIN GUARD =============
    private interface Action { void run(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_participants);

        eventId = getIntent().getLongExtra("eventId", -1);
        if (eventId == -1) { finish(); return; }

        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // 1) ưu tiên adminId từ Intent (nếu EventDetailActivity có truyền)
        adminId = getIntent().getLongExtra("adminId", 0L);

        // 2) fallback từ SharedPreferences
        if (adminId <= 0) adminId = prefs.getLong("userId", 0L);

        Log.d(TAG, "eventId=" + eventId + " adminId=" + adminId);

        edtStudentId = findViewById(R.id.edtStudentId);
        btnClear = findViewById(R.id.btnClear);
        tvCount = findViewById(R.id.tvCount);
        rv = findViewById(R.id.rvParticipants);

        vm = new ViewModelProvider(this).get(EventManageViewModel.class);

        adapter = new EventParticipantsAdapter(new EventParticipantsAdapter.Listener() {
            @Override
            public void onCheckIn(Long studentId) {
                ensureAdminIdThenRun(() -> {
                    Log.d(TAG, "checkin studentId=" + studentId + " adminId=" + adminId);
                    vm.checkIn(eventId, studentId, adminId);
                });
            }

            @Override
            public void onCheckOut(Long studentId) {
                ensureAdminIdThenRun(() -> {
                    Log.d(TAG, "checkout studentId=" + studentId + " adminId=" + adminId);
                    vm.checkOut(eventId, studentId, adminId);
                });
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Đừng khoá theo canCheckin/canCheckout (DTO BE có lúc trả false sai)
        adapter.setEventPermissions(true, true);

        observeVM();

        // Filter theo MSSV (studentCode) đúng với UI "Nhập MSSV để lọc"
        edtStudentId.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s == null ? "" : s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnClear.setOnClickListener(v -> edtStudentId.setText(""));

        // Load
        vm.loadEventById(eventId);
        vm.loadRegistrationsByEvent(eventId);

        // Nếu chưa có adminId thì hỏi ngay 1 lần
        if (adminId <= 0) {
            promptAdminId("Chưa có adminId. Nhập adminId để điểm danh/check-out:");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vm != null && eventId != -1) {
            vm.loadEventById(eventId);
            vm.loadRegistrationsByEvent(eventId);
        }
    }

    private void observeVM() {
        vm.getEventDetailState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                EventDTO e = ((ResultState.Success<EventDTO>) state).data;
                if (e != null) {
                    Log.d(TAG, "event computedStatus=" + e.computedStatus
                            + " canCheckin=" + e.canCheckin
                            + " canCheckout=" + e.canCheckout);
                }
            } else if (state instanceof ResultState.Error) {
                Log.e(TAG, "loadEvent error: " + ((ResultState.Error<?>) state).message);
            }
        });

        vm.getRegistrationsState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                List<EventRegistrationDTO> data =
                        ((ResultState.Success<List<EventRegistrationDTO>>) state).data;

                all = (data == null) ? new ArrayList<>() : data;
                applyFilter(edtStudentId.getText() == null ? "" : edtStudentId.getText().toString());

            } else if (state instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) state).message, Toast.LENGTH_SHORT).show();
            }
        });

        vm.getCheckInState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                Toast.makeText(this, "Check-in OK", Toast.LENGTH_SHORT).show();
                vm.loadRegistrationsByEvent(eventId);
            } else if (state instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) state).message, Toast.LENGTH_SHORT).show();
            }
        });

        vm.getCheckOutState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                Toast.makeText(this, "Check-out OK", Toast.LENGTH_SHORT).show();
                vm.loadRegistrationsByEvent(eventId);
            } else if (state instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) state).message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =========================
    // FILTER: theo MSSV (studentCode)
    // =========================
    private void applyFilter(String query) {
        String q = (query == null) ? "" : query.trim();

        if (q.isEmpty()) {
            adapter.setItems(all);
            tvCount.setText("Tổng tham gia: " + all.size() + " bạn");
            return;
        }

        List<EventRegistrationDTO> filtered = new ArrayList<>();
        for (EventRegistrationDTO it : all) {
            String code = (it.studentCode == null) ? "" : it.studentCode;
            if (code.contains(q)) filtered.add(it);
        }

        adapter.setItems(filtered);
        tvCount.setText(filtered.size() + " bạn (lọc)");
    }

    // =========================
    // ADMIN ID GUARD + POPUP
    // =========================
    private void ensureAdminIdThenRun(Action action) {
        if (adminId > 0) {
            action.run();
            return;
        }
        promptAdminId("Nhập adminId để thực hiện thao tác:", action);
    }

    private void promptAdminId(String message) {
        promptAdminId(message, null);
    }

    private void promptAdminId(String message, Action afterSet) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Ví dụ: 1");

        new AlertDialog.Builder(this)
                .setTitle("Cần adminId")
                .setMessage(message)
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Lưu", (d, w) -> {
                    String s = input.getText() == null ? "" : input.getText().toString().trim();
                    long val;
                    try { val = Long.parseLong(s); }
                    catch (Exception e) { val = 0; }

                    if (val <= 0) {
                        Toast.makeText(this, "adminId không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    adminId = val;
                    prefs.edit().putLong("userId", adminId).apply(); // lưu để lần sau khỏi nhập

                    Toast.makeText(this, "Đã lưu adminId=" + adminId, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "saved adminId=" + adminId);

                    if (afterSet != null) afterSet.run();
                })
                .setNegativeButton("Huỷ", (d, w) -> {
                    Toast.makeText(this, "Chưa có adminId nên không thao tác được", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
