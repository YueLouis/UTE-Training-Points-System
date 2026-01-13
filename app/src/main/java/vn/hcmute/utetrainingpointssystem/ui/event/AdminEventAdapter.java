package vn.hcmute.utetrainingpointssystem.ui.event;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.model.event.EventDTO;
import vn.hcmute.utetrainingpointssystem.viewmodel.event.EventListViewModel;

public class AdminEventAdapter extends ListAdapter<EventDTO, AdminEventAdapter.ViewHolder> {

    private final EventListViewModel viewModel;
    private final Context context;
    private OnEventDeletedListener onEventDeletedListener;

    public interface OnEventDeletedListener {
        void onEventDeleted();
    }

    public void setOnEventDeletedListener(OnEventDeletedListener listener) {
        this.onEventDeletedListener = listener;
    }

    public AdminEventAdapter(EventListViewModel viewModel, Context context) {
        super(DIFF_CALLBACK);
        this.viewModel = viewModel;
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<EventDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<EventDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull EventDTO oldItem, @NonNull EventDTO newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull EventDTO oldItem, @NonNull EventDTO newItem) {
            return oldItem.title.equals(newItem.title);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_admin, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventDTO event = getItem(position);
        holder.bind(event, viewModel, context, onEventDeletedListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEventTitle;
        private final TextView tvEventCategory;
        private final TextView tvEventStatus;
        private final TextView tvEventTime;
        private final Button btnEdit;
        private final Button btnDelete;

        public ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventCategory = itemView.findViewById(R.id.tvEventCategory);
            tvEventStatus = itemView.findViewById(R.id.tvEventStatus);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(EventDTO event, EventListViewModel viewModel, Context context, AdminEventAdapter.OnEventDeletedListener onEventDeletedListener) {
            tvEventTitle.setText(event.title != null ? event.title : "Event");
            tvEventCategory.setText(getCategoryName(event.categoryId));
            tvEventStatus.setText(event.status != null ? event.status : "UNKNOWN");
            tvEventTime.setText(event.startTime != null ? event.startTime : "N/A");

            // Click to view details
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("eventId", event.id);
                context.startActivity(intent);
            });

            // Edit button
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EventCreateActivity.class);
                intent.putExtra("eventId", event.id);
                intent.putExtra("isEdit", true);
                context.startActivity(intent);
            });

            // Delete button
            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn chắc muốn xóa sự kiện: " + event.title + "?")
                        .setNegativeButton("Hủy", null)
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            Toast.makeText(context, "Đang xóa...", Toast.LENGTH_SHORT).show();

                            viewModel.deleteEvent(event.id, new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(context, "✓ Sự kiện đã bị xóa", Toast.LENGTH_SHORT).show();
                                        if (onEventDeletedListener != null) {
                                            onEventDeletedListener.onEventDeleted();
                                        }
                                    } else {
                                        // Handle specific error codes
                                        String errorMsg = "Xóa thất bại";

                                        try {
                                            if (response.errorBody() != null) {
                                                String errorBody = response.errorBody().string();
                                                if (errorBody.contains("foreign key constraint")) {
                                                    errorMsg = "❌ Không thể xóa! Sự kiện đã có dữ liệu liên kết";
                                                } else if (errorBody.contains("CONSTRAINT")) {
                                                    errorMsg = "❌ Không thể xóa! Sự kiện đã có dữ liệu liên kết";
                                                }
                                            }
                                        } catch (Exception e) {
                                            // If error parsing fails
                                            if (response.code() == 409) {
                                                errorMsg = "❌ Không thể xóa! Sự kiện đã có dữ liệu liên kết";
                                            }
                                        }

                                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    String errorMsg = t.getMessage();
                                    if (errorMsg == null || errorMsg.isEmpty()) {
                                        errorMsg = "Lỗi kết nối";
                                    }
                                    Toast.makeText(context, "❌ Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
                                }
                            });
                        })
                        .show();
            });
        }

        private String getCategoryName(Long categoryId) {
            if (categoryId == null) return "Unknown";
            switch (categoryId.intValue()) {
                case 1:
                    return "DRL";
                case 2:
                    return "CTXH";
                case 3:
                    return "CDDN";
                default:
                    return "Other";
            }
        }
    }
}

