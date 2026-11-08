package a;

import android.media.audiofx.AudioEffect;
import android.util.Log;
import com.miui.powerkeeper.powerchecker.BatteryStatsHelper;
import java.util.UUID;

/* JADX INFO: compiled from: DolbyAudioEffect.java */
/* JADX INFO: loaded from: classes.dex */
public class a extends AudioEffect {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final UUID f0d = UUID.fromString("9d4921da-8225-4f29-aefa-39537a04bcaa");

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    private static int f1e = 0;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private boolean f2a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private int f3b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    private final Object f4c;

    public a(int i2, int i3) throws RuntimeException {
        super(AudioEffect.EFFECT_TYPE_NULL, f0d, i2, i3);
        this.f2a = true;
        this.f4c = new Object();
        if (i3 == 0) {
            Log.i("DolbyAudioEffect", "Creating a DolbyAudioEffect to global output mix!");
        }
        this.f3b = i3;
        if (f1e == 0) {
            f1e = f();
        }
    }

    private static int a(byte[] bArr) {
        return (bArr[0] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) | ((bArr[3] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 24) | ((bArr[2] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 16) | ((bArr[1] & BatteryStatsHelper.BatteryHistoryItemV23.CMD_NULL) << 8);
    }

    private void b(int i2) {
        if (i2 < 0) {
            if (i2 == -5) {
                throw new UnsupportedOperationException("DolbyAudioEffect: invalid parameter operation");
            }
            if (i2 == -4) {
                throw new IllegalArgumentException("DolbyAudioEffect: bad parameter value");
            }
            throw new RuntimeException("DolbyAudioEffect: set/get parameter error");
        }
    }

    private static int g(int i2, byte[] bArr, int i3) {
        bArr[i3] = (byte) (i2 & 255);
        bArr[i3 + 1] = (byte) ((i2 >>> 8) & 255);
        bArr[i3 + 2] = (byte) ((i2 >>> 16) & 255);
        bArr[i3 + 3] = (byte) ((i2 >>> 24) & 255);
        return 4;
    }

    protected boolean c(int i2) {
        byte[] bArr = new byte[12];
        g(i2, bArr, 0);
        b(getParameter(i2 + 5, bArr));
        return a(bArr) > 0;
    }

    public boolean d() {
        return c(0);
    }

    protected int e(int i2) {
        byte[] bArr = new byte[12];
        g(i2, bArr, 0);
        b(getParameter(i2 + 5, bArr));
        return a(bArr);
    }

    public int f() {
        return e(50331648);
    }

    protected int h(int i2, boolean z2) {
        byte[] bArr = new byte[12];
        int iG = g(i2, bArr, 0);
        g(z2 ? 1 : 0, bArr, iG + g(1, bArr, iG));
        b(setParameter(5, bArr));
        return 0;
    }

    @Override // android.media.audiofx.AudioEffect
    public boolean hasControl() {
        try {
            return super.hasControl();
        } catch (IllegalStateException e2) {
            Log.e("DolbyAudioEffect", e2.toString());
            return false;
        }
    }

    public void i(boolean z2) {
        h(0, z2);
        super.setEnabled(z2);
    }

    protected int j(int i2, int i3) {
        byte[] bArr = new byte[12];
        int iG = g(i2, bArr, 0);
        g(i3, bArr, iG + g(1, bArr, iG));
        b(setParameter(5, bArr));
        return 0;
    }

    public void k(int i2) throws IllegalArgumentException {
        if (i2 >= 0 && i2 < f1e) {
            j(167772160, i2);
            return;
        }
        Log.e("DolbyAudioEffect", "ERROR in setProfile(): Invalid profile index" + i2);
        throw new IllegalArgumentException();
    }
}
