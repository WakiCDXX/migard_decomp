package miui.cloud.exception;

/* JADX INFO: loaded from: classes.dex */
public class CloudServiceFailureException extends Exception {
    private int errorCode;

    public CloudServiceFailureException() {
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public CloudServiceFailureException(String str) {
        super(str);
    }

    public CloudServiceFailureException(Throwable th) {
        super(th);
    }

    public CloudServiceFailureException(Throwable th, int i2) {
        super(th);
        this.errorCode = i2;
    }
}
