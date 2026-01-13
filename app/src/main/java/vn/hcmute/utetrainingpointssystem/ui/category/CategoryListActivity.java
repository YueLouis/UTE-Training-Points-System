package vn.hcmute.utetrainingpointssystem.ui.category;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vn.hcmute.utetrainingpointssystem.R;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.category.EventCategoryDTO;
import vn.hcmute.utetrainingpointssystem.viewmodel.category.CategoryListViewModel;
import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity {

    private CategoryListViewModel viewModel;
    private RecyclerView rvCategories;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Setup RecyclerView
        rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter();
        rvCategories.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CategoryListViewModel.class);

        // Observe categories
        viewModel.getCategoriesResult().observe(this, result -> {
            if (result instanceof ResultState.Success) {
                ResultState.Success<List<EventCategoryDTO>> successState = (ResultState.Success<List<EventCategoryDTO>>) result;
                List<EventCategoryDTO> categories = successState.data;
                adapter.submitList(categories != null ? categories : new ArrayList<>());
            } else if (result instanceof ResultState.Error) {
                Toast.makeText(this, ((ResultState.Error<?>) result).message, Toast.LENGTH_SHORT).show();
            }
        });

        // Load categories
        viewModel.fetchAllCategories();
    }
}

