package vn.hcmute.utetrainingpointssystem.ui.category;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.model.category.EventCategoryDTO;

public class CategoryAdapter extends ListAdapter<EventCategoryDTO, CategoryAdapter.ViewHolder> {

    public CategoryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<EventCategoryDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<EventCategoryDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull EventCategoryDTO oldItem, @NonNull EventCategoryDTO newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull EventCategoryDTO oldItem, @NonNull EventCategoryDTO newItem) {
            return oldItem.name.equals(newItem.name);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventCategoryDTO category = getItem(position);
        holder.bind(category);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategoryName;
        private final TextView tvCategoryDescription;

        public ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
        }

        public void bind(EventCategoryDTO category) {
            tvCategoryName.setText(category.name);
            tvCategoryDescription.setText(category.description != null ? category.description : "No description");
        }
    }
}

