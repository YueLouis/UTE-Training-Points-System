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
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.model.event.EventRequest;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventManageViewModel;

public class EventEditActivity extends AppCompatActivity {

    private static final String TAG = "EventEdit";

    private final TimeZone VN_TZ = TimeZone.getTimeZone("GMT+07:00");
    private final TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");

    private static final long DEFAULT_POINT_TYPE_ID = 1L;
    private static final long DEFAULT_CREATED_BY = 1L;

    private static final String MODE_ATTENDANCE = "ATTENDANCE"; // Trực tiếp
    private static final String MODE_ONLINE = "ONLINE";         // Online

    private EventManageViewModel vm;

    private long eventId;
    private EventDTO loaded;

    // header
    private ImageView btnBack;

    // UI (KHỚP XML của em)
    private Spinner spSemester, spCategory, spEventMode;
    private EditText edtPointValue;

    private EditText edtTitle, edtDesc;
    private EditText edtStartTime, edtEndTime, edtDeadline;
    private EditText edtMaxParticipants;

    private View layoutStartTime, layoutEndTime;
    private ImageView ivStartTime, ivEndTime;

    private View groupLocation, groupSurvey;
    private EditText edtLocation;
    private EditText edtSurveyUrl, edtSurveySecretCode;

    private EditText edtBannerUrl;
    private Button btnSave;

    // category
    private final List<EventCategoryDTO> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryNameAdapter;

    // semester
    private final List<String> semesterDisplay = new ArrayList<>();
    private final List<Long> semesterIdList = new ArrayList<>();
    private ArrayAdapter<String> semesterAdapter;

    // mode (eventMode)
    private final List<String> modeDisplay = new ArrayList<>();
    private final List<String> modeValue = new ArrayList<>();
    private ArrayAdapter<String> modeAdapter;

    // submit time: UTC Z
    private String isoStartUtcZ, isoEndUtcZ, isoDeadlineUtcZ;

    private boolean suppressModeCallback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        eventId = getIntent().getLongExtra("eventId", -1);
        if (eventId == -1) { finish(); return; }

        vm = new ViewModelProvider(this).get(EventManageViewModel.class);

        // ===== bind views =====
        btnBack = findViewById(R.id.btnBack);

        spSemester = findViewById(R.id.spSemester);
        spCategory = findViewById(R.id.spCategory);
        spEventMode = findViewById(R.id.spEventMode);

        edtPointValue = findViewById(R.id.edtPointValue);

        edtTitle = findViewById(R.id.edtTitle);
        edtDesc = findViewById(R.id.edtDesc);

        layoutStartTime = findViewById(R.id.layoutStartTime);
        layoutEndTime = findViewById(R.id.layoutEndTime);
        ivStartTime = findViewById(R.id.ivStartTime);
        ivEndTime = findViewById(R.id.ivEndTime);

        edtStartTime = findViewById(R.id.edtStartTime);
        edtEndTime = findViewById(R.id.edtEndTime);
        edtDeadline = findViewById(R.id.edtDeadline);

        edtMaxParticipants = findViewById(R.id.edtMaxParticipants);

        groupLocation = findViewById(R.id.groupLocation);
        groupSurvey = findViewById(R.id.groupSurvey);
        edtLocation = findViewById(R.id.edtLocation);

        edtSurveyUrl = findViewById(R.id.edtSurveyUrl);
        edtSurveySecretCode = findViewById(R.id.edtSurveySecretCode);

        edtBannerUrl = findViewById(R.id.edtBannerUrl);

        btnSave = findViewById(R.id.btnSave);

        if (spSemester == null || spCategory == null || spEventMode == null
                || edtTitle == null || edtDesc == null
                || edtStartTime == null || edtEndTime == null || edtDeadline == null
                || groupLocation == null || groupSurvey == null
                || btnSave == null) {
            Toast.makeText(this, "XML/ID không khớp. Kiểm tra activity_event_edit.xml", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Missing view(s) - check IDs");
            return;
        }

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // ===== setup spinners =====
        setupSemesterSpinner();
        setupEventModeSpinner();
        setupCategorySpinner();

        // ===== time pickers: click vào edt + layout + icon =====
        View.OnClickListener pickStart = v -> pickDateTimeUtcZ(isoZ -> {
            isoStartUtcZ = isoZ;
            if (TextUtils.isEmpty(isoDeadlineUtcZ)) {
                isoDeadlineUtcZ = isoStartUtcZ;
                edtDeadline.setText(displayVNFromUtcAny(isoDeadlineUtcZ));
            }
        }, edtStartTime);

        View.OnClickListener pickEnd = v -> pickDateTimeUtcZ(isoZ -> isoEndUtcZ = isoZ, edtEndTime);

        edtStartTime.setOnClickListener(pickStart);
        if (layoutStartTime != null) layoutStartTime.setOnClickListener(pickStart);
        if (ivStartTime != null) ivStartTime.setOnClickListener(pickStart);

        edtEndTime.setOnClickListener(pickEnd);
        if (layoutEndTime != null) layoutEndTime.setOnClickListener(pickEnd);
        if (ivEndTime != null) ivEndTime.setOnClickListener(pickEnd);

        edtDeadline.setOnClickListener(v ->
                pickDateTimeUtcZ(isoZ -> isoDeadlineUtcZ = isoZ, edtDeadline)
        );

        // ===== observe VM =====
        observeVM();

        // load data
        vm.loadCategories();
        vm.loadEventById(eventId);

        // default UI
        applyModeUI(getSelectedModeValue(), false);

        btnSave.setOnClickListener(v -> submitUpdate());
    }

