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
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull EventDTO oldItem, @NonNull EventDTO newItem) {
            String ot = oldItem.getTitle() == null ? "" : oldItem.getTitle();
            String nt = newItem.getTitle() == null ? "" : newItem.getTitle();
            return ot.equals(nt);
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
        holder.txtTitle.setText(e.getTitle());
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtTitle;
        VH(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}
