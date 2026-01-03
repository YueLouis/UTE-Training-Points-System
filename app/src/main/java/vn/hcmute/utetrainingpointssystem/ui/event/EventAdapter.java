package vn.hcmute.utetrainingpointssystem.ui.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;

public class EventAdapter extends ListAdapter<EventDTO, EventAdapter.VH> {

    public EventAdapter() {
        super(DIFF);
    }

    private static final DiffUtil.ItemCallback<EventDTO> DIFF = new DiffUtil.ItemCallback<EventDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull EventDTO oldItem, @NonNull EventDTO newItem) {
            Long oid = oldItem.id;
            Long nid = newItem.id;
            if (oid == null || nid == null) return false;
            return oid.equals(nid);
        }

        @Override
        public boolean areContentsTheSame(@NonNull EventDTO oldItem, @NonNull EventDTO newItem) {
            String ot = (oldItem.title == null) ? "" : oldItem.title;
            String nt = (newItem.title == null) ? "" : newItem.title;

            // nếu muốn chặt hơn thì so thêm status/startTime/endTime...
            String os = (oldItem.status == null) ? "" : oldItem.status;
            String ns = (newItem.status == null) ? "" : newItem.status;

            return ot.equals(nt) && os.equals(ns);
        }
    };

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        EventDTO e = getItem(position);
        holder.txtTitle.setText(e.title != null ? e.title : "");
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtTitle;

        VH(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}
