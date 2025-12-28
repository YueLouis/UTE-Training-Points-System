package vn.hcmute.utetrainingpointssystem.core;

/*
 * ResultState: wrapper trạng thái dữ liệu cho MVVM:
 * - Loading: đang gọi API
 * - Success<T>: lấy dữ liệu OK
 * - Error: API lỗi / exception
 *
 * ViewModel emit ResultState, Activity/Fragment observe và render UI (loading/data/error).
 */
public abstract class ResultState<T> {

    public static final class Loading<T> extends ResultState<T> { }

    public static final class Success<T> extends ResultState<T> {
        public final T data;
        public Success(T data) { this.data = data; }
    }

    public static final class Error<T> extends ResultState<T> {
        public final String message;
        public Error(String message) { this.message = message; }
    }
}
