/*
 * SingleLiveEvent: LiveData chỉ bắn 1 lần cho các "event" UI.
 *
 * Dùng cho: toast, snackbar, navigate, finish(), open screen...
 * Tránh lỗi xoay màn hình/Back stack làm event bị bắn lại.
 *
 * MVVM: ViewModel phát event -> Activity/Fragment observe và xử lý 1 lần.
 */
package vn.hcmute.utetrainingpointssystem.core;

public class SingleLiveEvent {
}
