package vn.hcmute.trainingpoints.exception;

public class PointsAlreadyAwardedException extends RuntimeException {
    public PointsAlreadyAwardedException(String message) {
        super(message);
    }
}
