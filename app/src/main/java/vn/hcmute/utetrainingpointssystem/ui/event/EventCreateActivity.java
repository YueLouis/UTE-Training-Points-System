package vn.hcmute.utetrainingpointssystem.ui.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.category.EventCategoryDTO;
import vn.hcmute.utetrainingpointssystem.model.event.EventRequest;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventManageViewModel;

public class EventCreateActivity extends AppCompatActivity {

    private EventManageViewModel vm;

    private EditText edtTitle, edtDesc, edtLocation, edtBannerUrl;
    private EditText edtStartTime, edtEndTime, edtDeadline, edtMaxParticipants, edtPointValue;

    private String isoStart, isoEnd, isoDeadline;

    private Spinner spCategory;
    private Button btnSave;

    private final List<EventCategoryDTO> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryNameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        vm = new ViewModelProvider(this).get(EventManageViewModel.class);

        edtTitle = findViewById(R.id.edtTitle);
        edtDesc = findViewById(R.id.edtDesc);
        edtLocation = findViewById(R.id.edtLocation);
        edtBannerUrl = findViewById(R.id.edtBannerUrl);

        spCategory = findViewById(R.id.spCategory);

        edtStartTime = findViewById(R.id.edtStartTime);
        edtEndTime = findViewById(R.id.edtEndTime);
        edtDeadline = findViewById(R.id.edtDeadline);

        edtMaxParticipants = findViewById(R.id.edtMaxParticipants);
        edtPointValue = findViewById(R.id.edtPointValue);

        btnSave = findViewById(R.id.btnSave);

        categoryNameAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryNameAdapter);

        // picker time
        edtStartTime.setOnClickListener(v -> pickDateTime(iso -> { isoStart = iso; edtStartTime.setText(iso); }));
        edtEndTime.setOnClickListener(v -> pickDateTime(iso -> { isoEnd = iso; edtEndTime.setText(iso); }));
        edtDeadline.setOnClickListener(v -> pickDateTime(iso -> { isoDeadline = iso; edtDeadline.setText(iso); }));

        observeVM();
        vm.loadCategories();

        btnSave.setOnClickListener(v -> submitCreate());
    }

    private void observeVM() {
        vm.getCategoriesState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                List<EventCategoryDTO> data = ((ResultState.Success<List<EventCategoryDTO>>) state).data;
                categoryList.clear();
                categoryNameAdapter.clear();

                if (data != null) {
                    categoryList.addAll(data);
                    for (EventCategoryDTO c : data) categoryNameAdapter.add(c.name);
                }
                categoryNameAdapter.notifyDataSetChanged();
            } else if (state instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) state).message, Toast.LENGTH_SHORT).show();
            }
        });

        vm.getActionState().observe(this, state -> {
            if (state instanceof ResultState.Loading) {
                btnSave.setEnabled(false);
            } else if (state instanceof ResultState.Success) {
                btnSave.setEnabled(true);
                Toast.makeText(this, "Tạo event thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else if (state instanceof ResultState.Error) {
                btnSave.setEnabled(true);
                Toast.makeText(this, ((ResultState.Error<?>) state).message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitCreate() {
        String title = edtTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Title không được rỗng", Toast.LENGTH_SHORT).show();
            return;
        }
        if (categoryList.isEmpty()) {
            Toast.makeText(this, "Chưa có category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isoStart == null || isoEnd == null) {
            Toast.makeText(this, "Chọn Start time và End time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isoDeadline == null) isoDeadline = isoStart;

        Integer maxP = parseIntOrNull(edtMaxParticipants.getText().toString().trim(), "Max participants");
        if (maxP == Integer.MIN_VALUE) return;

        Integer pointValue = parseIntOrNull(edtPointValue.getText().toString().trim(), "Point value");
        if (pointValue == Integer.MIN_VALUE) return;

        int pos = spCategory.getSelectedItemPosition();
        Long categoryId = categoryList.get(pos).id;

        EventRequest req = new EventRequest();
        req.semesterId = 1L;
        req.categoryId = categoryId;

        req.title = title;
        req.description = edtDesc.getText().toString().trim();
        req.location = edtLocation.getText().toString().trim();
        req.bannerUrl = edtBannerUrl.getText().toString().trim();

        req.startTime = isoStart;
        req.endTime = isoEnd;
        req.registrationDeadline = isoDeadline;

        req.maxParticipants = maxP;
        req.pointTypeId = 1L;
        req.pointValue = (pointValue != null) ? pointValue : 0;

        req.createdBy = 1L;
        req.eventMode = "ATTENDANCE";
        req.surveyUrl = "";

        vm.createEvent(req);
    }

    private Integer parseIntOrNull(String s, String field) {
        if (s.isEmpty()) return null;
        try { return Integer.parseInt(s); }
        catch (Exception e) {
            Toast.makeText(this, field + " không hợp lệ", Toast.LENGTH_SHORT).show();
            return Integer.MIN_VALUE;
        }
    }

    private interface IsoCallback { void onPicked(String iso); }

    private void pickDateTime(IsoCallback cb) {
        Calendar cal = Calendar.getInstance();

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    TimePickerDialog tp = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                cal.set(year, month, dayOfMonth, hourOfDay, minute, 0);

                                SimpleDateFormat sdf =
                                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                                cb.onPicked(sdf.format(cal.getTime()));
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                    );
                    tp.show();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dp.show();
    }
}
