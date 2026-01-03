package vn.hcmute.utetrainingpointssystem.ui.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

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
            String ot = oldItem.title == null ? "" : oldItem.title;
            String nt = newItem.title == null ? "" : newItem.title;
            String os = oldItem.status == null ? "" : oldItem.status;
            String ns = newItem.status == null ? "" : newItem.status;
            return ot.equals(nt) && os.equals(ns);
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

        h.txtTitle.setText(e.title == null ? "" : e.title);
        h.txtStatus.setText(e.status == null ? "" : e.status);

        h.itemView.setOnClickListener(v -> { if (itemClick != null) itemClick.onClick(e); });
        h.btnEdit.setOnClickListener(v -> { if (editClick != null) editClick.onEdit(e); });
        h.btnDelete.setOnClickListener(v -> { if (deleteClick != null) deleteClick.onDelete(e); });
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtTitle, txtStatus;
        ImageButton btnEdit, btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
