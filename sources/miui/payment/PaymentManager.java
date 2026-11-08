package miui.payment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.push.extension.PluginConstants;
import com.xiaomi.smack.packet.Message;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import miui.cloud.exception.AuthenticationFailureException;
import miui.cloud.exception.OperationCancelledException;
import miui.os.Build;
import miui.payment.exception.PaymentServiceFailureException;

/* JADX INFO: loaded from: classes.dex */
public class PaymentManager {
    private static final String ACTION_PAYMENT = "com.xiaomi.xmsf.action.PAYMENT";
    public static final int CAPABILITY = 3;
    private static final boolean DEBUG = true;
    public static final int ERROR_CODE_ACCOUNT_CHANGED = 10;
    public static final int ERROR_CODE_ACCOUNT_FROZEN = 9;
    public static final int ERROR_CODE_ACCOUNT_THROTTING = 15;
    public static final int ERROR_CODE_AUTHENTICATION_ERROR = 5;
    public static final int ERROR_CODE_CALLER_INVALID = 12;
    public static final int ERROR_CODE_CALL_TOO_FAST = 14;
    public static final int ERROR_CODE_CANCELED = 4;
    public static final int ERROR_CODE_DUPLICATE_DEDUCT = 16;
    public static final int ERROR_CODE_DUPLICATE_PURCHASE = 7;
    public static final int ERROR_CODE_EXCEPTION = 1;
    public static final int ERROR_CODE_INVALID_PARAMS = 2;
    public static final int ERROR_CODE_NETWORK_ERROR = 3;
    public static final int ERROR_CODE_ORDER_ERROR = 13;
    public static final int ERROR_CODE_SERVER_ERROR = 6;
    public static final int ERROR_CODE_THIRD_PARTY = 11;
    public static final int ERROR_CODE_USER_ID_MISMATCH = 8;
    public static final String KEY_INTENT = "intent";
    private static final String PACKAGE_PAYMENT = "com.xiaomi.payment";
    public static final String PAYMENT_KEY_IS_NO_ACCOUNT = "payment_is_no_account";
    public static final String PAYMENT_KEY_PAYMENT_RESULT = "payment_payment_result";

    @Deprecated
    public static final String PAYMENT_KEY_QUICK = "payment_quick";
    public static final String PAYMENT_KEY_TRADE_BALANCE = "payment_trade_balance";
    private static final String TAG = "PaymentManager";
    private final Context mContext;
    private final Handler mMainHandler;

    private class PaymentCallback implements PaymentManagerCallback<Bundle> {
        private String mPaymentId;
        private PaymentListener mPaymentListener;
        private String mServiceId;

        public PaymentCallback(String str, String str2, PaymentListener paymentListener) {
            this.mServiceId = str;
            this.mPaymentId = str2;
            this.mPaymentListener = paymentListener;
        }

        @Override // miui.payment.PaymentManager.PaymentManagerCallback
        public void run(PaymentManagerFuture<Bundle> paymentManagerFuture) {
            if (this.mPaymentListener == null) {
                return;
            }
            try {
                Bundle result = paymentManagerFuture.getResult();
                if (result != null) {
                    this.mPaymentListener.onSuccess(this.mPaymentId, result);
                } else {
                    this.mPaymentListener.onFailed(this.mPaymentId, 1, Message.MSG_TYPE_ERROR, new Bundle());
                }
            } catch (AuthenticationFailureException e2) {
                this.mPaymentListener.onFailed(this.mPaymentId, 5, e2.getMessage(), new Bundle());
            } catch (IOException e3) {
                this.mPaymentListener.onFailed(this.mPaymentId, 3, e3.getMessage(), new Bundle());
            } catch (OperationCancelledException e4) {
                this.mPaymentListener.onFailed(this.mPaymentId, 4, e4.getMessage(), new Bundle());
            } catch (PaymentServiceFailureException e5) {
                this.mPaymentListener.onFailed(this.mPaymentId, e5.getError(), e5.getMessage(), e5.getErrorResult());
            } finally {
                this.mPaymentListener = null;
            }
        }
    }

    public interface PaymentListener {
        void onFailed(String str, int i2, String str2, Bundle bundle);

        void onSuccess(String str, Bundle bundle);
    }

    private interface PaymentManagerCallback<V> {
        void run(PaymentManagerFuture<V> paymentManagerFuture);
    }