    // ================= UI TOGGLE =================

    private void applyModeUI(String mode, boolean clearHiddenFields) {
        if (MODE_ATTENDANCE.equals(mode)) {
            groupLocation.setVisibility(View.VISIBLE);
            groupSurvey.setVisibility(View.GONE);

            if (clearHiddenFields) {
                if (edtSurveyUrl != null) edtSurveyUrl.setText("");
                if (edtSurveySecretCode != null) edtSurveySecretCode.setText("");
            }
        } else {
            groupLocation.setVisibility(View.GONE);
            groupSurvey.setVisibility(View.VISIBLE);

            if (clearHiddenFields) {
                if (edtLocation != null) edtLocation.setText("");
            }
        }
    }

    // ================= SPINNER SETUP =================

    private void setupSemesterSpinner() {
        semesterDisplay.clear();
        semesterIdList.clear();

        semesterDisplay.add("HK1"); semesterIdList.add(1L);
        semesterDisplay.add("HK2"); semesterIdList.add(2L);
        semesterDisplay.add("HK Hè"); semesterIdList.add(3L);

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

    private void setSemesterSelectionById(Long semesterId) {
        if (semesterId == null) return;
        for (int i = 0; i < semesterIdList.size(); i++) {
            if (semesterId.equals(semesterIdList.get(i))) {
                spSemester.setSelection(i);
                return;
            }
        }
    }

    private void setupEventModeSpinner() {
        modeDisplay.clear();
        modeValue.clear();

        modeDisplay.add("Trực tiếp");
        modeValue.add(MODE_ATTENDANCE);

        modeDisplay.add("Online");
        modeValue.add(MODE_ONLINE);

        modeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modeDisplay);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEventMode.setAdapter(modeAdapter);

        spEventMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (suppressModeCallback) return;
                applyModeUI(getSelectedModeValue(), true);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private String getSelectedModeValue() {
        int pos = spEventMode.getSelectedItemPosition();
        if (pos < 0 || pos >= modeValue.size()) return MODE_ATTENDANCE;
        return modeValue.get(pos);
    }

    private void setModeSelection(String mode) {
        for (int i = 0; i < modeValue.size(); i++) {
            if (modeValue.get(i).equals(mode)) {
                spEventMode.setSelection(i);
                return;
            }
        }
        spEventMode.setSelection(0);
    }

    private void setupCategorySpinner() {
        categoryNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        categoryNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryNameAdapter);
    }

