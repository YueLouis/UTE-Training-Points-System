package vn.hcmute.utetrainingpointssystem.viewmodel.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.utetrainingpointssystem.core.ResultState;
import vn.hcmute.utetrainingpointssystem.model.category.EventCategoryDTO;
import vn.hcmute.utetrainingpointssystem.network.RetrofitClient;
import vn.hcmute.utetrainingpointssystem.network.api.EventCategoryApi;

public class CategoryListViewModel extends ViewModel {

    private final MutableLiveData<ResultState<List<EventCategoryDTO>>> categoriesState = new MutableLiveData<>();

    public LiveData<ResultState<List<EventCategoryDTO>>> getCategoriesResult() {
        return categoriesState;
    }

    public void fetchAllCategories() {
        categoriesState.setValue(new ResultState.Loading<>());

        EventCategoryApi api = RetrofitClient.getClient().create(EventCategoryApi.class);
        api.getAll().enqueue(new Callback<List<EventCategoryDTO>>() {
            @Override
            public void onResponse(Call<List<EventCategoryDTO>> call, Response<List<EventCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriesState.setValue(new ResultState.Success<>(response.body()));
                } else {
                    categoriesState.setValue(new ResultState.Error<>("Failed to load categories"));
                }
            }

            @Override
            public void onFailure(Call<List<EventCategoryDTO>> call, Throwable t) {
                categoriesState.setValue(new ResultState.Error<>(t.getMessage()));
            }
        });
    }
}