    private interface PaymentManagerFuture<V> {
        boolean cancel(boolean z2);

        V getResult() throws OperationCancelledException, IOException, AuthenticationFailureException, PaymentServiceFailureException;

        V getResult(long j2, TimeUnit timeUnit) throws OperationCancelledException, IOException, AuthenticationFailureException, PaymentServiceFailureException;

        boolean isCancelled();

        boolean isDone();
    }

    private abstract class PmsTask extends FutureTask<Bundle> implements PaymentManagerFuture<Bundle>, ServiceConnection {
        private final int HOST_MONITOR_HEART_INTERNAL;
        private Activity mActivity;
        private PaymentManagerCallback<Bundle> mCallback;
        private Handler mHandler;
        private Runnable mHostActivityMonitor;
        private boolean mIsBound;
        private IPaymentManagerResponse mResponse;
        private IPaymentManagerService mService;

        class IPaymentManagerResponseImpl extends IPaymentManagerResponse.Stub {
            IPaymentManagerResponseImpl() {
            }

            @Override // miui.payment.IPaymentManagerResponse
            public void onError(int i2, String str, Bundle bundle) throws RemoteException {
                if (i2 == 4) {
                    PmsTask.this.cancel(true);
                    PmsTask.this.unBind();
                } else {
                    PmsTask pmsTask = PmsTask.this;
                    pmsTask.setException(pmsTask.convertErrorCodeToException(i2, str, bundle));
                }
            }

            @Override // miui.payment.IPaymentManagerResponse
            public void onResult(Bundle bundle) throws RemoteException {
                Intent intent = (Intent) bundle.getParcelable(PaymentManager.KEY_INTENT);
                if (intent == null) {
                    PmsTask.this.set(bundle);
                } else if (PmsTask.this.mActivity != null) {
                    PmsTask.this.mActivity.startActivity(intent);
                } else {
                    PmsTask.this.setException(new PaymentServiceFailureException(2, "activity cannot be null"));
                }
            }
        }

