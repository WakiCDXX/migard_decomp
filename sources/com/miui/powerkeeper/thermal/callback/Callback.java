package com.miui.powerkeeper.thermal.callback;

import com.miui.powerkeeper.thermal.IThermalScenarioListener;
import java.util.Arrays;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class Callback {
    private int mCurrentTempState;
    private IThermalScenarioListener mListener;
    private int[] mThreshold;
    private int mThresholdLength;

    public Callback(int[] iArr, IThermalScenarioListener iThermalScenarioListener) {
        this.mListener = iThermalScenarioListener;
        this.mThreshold = iArr;
        this.mThresholdLength = iArr.length;
    }

    private int getTempState(int i2) {
        int i3 = 0;
        while (true) {
            int i4 = this.mThresholdLength;
            if (i3 >= i4) {
                return i4;
            }
            if (i2 < this.mThreshold[i3]) {
                return i3;
            }
            i3++;
        }
    }

    private void sendTempChanged(int i2) {
        try {
            this.mListener.onTemperatureChanged(i2);
        } catch (Exception unused) {
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Callback callback = (Callback) obj;
        return this.mThresholdLength == callback.mThresholdLength && this.mListener.equals(callback.mListener) && Arrays.equals(this.mThreshold, callback.mThreshold);
    }

    public int hashCode() {
        return (Objects.hash(this.mListener, Integer.valueOf(this.mThresholdLength)) * 31) + Arrays.hashCode(this.mThreshold);
    }

    public boolean listenerEquals(IThermalScenarioListener iThermalScenarioListener) {
        return this.mListener.asBinder() == iThermalScenarioListener.asBinder();
    }

    void onTempChanged(int i2) {
        int tempState = getTempState(i2);
        if (tempState != this.mCurrentTempState) {
            this.mCurrentTempState = tempState;
            sendTempChanged(i2);
        }
    }

    void onScenarioChanged(int i2) {
    }
}
