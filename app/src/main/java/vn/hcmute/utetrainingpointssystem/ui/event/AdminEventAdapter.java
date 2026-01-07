package vn.hcmute.utetrainingpointssystem.ui.event;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;

public class AdminEventAdapter extends ListAdapter<EventDTO, AdminEventAdapter.VH> {

    public interface OnItemClick { void onClick(EventDTO e); }
    public interface OnEditClick { void onEdit(EventDTO e); }
    public interface OnDeleteClick { void onDelete(EventDTO e); }

    private final OnItemClick itemClick;
    private final OnEditClick editClick;
    private final OnDeleteClick deleteClick;

    public AdminEventAdapter(OnItemClick itemClick, OnEditClick editClick, OnDeleteClick deleteClick) {
        super(DIFF);
        this.itemClick = itemClick;
        this.editClick = editClick;
        this.deleteClick = deleteClick;
    }

    private static final DiffUtil.ItemCallback<EventDTO> DIFF = new DiffUtil.ItemCallback<EventDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull EventDTO oldItem, @NonNull EventDTO newItem) {
            return oldItem.id != null && oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull EventDTO oldItem, @NonNull EventDTO newItem) {
            String ot = safe(oldItem.title);
            String nt = safe(newItem.title);

            String os = statusBadge(oldItem);
            String ns = statusBadge(newItem);

            return ot.equals(nt)
                    && os.equals(ns)
                    && Objects.equals(oldItem.categoryId, newItem.categoryId);
        }

        private String safe(String s) { return s == null ? "" : s; }

        private String statusBadge(EventDTO e) {
            String computed = safe(e.computedStatus).trim();
            if (!computed.isEmpty()) return computed;
            return safe(e.status).trim();
        }
    };

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_event, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        EventDTO e = getItem(position);

        // ===== category -> tag + color =====
        int cardColor;
        String tagText;

        Long cid = e.categoryId; // đúng field của em

        if (cid != null && cid == 1L) {
            tagText = "DRL";
            cardColor = ContextCompat.getColor(h.itemView.getContext(), R.color.cat_prl);
        } else if (cid != null && cid == 2L) {
            tagText = "CTXH";
            cardColor = ContextCompat.getColor(h.itemView.getContext(), R.color.cat_ctxh);
        } else if (cid != null && cid == 3L) {
            tagText = "CDDN";
            cardColor = ContextCompat.getColor(h.itemView.getContext(), R.color.cat_cddn);
        } else {
            tagText = "N/A";
            cardColor = 0xFFEFEFEF;
        }

        h.tvTag.setText(tagText);

        // ===== nền item (root) =====
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(24f);
        bg.setColor(cardColor);
        h.root.setBackground(bg);

        // ===== nền box icon nhạt hơn =====
        int lightColor = ColorUtils.blendARGB(cardColor, Color.WHITE, 0.55f);

        Drawable boxBg = h.boxIcon.getBackground();
        if (boxBg != null) {
            boxBg = boxBg.mutate();
            boxBg.setTint(lightColor);
        }

        // ===== bind text =====
        h.txtTitle.setText(e.title == null ? "" : e.title);

        String computed = e.computedStatus == null ? "" : e.computedStatus.trim();
        String manual = e.status == null ? "" : e.status.trim();
        h.txtStatus.setText(!computed.isEmpty() ? computed : manual);

        // ===== clicks =====
        h.itemView.setOnClickListener(v -> { if (itemClick != null) itemClick.onClick(e); });
        h.btnEdit.setOnClickListener(v -> { if (editClick != null) editClick.onEdit(e); });
        h.btnDelete.setOnClickListener(v -> { if (deleteClick != null) deleteClick.onDelete(e); });
    }

    static class VH extends RecyclerView.ViewHolder {
        View root;
        LinearLayout boxIcon;
        TextView tvTag, txtTitle, txtStatus;
        ImageButton btnEdit, btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            boxIcon = itemView.findViewById(R.id.boxIcon);
            tvTag = itemView.findViewById(R.id.tvTag);

            txtTitle = itemView.findViewById(R.id.tvTitle);
            txtStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