        protected PmsTask(PaymentManager paymentManager, Activity activity) {
            this(paymentManager, activity, null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Exception convertErrorCodeToException(int i2, String str, Bundle bundle) {
            if (i2 == 3) {
                return new IOException(str);
            }
            if (i2 == 5) {
                return new AuthenticationFailureException(str);
            }
            if (TextUtils.isEmpty(str)) {
                str = "Unknown payment failure";
            }
            return new PaymentServiceFailureException(i2, str, bundle);
        }

        private void ensureNotOnMainThread() {
            Looper looperMyLooper = Looper.myLooper();
            if (looperMyLooper == null || looperMyLooper != PaymentManager.this.mContext.getMainLooper()) {
                return;
            }
            IllegalStateException illegalStateException = new IllegalStateException("calling this from your main thread can lead to deadlock");
            Log.e(PaymentManager.TAG, "calling this from your main thread can lead to deadlock and/or ANRs", illegalStateException);
            throw illegalStateException;
        }

        private Bundle internalGetResult(Long l2, TimeUnit timeUnit) throws OperationCancelledException, IOException, AuthenticationFailureException, PaymentServiceFailureException {
            if (!isDone()) {
                ensureNotOnMainThread();
            }
            try {
                try {
                    try {
                        if (l2 == null) {
                            Bundle bundle = get();
                            cancel(true);
                            return bundle;
                        }
                        Bundle bundle2 = get(l2.longValue(), timeUnit);
                        cancel(true);
                        return bundle2;
                    } catch (InterruptedException | TimeoutException unused) {
                        cancel(true);
                        throw new OperationCancelledException("cancelled by exception");
                    }
                } catch (CancellationException unused2) {
                    throw new OperationCancelledException("cancelled by user");
                } catch (ExecutionException e2) {
                    Throwable cause = e2.getCause();
                    if (cause instanceof IOException) {
                        throw ((IOException) cause);
                    }
                    if (cause instanceof PaymentServiceFailureException) {
                        throw ((PaymentServiceFailureException) cause);
                    }
                    if (cause instanceof AuthenticationFailureException) {
                        throw ((AuthenticationFailureException) cause);
                    }
                    if (cause instanceof OperationCancelledException) {
                        throw ((OperationCancelledException) cause);
                    }
                    if (cause instanceof RuntimeException) {
                        throw ((RuntimeException) cause);
                    }
                    if (cause instanceof Error) {
                        throw ((Error) cause);
                    }
                    throw new IllegalStateException(cause);
                }
            } catch (Throwable th) {
                cancel(true);
                throw th;
            }
        }

        protected void bind() {
            if (this.mIsBound) {
                return;
            }
            if (!bindToPaymentService()) {
                setException(new PaymentServiceFailureException(1, "bind to service failed"));
            } else {
                this.mIsBound = true;
                Log.d(PaymentManager.TAG, "service bound");
            }
        }

        protected boolean bindToPaymentService() {
            Intent intent = new Intent(PaymentManager.ACTION_PAYMENT);
            intent.setPackage(PaymentManager.PACKAGE_PAYMENT);
            return PaymentManager.this.mContext.bindService(intent, this, 1);
        }

        protected abstract void doWork() throws RemoteException;

        @Override // java.util.concurrent.FutureTask
        protected void done() {
            if (this.mCallback != null) {
                Handler handler = this.mHandler;
                if (handler == null) {
                    handler = PaymentManager.this.mMainHandler;
                }
                handler.post(new Runnable() { // from class: miui.payment.PaymentManager.PmsTask.3
                    @Override // java.lang.Runnable
                    public void run() {
                        PmsTask.this.mCallback.run(PmsTask.this);
                        PmsTask.this.mCallback = null;
                    }
                });
            }
            PaymentManager.this.mMainHandler.removeCallbacks(this.mHostActivityMonitor);
            this.mHandler = null;
            this.mActivity = null;
        }

        protected IPaymentManagerResponse getResponse() {
            return this.mResponse;
        }

        protected IPaymentManagerService getService() {
            return this.mService;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(PaymentManager.TAG, "onServiceConnected, component:" + componentName);
            this.mService = IPaymentManagerService.Stub.asInterface(iBinder);
            try {
                doWork();
                PaymentManager.this.mMainHandler.postDelayed(this.mHostActivityMonitor, PluginConstants.TTS_REMOVE_BEAN_TIME);
            } catch (RemoteException e2) {
                setException(e2);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            if (!isDone()) {
                Log.e(PaymentManager.TAG, "payment service disconnected, but task is not completed");
                setException(new PaymentServiceFailureException(1, "active service exits unexpectedly"));
            }
            this.mService = null;
        }

        @Override // java.util.concurrent.FutureTask
        protected void setException(Throwable th) {
            super.setException(th);
            unBind();
        }

        public final PaymentManagerFuture<Bundle> start() {
            bind();
            return this;
        }

        protected void unBind() {
            if (this.mIsBound) {
                PaymentManager.this.mContext.unbindService(this);
                this.mIsBound = false;
                Log.d(PaymentManager.TAG, "service unbinded");
            }
        }

        protected PmsTask(PaymentManager paymentManager, Activity activity, PaymentManagerCallback<Bundle> paymentManagerCallback) {
            this(activity, null, paymentManagerCallback);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.util.concurrent.FutureTask
        public void set(Bundle bundle) {
            super.set(bundle);
            unBind();
        }

        protected PmsTask(Activity activity, Handler handler, PaymentManagerCallback<Bundle> paymentManagerCallback) {
            super(new Callable<Bundle>() { // from class: miui.payment.PaymentManager.PmsTask.2
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Bundle call() throws Exception {
                    throw new IllegalStateException("this should never be called");
                }
            });
            this.mIsBound = false;
            this.HOST_MONITOR_HEART_INTERNAL = 5000;
            this.mHostActivityMonitor = new Runnable() { // from class: miui.payment.PaymentManager.PmsTask.1
                @Override // java.lang.Runnable
                public void run() {
                    Activity activity2 = PmsTask.this.mActivity;
                    if (PmsTask.this.isDone() || activity2 == null) {
                        return;
                    }
                    if (activity2.isFinishing()) {
                        PmsTask.this.setException(new OperationCancelledException("Operation has been cancelled because host activity has finished."));
                    } else {
                        PaymentManager.this.mMainHandler.postDelayed(this, PluginConstants.TTS_REMOVE_BEAN_TIME);
                    }
                }
            };
            this.mActivity = activity;
            this.mHandler = handler;
            this.mCallback = paymentManagerCallback;
            this.mResponse = new IPaymentManagerResponseImpl();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // miui.payment.PaymentManager.PaymentManagerFuture
        public Bundle getResult(long j2, TimeUnit timeUnit) throws OperationCancelledException, IOException, AuthenticationFailureException, PaymentServiceFailureException {
            return internalGetResult(Long.valueOf(j2), timeUnit);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // miui.payment.PaymentManager.PaymentManagerFuture
        public Bundle getResult() throws OperationCancelledException, IOException, AuthenticationFailureException, PaymentServiceFailureException {
            return internalGetResult(null, null);
        }
    }

    private PaymentManager(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mMainHandler = new Handler(applicationContext.getMainLooper());
    }

    public static PaymentManager get(Context context) {
        return new PaymentManager(context);
    }

    private PaymentManagerFuture<Bundle> internalGetMiliBalance(Activity activity, final String str, final String str2, PaymentManagerCallback<Bundle> paymentManagerCallback) {
        return new PmsTask(activity, paymentManagerCallback) { // from class: miui.payment.PaymentManager.2
            @Override // miui.payment.PaymentManager.PmsTask
            protected void doWork() throws RemoteException {
                getService().getMiliBalance(getResponse(), null, str, str2);
            }
        }.start();
    }

    private PaymentManagerFuture<Bundle> internalPayForOrder(Activity activity, final String str, final Bundle bundle, PaymentManagerCallback<Bundle> paymentManagerCallback) {
        return new PmsTask(activity, paymentManagerCallback) { // from class: miui.payment.PaymentManager.1
            @Override // miui.payment.PaymentManager.PmsTask
            protected void doWork() throws RemoteException {
                IPaymentManagerService service = getService();
                Bundle bundle2 = new Bundle();
                Bundle bundle3 = bundle;
                if (bundle3 != null) {
                    bundle2.putAll(bundle3);
                }
                service.payForOrder(getResponse(), null, str, bundle2);
            }
        }.start();
    }

    private void internalStartMibiIntent(Activity activity, Uri uri) {
        if (activity == null) {
            throw new InvalidParameterException("activity cannot be null");
        }
        if (uri == null) {
            throw new InvalidParameterException("intent uri cannot be null");
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setPackage(PACKAGE_PAYMENT);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public void getMiliBalance(Activity activity, String str, String str2, String str3, PaymentListener paymentListener) {
        if (activity == null) {
            throw new InvalidParameterException("activity cannot be null");
        }
        if (TextUtils.isEmpty(str2)) {
            throw new InvalidParameterException("serviceId cannot be empty");
        }
        if (TextUtils.isEmpty(str3)) {
            throw new InvalidParameterException("verify cannot be empty");
        }
        internalGetMiliBalance(activity, str2, str3, new PaymentCallback(str2, str, paymentListener));
    }

    public void gotoMiliCenter(Activity activity) {
        internalStartMibiIntent(activity, Uri.parse("https://app.mibi.xiaomi.com/?id=mibi.milicenter"));
    }

    public void gotoPayRecord(Activity activity, String str, String str2) {
        internalStartMibiIntent(activity, Uri.parse("https://app.mibi.xiaomi.com/?id=mibi.billRecord"));
    }

    public void gotoRechargeRecord(Activity activity, String str, String str2) {
        internalStartMibiIntent(activity, Uri.parse("https://app.mibi.xiaomi.com/?id=mibi.billRecord"));
    }

    public boolean isMibiServiceDisabled() {
        Intent intent = new Intent(ACTION_PAYMENT);
        intent.setPackage(PACKAGE_PAYMENT);
        if (this.mContext.getPackageManager().resolveService(intent, 0) == null) {
            return true;
        }
        return Build.IS_INTERNATIONAL_BUILD && !Build.checkRegion("HK");
    }

    public void payForOrder(Activity activity, String str, String str2, Bundle bundle, PaymentListener paymentListener) {
        if (activity == null) {
            throw new InvalidParameterException("activity cannot be null");
        }
        if (TextUtils.isEmpty(str2)) {
            throw new InvalidParameterException("order cannot be empty");
        }
        internalPayForOrder(activity, str2, bundle, new PaymentCallback("thd", str, paymentListener));
    }

    public void recharge(Activity activity, String str, String str2, String str3) {
        internalStartMibiIntent(activity, Uri.parse("https://app.mibi.xiaomi.com/?id=mibi.recharge"));
    }
}
