package vn.hcmute.utetrainingpointssystem.ui.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

    private static final String TAG = "EventCreate";

    private final TimeZone VN_TZ = TimeZone.getTimeZone("GMT+07:00");
    private final TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");

    // default cho các field hệ thống (tuỳ BE của nhóm)
    private static final long DEFAULT_POINT_TYPE_ID = 1L;
    private static final long DEFAULT_CREATED_BY = 1L;

    // eventMode gửi BE
    private static final String MODE_ATTENDANCE = "ATTENDANCE"; // Trực tiếp
    private static final String MODE_ONLINE = "ONLINE";         // Online

    private EventManageViewModel vm;

    // header
    private ImageView btnBack;

    // UI (KHỚP XML)
    private TextView tvId;
    private Spinner spSemester, spCategory, spParticipationType;
    private EditText edtPoints;

    private EditText edtTitle, edtDescription;
    private EditText edtStartTime, edtEndTime, edtDeadline;
    private EditText edtMaxParticipants;

    private View groupLocation, groupSurvey;
    private EditText edtLocation;
    private EditText edtSurveyUrl, edtSurveySecretCode;

    private EditText edtBannerUrl;
    private Button btnSubmit;

    // category
    private final List<EventCategoryDTO> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryNameAdapter;

    // semester
    private final List<String> semesterDisplay = new ArrayList<>();
    private final List<Long> semesterIdList = new ArrayList<>();
    private ArrayAdapter<String> semesterAdapter;

    // participation type (mode)
    private final List<String> modeDisplay = new ArrayList<>();
    private final List<String> modeValue = new ArrayList<>();
    private ArrayAdapter<String> modeAdapter;

    // SUBMIT lên BE: UTC Z
    private String isoStartUtcZ, isoEndUtcZ, isoDeadlineUtcZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        // ===== bind views theo XML =====
        btnBack = findViewById(R.id.btnBack);


        spSemester = findViewById(R.id.spSemester);
        spCategory = findViewById(R.id.spCategory);
        edtPoints = findViewById(R.id.edtPoints);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);

        edtStartTime = findViewById(R.id.edtStartTime);
        edtEndTime = findViewById(R.id.edtEndTime);
        edtDeadline = findViewById(R.id.edtDeadline);

        spParticipationType = findViewById(R.id.spParticipationType);
        edtMaxParticipants = findViewById(R.id.edtMaxParticipants);

        groupLocation = findViewById(R.id.groupLocation);
        edtLocation = findViewById(R.id.edtLocation);

        groupSurvey = findViewById(R.id.groupSurvey);
        edtSurveyUrl = findViewById(R.id.edtSurveyUrl);
        edtSurveySecretCode = findViewById(R.id.edtSurveySecretCode);

        edtBannerUrl = findViewById(R.id.edtBannerUrl);

        btnSubmit = findViewById(R.id.btnSubmit);

        // nếu cái nào null => layout đang không khớp
        if (spSemester == null || spCategory == null || spParticipationType == null
                || edtStartTime == null || edtEndTime == null || edtDeadline == null
                || btnSubmit == null || groupLocation == null || groupSurvey == null) {
            Toast.makeText(this, "XML/ID không khớp. Kiểm tra activity_event_create.xml", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Missing view(s)");
            return;
        }

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // ID: create thì chưa có id => để dấu —
        if (tvId != null) tvId.setText("—");

        vm = new ViewModelProvider(this).get(EventManageViewModel.class);

        // ===== setup spinners =====
        setupSemesterSpinner();
        setupParticipationSpinner();
        setupCategorySpinner(); // adapter rỗng, data load sau

        // ===== time pickers =====
        edtStartTime.setOnClickListener(v -> pickDateTimeUtcZ(isoZ -> {
            isoStartUtcZ = isoZ;
            if (TextUtils.isEmpty(isoDeadlineUtcZ)) {
                isoDeadlineUtcZ = isoStartUtcZ;
                edtDeadline.setText(displayVNFromUtcAny(isoDeadlineUtcZ));
            }
        }, edtStartTime));

        edtEndTime.setOnClickListener(v -> pickDateTimeUtcZ(isoZ -> isoEndUtcZ = isoZ, edtEndTime));
        edtDeadline.setOnClickListener(v -> pickDateTimeUtcZ(isoZ -> isoDeadlineUtcZ = isoZ, edtDeadline));

        // ===== observe VM =====
        observeVM();
        vm.loadCategories();

        // set UI theo mode hiện tại
        applyModeUI(getSelectedModeValue());

        btnSubmit.setOnClickListener(v -> submitCreate());
    }

    // ================= UI TOGGLE =================

    private void applyModeUI(String mode) {
        if (MODE_ATTENDANCE.equals(mode)) {
            groupLocation.setVisibility(View.VISIBLE);
            groupSurvey.setVisibility(View.GONE);

            // clear survey để khỏi gửi nhầm
            if (edtSurveyUrl != null) edtSurveyUrl.setText("");
            if (edtSurveySecretCode != null) edtSurveySecretCode.setText("");
        } else {
            groupLocation.setVisibility(View.GONE);
            groupSurvey.setVisibility(View.VISIBLE);

            // clear location
            if (edtLocation != null) edtLocation.setText("");
        }
    }

    // ================= SPINNER SETUP =================

    private void setupSemesterSpinner() {
        semesterDisplay.clear();
        semesterIdList.clear();

        semesterDisplay.add("HK1");
        semesterIdList.add(1L);

        semesterDisplay.add("HK2");
        semesterIdList.add(2L);

        semesterDisplay.add("HK Hè");
        semesterIdList.add(3L);

        semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesterDisplay);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSemester.setAdapter(semesterAdapter);
        spSemester.setSelection(0);
    }

    private Long getSelectedSemesterId() {
        int pos = spSemester.getSelectedItemPosition();
        if (pos < 0 || pos >= semesterIdList.size()) return 1L;
        return semesterIdList.get(pos);
    }

    private void setupParticipationSpinner() {
        modeDisplay.clear();
        modeValue.clear();

        modeDisplay.add("Trực tiếp");
        modeValue.add(MODE_ATTENDANCE);

        modeDisplay.add("Online");
        modeValue.add(MODE_ONLINE);

        modeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modeDisplay);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spParticipationType.setAdapter(modeAdapter);

        spParticipationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyModeUI(getSelectedModeValue());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private String getSelectedModeValue() {
        int pos = spParticipationType.getSelectedItemPosition();
        if (pos < 0 || pos >= modeValue.size()) return MODE_ATTENDANCE;
        return modeValue.get(pos);
    }

    private void setupCategorySpinner() {
        categoryNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryNameAdapter);
    }

    // ================= VM OBSERVE =================

    private void observeVM() {
        vm.getCategoriesState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                List<EventCategoryDTO> data = ((ResultState.Success<List<EventCategoryDTO>>) state).data;

                categoryList.clear();
                categoryNameAdapter.clear();

                if (data != null) {
                    categoryList.addAll(data);
                    for (EventCategoryDTO c : data) {
                        categoryNameAdapter.add(c.name);
                    }
                }
                categoryNameAdapter.notifyDataSetChanged();

            } else if (state instanceof ResultState.Error) {
                reject(((ResultState.Error<?>) state).message);
            }
        });

        vm.getActionState().observe(this, state -> {
            if (state instanceof ResultState.Loading) {
                btnSubmit.setEnabled(false);
            } else if (state instanceof ResultState.Success) {
                btnSubmit.setEnabled(true);
                toast("Tạo event thành công");
                finish();
            } else if (state instanceof ResultState.Error) {
                btnSubmit.setEnabled(true);
                reject(((ResultState.Error<?>) state).message);
            }
        });
    }

    // ================= SUBMIT =================

    private void submitCreate() {
        String title = safeText(edtTitle);
        if (title.isEmpty()) { reject("Tiêu đề không được rỗng"); return; }

        if (categoryList.isEmpty()) { reject("Chưa có loại sự kiện (category)"); return; }

        if (TextUtils.isEmpty(isoStartUtcZ) || TextUtils.isEmpty(isoEndUtcZ)) {
            reject("Vui lòng chọn thời gian bắt đầu và kết thúc");
            return;
        }
        if (TextUtils.isEmpty(isoDeadlineUtcZ)) isoDeadlineUtcZ = isoStartUtcZ;

        if (!validateTimesUtc(isoStartUtcZ, isoEndUtcZ, isoDeadlineUtcZ)) return;

        String mode = getSelectedModeValue();

        // Trực tiếp: bắt buộc địa điểm
        if (MODE_ATTENDANCE.equals(mode)) {
            if (safeText(edtLocation).isEmpty()) {
                reject("Trực tiếp thì phải nhập địa điểm");
                return;
            }
        }

        // banner url optional
        String banner = normalizeOptionalUrl(edtBannerUrl);
        if (banner == null) return;

        Integer maxP = parseNonNegativeIntOrNull(safeText(edtMaxParticipants), "Số lượng tối đa");
        if (maxP == Integer.MIN_VALUE) return;

        Integer pointValue = parseNonNegativeIntOrNull(safeText(edtPoints), "Số điểm");
        if (pointValue == Integer.MIN_VALUE) return;

        int pos = spCategory.getSelectedItemPosition();
        if (pos < 0 || pos >= categoryList.size()) { reject("Chọn loại sự kiện không hợp lệ"); return; }
        Long categoryId = categoryList.get(pos).id;

        EventRequest req = new EventRequest();
        req.semesterId = getSelectedSemesterId();
        req.categoryId = categoryId;

        req.title = title;
        req.description = safeText(edtDescription);

        req.bannerUrl = banner;

        req.startTime = isoStartUtcZ;
        req.endTime = isoEndUtcZ;
        req.registrationDeadline = isoDeadlineUtcZ;

        req.maxParticipants = maxP;

        req.pointTypeId = DEFAULT_POINT_TYPE_ID;
        req.pointValue = (pointValue != null) ? pointValue : 0;

        req.createdBy = DEFAULT_CREATED_BY;
        req.eventMode = mode;

        if (MODE_ATTENDANCE.equals(mode)) {
            req.location = safeText(edtLocation);
            req.surveyUrl = "";
            req.surveySecretCode = "";
        } else {
            req.location = "";

            String survey = normalizeOptionalUrl(edtSurveyUrl);
            if (survey == null) return; // url sai
            req.surveyUrl = survey;

            req.surveySecretCode = safeText(edtSurveySecretCode);
        }

        Log.i(TAG, "SUBMIT mode=" + mode
                + " semesterId=" + req.semesterId
                + " categoryId=" + req.categoryId
                + " start=" + isoStartUtcZ + " end=" + isoEndUtcZ);

        vm.createEvent(req);
    }

    // ================= VALIDATE =================

    private boolean validateTimesUtc(String startZ, String endZ, String deadlineZ) {
        Date start = parseUtcZFlexible(startZ);
        Date end = parseUtcZFlexible(endZ);
        Date deadline = parseUtcZFlexible(deadlineZ);

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

    private Date parseUtcZFlexible(String isoZ) {
        if (isoZ == null) return null;
        isoZ = isoZ.trim();
        if (isoZ.isEmpty()) return null;

        String[] patterns = new String[] {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'"
        };

        for (String p : patterns) {
            try {
                SimpleDateFormat in = new SimpleDateFormat(p, Locale.US);
                in.setLenient(false);
                in.setTimeZone(UTC_TZ);
                Date d = in.parse(isoZ);
                if (d != null) return d;
            } catch (Exception ignore) {}
        }

        Log.e(TAG, "Parse UTCZ failed: " + isoZ);
        return null;
    }

    // ================= URL =================

    private String normalizeOptionalUrl(EditText edt) {
        if (edt == null) return "";
        String raw = safeText(edt);
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

                            Date picked = vnCal.getTime();

                            SimpleDateFormat display = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            display.setTimeZone(VN_TZ);
                            target.setText(display.format(picked));

                            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                            iso.setTimeZone(UTC_TZ);
                            cb.onPicked(iso.format(picked));
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

    private String displayVNFromUtcAny(String isoZ) {
        if (TextUtils.isEmpty(isoZ)) return "";
        Date d = parseUtcZFlexible(isoZ);
        if (d == null) return "";

        SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        out.setTimeZone(VN_TZ);
        return out.format(d);
    }

    // ================= UTIL =================

    private String safeText(EditText edt) {
        return (edt == null || edt.getText() == null) ? "" : edt.getText().toString().trim();
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void reject(String msg) {
        Log.e(TAG, msg);
        toast(msg);
    }

    private Integer parseNonNegativeIntOrNull(String s, String field) {
        if (s.isEmpty()) return null;
        try {
            int v = Integer.parseInt(s);
            if (v < 0) { reject(field + " phải >= 0"); return Integer.MIN_VALUE; }
            return v;
        } catch (Exception e) {
            reject(field + " không hợp lệ");
            return Integer.MIN_VALUE;
        }
    }
}