    private void syncCategoryById(Long categoryId) {
        if (categoryId == null) return;
        if (categoryList.isEmpty()) return;

        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).id != null && categoryId.equals(categoryList.get(i).id)) {
                spCategory.setSelection(i);
                return;
            }
        }
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
                    for (EventCategoryDTO c : data) categoryNameAdapter.add(c.name);
                }
                categoryNameAdapter.notifyDataSetChanged();

                if (loaded != null) syncCategoryById(loaded.categoryId);

            } else if (state instanceof ResultState.Error) {
                reject(((ResultState.Error<?>) state).message);
            }
        });

        vm.getEventDetailState().observe(this, state -> {
            if (state instanceof ResultState.Success) {
                loaded = ((ResultState.Success<EventDTO>) state).data;
                if (loaded == null) return;

                edtTitle.setText(nz(loaded.title));
                edtDesc.setText(nz(loaded.description));
                edtBannerUrl.setText(nz(loaded.bannerUrl));

                edtMaxParticipants.setText(loaded.maxParticipants == null ? "" : String.valueOf(loaded.maxParticipants));
                edtPointValue.setText(loaded.pointValue == null ? "" : String.valueOf(loaded.pointValue));

                // semester
                setSemesterSelectionById(loaded.semesterId);

                // category
                syncCategoryById(loaded.categoryId);

                // time
                isoStartUtcZ = toUtcZ(loaded.startTime);
                isoEndUtcZ = toUtcZ(loaded.endTime);
                isoDeadlineUtcZ = toUtcZ(loaded.registrationDeadline);

                edtStartTime.setText(displayVNFromUtcAny(isoStartUtcZ));
                edtEndTime.setText(displayVNFromUtcAny(isoEndUtcZ));
                edtDeadline.setText(displayVNFromUtcAny(isoDeadlineUtcZ));

                // mode
                String mode = (loaded.eventMode == null || loaded.eventMode.trim().isEmpty())
                        ? MODE_ATTENDANCE
                        : loaded.eventMode.trim().toUpperCase(Locale.US);

                suppressModeCallback = true;
                setModeSelection(mode);
                suppressModeCallback = false;

                applyModeUI(mode, false);

                if (MODE_ONLINE.equals(mode)) {
                    if (edtLocation != null) edtLocation.setText("");
                    if (edtSurveyUrl != null) edtSurveyUrl.setText(nz(loaded.surveyUrl));
                    if (edtSurveySecretCode != null) edtSurveySecretCode.setText(nz(loaded.surveySecretCode));
                } else {
                    if (edtLocation != null) edtLocation.setText(nz(loaded.location));
                    if (edtSurveyUrl != null) edtSurveyUrl.setText("");
                    if (edtSurveySecretCode != null) edtSurveySecretCode.setText("");
                }

            } else if (state instanceof ResultState.Error) {
                reject(((ResultState.Error<?>) state).message);
            }
        });

        vm.getActionState().observe(this, state -> {
            if (state instanceof ResultState.Loading) {
                btnSave.setEnabled(false);
            } else if (state instanceof ResultState.Success) {
                btnSave.setEnabled(true);
                toast("Cập nhật event thành công");
                finish();
            } else if (state instanceof ResultState.Error) {
                btnSave.setEnabled(true);
                reject(((ResultState.Error<?>) state).message);
            }
        });
    }

    // ================= SUBMIT UPDATE =================

    private void submitUpdate() {
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

        if (MODE_ATTENDANCE.equals(mode)) {
            if (safeText(edtLocation).isEmpty()) {
                reject("Trực tiếp thì phải nhập địa điểm");
                return;
            }
        }

        String banner = normalizeOptionalUrl(edtBannerUrl);
        if (banner == null) return;

        Integer maxP = parseNonNegativeIntOrNull(safeText(edtMaxParticipants), "Số lượng tối đa");
        if (maxP == Integer.MIN_VALUE) return;

        Integer pointValue = parseNonNegativeIntOrNull(safeText(edtPointValue), "Điểm");
        if (pointValue == Integer.MIN_VALUE) return;

        int pos = spCategory.getSelectedItemPosition();
        if (pos < 0 || pos >= categoryList.size()) { reject("Chọn loại sự kiện không hợp lệ"); return; }
        Long categoryId = categoryList.get(pos).id;

        EventRequest req = new EventRequest();
        req.semesterId = getSelectedSemesterId();
        req.categoryId = categoryId;

        req.title = title;
        req.description = safeText(edtDesc);

        req.bannerUrl = banner;

        req.startTime = isoStartUtcZ;
        req.endTime = isoEndUtcZ;
        req.registrationDeadline = isoDeadlineUtcZ;

        req.maxParticipants = maxP;

        req.pointTypeId = (loaded != null && loaded.pointTypeId != null) ? loaded.pointTypeId : DEFAULT_POINT_TYPE_ID;
        req.pointValue = (pointValue != null) ? pointValue : 0;
        req.createdBy = (loaded != null && loaded.createdBy != null) ? loaded.createdBy : DEFAULT_CREATED_BY;

        req.eventMode = mode;

        if (MODE_ATTENDANCE.equals(mode)) {
            req.location = safeText(edtLocation);
            req.surveyUrl = "";
            req.surveySecretCode = "";
        } else {
            req.location = "";

            String survey = normalizeOptionalUrl(edtSurveyUrl);
            if (survey == null) return;
            req.surveyUrl = survey;

            req.surveySecretCode = safeText(edtSurveySecretCode);
        }

        Log.i(TAG, "UPDATE eventId=" + eventId
                + " mode=" + mode
                + " semesterId=" + req.semesterId
                + " categoryId=" + req.categoryId);

        vm.updateEvent(eventId, req);
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

    // ================= BE time -> UTCZ =================

    private String toUtcZ(String isoAny) {
        Date d = parseIsoSmartAsServerTime(isoAny);
        if (d == null) return "";

        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        out.setTimeZone(UTC_TZ);
        return out.format(d);
    }

    private Date parseIsoSmartAsServerTime(String iso) {
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

    private String nz(String s) { return s == null ? "" : s; }

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
