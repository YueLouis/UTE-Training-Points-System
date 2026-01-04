package vn.hcmute.utetrainingpointssystem.ui.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.category.EventCategoryDTO;
import vn.hcmute.utetrainingpointssystem.model.event.EventRequest;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventManageViewModel;

public class EventCreateActivity extends AppCompatActivity {

    private static final String TAG = "EventCreateValidate";

    private final TimeZone VN_TZ = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    private final TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");

    private EventManageViewModel vm;

    private EditText edtTitle, edtDesc, edtLocation, edtBannerUrl, edtSurveyUrl;
    private EditText edtStartTime, edtEndTime, edtDeadline, edtMaxParticipants, edtPointValue;

    private Spinner spCategory;
    private Button btnSave;

    private final List<EventCategoryDTO> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryNameAdapter;

    // ✅ SUBMIT lên BE: UTC Z
    private String isoStartUtcZ, isoEndUtcZ, isoDeadlineUtcZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        vm = new ViewModelProvider(this).get(EventManageViewModel.class);

        edtTitle = findViewById(R.id.edtTitle);
        edtDesc = findViewById(R.id.edtDesc);
        edtLocation = findViewById(R.id.edtLocation);
        edtBannerUrl = findViewById(R.id.edtBannerUrl);
        edtSurveyUrl = findViewById(R.id.edtSurveyUrl);

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

        // ✅ pickers: chọn giờ VN, hiển thị VN, submit UTC Z
        edtStartTime.setOnClickListener(v -> pickDateTimeUtcZ(isoZ -> {
            isoStartUtcZ = isoZ;
            if (TextUtils.isEmpty(isoDeadlineUtcZ)) {
                isoDeadlineUtcZ = isoStartUtcZ;
                edtDeadline.setText(displayVNFromUtcZ(isoDeadlineUtcZ));
            }
        }, edtStartTime));

        edtEndTime.setOnClickListener(v -> pickDateTimeUtcZ(isoZ -> {
            isoEndUtcZ = isoZ;
        }, edtEndTime));

