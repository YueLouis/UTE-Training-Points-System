package vn.hcmute.utetrainingpointssystem.ui.event;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;

public class EventAdapter extends ListAdapter<EventDTO, EventAdapter.VH> {

    private OnItemClickListener listener;
    private OnActionClickListener actionListener;
    private boolean showCancelButton = false;

    public interface OnItemClickListener {
        void onItemClick(EventDTO event);
    }

    public interface OnActionClickListener {
        void onActionClick(EventDTO event);
    }

    public EventAdapter() {
        super(DIFF);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnActionClickListener(OnActionClickListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setShowCancelButton(boolean show) {
        this.showCancelButton = show;
    }

    private static final DiffUtil.ItemCallback<EventDTO> DIFF = new DiffUtil.ItemCallback<EventDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull EventDTO oldItem, @NonNull EventDTO newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull EventDTO oldItem, @NonNull EventDTO newItem) {
            return oldItem.title.equals(newItem.title) && oldItem.startTime.equals(newItem.startTime);
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
        holder.tvTitle.setText(e.title != null ? e.title.toUpperCase() : "");
        
        // Setup time and location string
        String timeLoc = e.startTime + " | Trực tiếp"; 
        holder.tvTimeLocation.setText(timeLoc);

        // Styling based on category
        int bgColor;
        int accentColor;
        String catLabel;

        long catId = e.categoryId;
        if (catId == 1) { // Example IDs, adjust to your backend
            bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.drl_color);
            accentColor = bgColor;
            catLabel = "DRL";
        } else if (catId == 2) {
            bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.ctxh_color);
            accentColor = bgColor;
            catLabel = "CTXH";
        } else {
            bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.cddn_color);
            accentColor = bgColor;
            catLabel = "CDDN";
        }

        holder.layoutCard.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        holder.tvCategoryLabel.setText(catLabel);
        holder.tvCategoryLabel.setTextColor(accentColor);
        holder.imgIcon.setImageTintList(ColorStateList.valueOf(accentColor));

        if (showCancelButton) {
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.tvHint.setVisibility(View.GONE);
            holder.btnAction.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onActionClick(e);
            });
        } else {
            holder.btnAction.setVisibility(View.GONE);
            holder.tvHint.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(e);
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout layoutCard;
        TextView tvTitle, tvTimeLocation, tvCategoryLabel, btnAction, tvHint;
        ImageView imgIcon;

        VH(@NonNull View itemView) {
            super(itemView);
            layoutCard = itemView.findViewById(R.id.layoutEventCard);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvTimeLocation = itemView.findViewById(R.id.tvEventTimeLocation);
            tvCategoryLabel = itemView.findViewById(R.id.tvCategoryLabel);
            btnAction = itemView.findViewById(R.id.btnAction);
            tvHint = itemView.findViewById(R.id.tvHint);
            imgIcon = itemView.findViewById(R.id.imgEventIcon);
        }
    }
}
