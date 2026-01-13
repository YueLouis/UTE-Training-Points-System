package vn.hcmute.utetrainingpointssystem.ui.user;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.model.user.UserDTO;

public class UserAdapter extends ListAdapter<UserDTO, UserAdapter.ViewHolder> {

    public UserAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<UserDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<UserDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull UserDTO oldItem, @NonNull UserDTO newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull UserDTO oldItem, @NonNull UserDTO newItem) {
            return oldItem.fullName.equals(newItem.fullName);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDTO user = getItem(position);
        holder.bind(user);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName;
        private final TextView tvUserEmail;
        private final TextView tvUserRole;
        private final TextView tvUserCode;

        public ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvUserCode = itemView.findViewById(R.id.tvUserCode);
        }

        public void bind(UserDTO user) {
            tvUserName.setText(user.fullName != null ? user.fullName : "N/A");
            tvUserEmail.setText(user.email != null ? user.email : "N/A");
            tvUserRole.setText(user.role != null ? user.role : "STUDENT");
            tvUserCode.setText(user.studentCode != null ? user.studentCode : "N/A");
        }
    }
}

