package vn.hcmute.utetrainingpointssystem.ui.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.model.registration.EventRegistrationDTO;

public class EventParticipantsAdapter extends RecyclerView.Adapter<EventParticipantsAdapter.VH> {

    public interface Listener {
        void onCheckIn(Long studentId);
        void onCheckOut(Long studentId);
    }

    private final Listener listener;
    private final List<EventRegistrationDTO> items = new ArrayList<>();

    private boolean eventCanCheckin = true;
    private boolean eventCanCheckout = true;

    private final TimeZone VN_TZ = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    private final TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");

    public EventParticipantsAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<EventRegistrationDTO> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    public void setEventPermissions(boolean canCheckin, boolean canCheckout) {
        this.eventCanCheckin = canCheckin;
        this.eventCanCheckout = canCheckout;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_participant, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        EventRegistrationDTO r = items.get(position);

        Long studentId = (r == null) ? null : r.studentId;

        // MSSV
        String mssv = "-";
        if (r != null) {
            if (r.studentCode != null && !r.studentCode.trim().isEmpty()) {
                mssv = r.studentCode.trim();
            } else if (studentId != null) {
                mssv = String.valueOf(studentId);
            }
        }
        h.tvStudent.setText("MSSV: " + mssv);

        // Tên
        String name = (r == null || r.studentName == null) ? "" : r.studentName.trim();
        if (name.isEmpty()) name = "-";
        h.tvName.setText("Họ tên: " + name);

        String cls = (r == null || r.studentClass == null) ? "" : r.studentClass.trim();
        String fac = (r == null || r.studentFaculty == null) ? "" : r.studentFaculty.trim();

        String meta = "";
        if (!cls.isEmpty()) meta += "Lớp: " + cls;
        if (!fac.isEmpty()) meta += (meta.isEmpty() ? "" : " | ") + "Khoa: " + fac;

        if (h.tvMeta != null) {
            if (meta.isEmpty()) {
                h.tvMeta.setVisibility(View.GONE);
            } else {
                h.tvMeta.setVisibility(View.VISIBLE);
                h.tvMeta.setText(meta);
            }
        }

        String inIso = (r == null) ? null : r.checkinTime;
        String outIso = (r == null) ? null : r.checkoutTime;

        boolean checkedIn = inIso != null && !inIso.trim().isEmpty();
        boolean checkedOut = outIso != null && !outIso.trim().isEmpty();

        h.tvStatus.setText(computeStatus(r, checkedIn, checkedOut));

        String inText = checkedIn ? formatToVN(inIso) : "-";
        String outText = checkedOut ? formatToVN(outIso) : "-";
        h.tvTimes.setText("In: " + inText + " | Out: " + outText);

        boolean hasStudentId = (studentId != null && studentId > 0);

        boolean canCheckInBtn = hasStudentId && eventCanCheckin && !checkedIn;
        boolean canCheckOutBtn = hasStudentId && eventCanCheckout && checkedIn && !checkedOut;

        h.btnCheckIn.setEnabled(canCheckInBtn);
        h.btnCheckOut.setEnabled(canCheckOutBtn);

        h.btnCheckIn.setAlpha(canCheckInBtn ? 1f : 0.35f);
        h.btnCheckOut.setAlpha(canCheckOutBtn ? 1f : 0.35f);

        h.btnCheckIn.setOnClickListener(v -> {
            if (!canCheckInBtn) return;
            if (listener != null) listener.onCheckIn(studentId);
        });

        h.btnCheckOut.setOnClickListener(v -> {
            if (!canCheckOutBtn) return;
            if (listener != null) listener.onCheckOut(studentId);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String computeStatus(EventRegistrationDTO r, boolean checkedIn, boolean checkedOut) {
        if (checkedOut) return "COMPLETED";
        if (checkedIn) return "CHECKED_IN";

        String s = (r == null || r.status == null) ? "" : r.status.trim().toUpperCase(Locale.US);
        if (!s.isEmpty()) return s;

        return "REGISTERED";
    }

    private String formatToVN(String iso) {
        Date d = parseIsoSmart(iso);
        if (d == null) return iso;

        SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        out.setTimeZone(VN_TZ);
        return out.format(d);
    }

    private Date parseIsoSmart(String iso) {
        if (iso == null) return null;
        iso = iso.trim();
        if (iso.isEmpty()) return null;

        String[] patterns = new String[]{
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

    static class VH extends RecyclerView.ViewHolder {
        TextView tvStudent, tvName, tvMeta, tvStatus, tvTimes;
        Button btnCheckIn, btnCheckOut;

        VH(@NonNull View itemView) {
            super(itemView);
            tvStudent = itemView.findViewById(R.id.tvStudent);
            tvName = itemView.findViewById(R.id.tvName);


            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTimes = itemView.findViewById(R.id.tvTimes);
            btnCheckIn = itemView.findViewById(R.id.btnCheckIn);
            btnCheckOut = itemView.findViewById(R.id.btnCheckOut);
        }
    }
}
