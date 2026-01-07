package vn.hcmute.utetrainingpointssystem.ui.event;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                   oldItem.isRegistered() == newItem.isRegistered();
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
        holder.tvTitle.setText(e.getTitle());
        holder.tvPointType.setText(e.getPointType() != null ? e.getPointType() : "DRL");
        
        String timeInfo = String.format("%s | %s", e.getStartTime(), e.getType() != null ? e.getType() : "Trực tiếp");
        holder.tvTime.setText(timeInfo);

        int color;
        if ("CTXH".equals(e.getPointType())) {
            color = Color.parseColor("#42A5F5"); // Blue
        } else if ("CDDN".equals(e.getPointType())) {
            color = Color.parseColor("#66BB6A"); // Green
        } else {
            color = Color.parseColor("#26C6DA"); // Cyan
        }

        holder.layoutItem.setBackgroundColor(color);
        holder.tvPointType.setTextColor(color);
        holder.imgEventIcon.setColorFilter(color);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("EVENT_ID", e.getId());
            v.getContext().startActivity(intent);
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvPointType;
        LinearLayout layoutItem;
        ImageView imgEventIcon;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPointType = itemView.findViewById(R.id.tvPointType);
            layoutItem = itemView.findViewById(R.id.layoutItem);
            imgEventIcon = itemView.findViewById(R.id.imgEventIcon);
        }
    }
}