        edtDeadline.setOnClickListener(v -> pickDateTimeUtcZ(isoZ -> {
            isoDeadlineUtcZ = isoZ;
        }, edtDeadline));

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
                reject(((ResultState.Error<?>) state).message);
            }
        });

        vm.getActionState().observe(this, state -> {
            if (state instanceof ResultState.Loading) {
                btnSave.setEnabled(false);
            } else if (state instanceof ResultState.Success) {
                btnSave.setEnabled(true);
                toast("Tạo event thành công");
                finish();
            } else if (state instanceof ResultState.Error) {
                btnSave.setEnabled(true);
                reject(((ResultState.Error<?>) state).message);
            }
        });
    }

    private void submitCreate() {
        String title = edtTitle.getText().toString().trim();
        if (title.isEmpty()) {
            reject("Tiêu đề không được rỗng");
            return;
        }
        if (categoryList.isEmpty()) {
            reject("Chưa có loại sự kiện (category)");
            return;
        }
        if (TextUtils.isEmpty(isoStartUtcZ) || TextUtils.isEmpty(isoEndUtcZ)) {
            reject("Vui lòng chọn thời gian bắt đầu và kết thúc");
            return;
        }
        if (TextUtils.isEmpty(isoDeadlineUtcZ)) isoDeadlineUtcZ = isoStartUtcZ;

        // ✅ validate logic time
        if (!validateTimesUtc(isoStartUtcZ, isoEndUtcZ, isoDeadlineUtcZ)) return;

        // ✅ URL optional validate (auto add https://)
        String banner = normalizeOptionalUrl(edtBannerUrl);
        if (banner == null) return;

        String survey = normalizeOptionalUrl(edtSurveyUrl);
        if (survey == null) return;

        Integer maxP = parseIntOrNull(edtMaxParticipants.getText().toString().trim(), "Số lượng tối đa");
        if (maxP == Integer.MIN_VALUE) return;

        Integer pointValue = parseIntOrNull(edtPointValue.getText().toString().trim(), "Điểm");
        if (pointValue == Integer.MIN_VALUE) return;

        int pos = spCategory.getSelectedItemPosition();
        Long categoryId = categoryList.get(pos).id;

        EventRequest req = new EventRequest();
        req.semesterId = 1L;
        req.categoryId = categoryId;

        req.title = title;
        req.description = edtDesc.getText().toString().trim();
        req.location = edtLocation.getText().toString().trim();
        req.bannerUrl = banner;

        // ✅ SUBMIT UTC Z
        req.startTime = isoStartUtcZ;
        req.endTime = isoEndUtcZ;
        req.registrationDeadline = isoDeadlineUtcZ;

        req.maxParticipants = maxP;
        req.pointTypeId = 1L;
        req.pointValue = (pointValue != null) ? pointValue : 0;

        req.createdBy = 1L;
        req.eventMode = "ATTENDANCE";
        req.surveyUrl = survey;

        Log.i(TAG, "SUBMIT: deadline=" + isoDeadlineUtcZ + " start=" + isoStartUtcZ + " end=" + isoEndUtcZ);
        vm.createEvent(req);
    }

    // ================= VALIDATE =================

    private boolean validateTimesUtc(String startZ, String endZ, String deadlineZ) {
        Date start = parseUtcZ(startZ);
        Date end = parseUtcZ(endZ);
        Date deadline = parseUtcZ(deadlineZ);

        if (start == null || end == null || deadline == null) {
            reject("Thời gian không hợp lệ (format sai)");
            return false;
        }

        if (deadline.after(start)) {
            reject("Hạn đăng ký phải trước (hoặc bằng) thời gian bắt đầu");
            return false;
        }

        if (!end.after(start)) {
            reject("Thời gian kết thúc phải sau thời gian bắt đầu");
            return false;
        }

        return true;
    }

    private Date parseUtcZ(String isoZ) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            in.setLenient(false);
            in.setTimeZone(UTC_TZ);
            return in.parse(isoZ);
        } catch (Exception e) {
            Log.e(TAG, "Parse UTCZ failed: " + isoZ, e);
            return null;
        }
    }

    // URL optional: rỗng => "", có => validate; thiếu scheme => auto add https://
    private String normalizeOptionalUrl(EditText edt) {
        String raw = edt.getText().toString().trim();
        if (TextUtils.isEmpty(raw)) return "";

        String url = raw;
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = "https://" + url;
            edt.setText(url);
        }

        try {
            URL u = new URL(url);
            if (TextUtils.isEmpty(u.getHost())) {
                reject("URL không hợp lệ: " + url);
                return null;
            }
            return url;
        } catch (Exception e) {
            reject("URL không hợp lệ: " + url);
            return null;
        }
    }

    // ================= PICKER =================

    private interface IsoZCallback { void onPicked(String isoUtcZ); }

    // Chọn VN -> hiển thị VN -> trả về UTC Z để submit
    private void pickDateTimeUtcZ(IsoZCallback cb, EditText target) {
        Calendar vnCal = Calendar.getInstance(VN_TZ);

        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> new TimePickerDialog(
                        this,
                        (timeView, hourOfDay, minute) -> {

                            vnCal.set(Calendar.YEAR, year);
                            vnCal.set(Calendar.MONTH, month);
                            vnCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            vnCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            vnCal.set(Calendar.MINUTE, minute);
                            vnCal.set(Calendar.SECOND, 0);
                            vnCal.set(Calendar.MILLISECOND, 0);

                            SimpleDateFormat display = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            display.setTimeZone(VN_TZ);
                            target.setText(display.format(vnCal.getTime()));

                            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                            iso.setTimeZone(UTC_TZ);
                            cb.onPicked(iso.format(vnCal.getTime()));
                        },
                        vnCal.get(Calendar.HOUR_OF_DAY),
                        vnCal.get(Calendar.MINUTE),
                        true
                ).show(),
                vnCal.get(Calendar.YEAR),
                vnCal.get(Calendar.MONTH),
                vnCal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private String displayVNFromUtcZ(String isoZ) {
        Date d = parseUtcZ(isoZ);
        if (d == null) return isoZ;

        SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        out.setTimeZone(VN_TZ);
        return out.format(d);
    }

    // ================= UTIL =================

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void reject(String msg) {
        Log.e(TAG, msg);
        toast(msg);
    }

    private Integer parseIntOrNull(String s, String field) {
        if (s.isEmpty()) return null;
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            reject(field + " không hợp lệ");
            return Integer.MIN_VALUE;
        }
    }
}
