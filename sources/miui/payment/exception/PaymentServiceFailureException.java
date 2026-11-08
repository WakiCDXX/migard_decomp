package miui.payment.exception;

import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class PaymentServiceFailureException extends Exception {
    private static final long serialVersionUID = 1;
    private int mErrorCode;
    private Bundle mErrorResult;

    public PaymentServiceFailureException(int i2, String str) {
        this(i2, str, null);
    }

    public int getError() {
        return this.mErrorCode;
    }

    public Bundle getErrorResult() {
        return this.mErrorResult;
    }

    public PaymentServiceFailureException(int i2, String str, Bundle bundle) {
        super(str);
        this.mErrorCode = i2;
        if (bundle == null) {
            this.mErrorResult = new Bundle();
        } else {
            this.mErrorResult = bundle;
        }
    }
}
