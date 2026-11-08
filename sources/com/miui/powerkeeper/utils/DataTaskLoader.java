package com.miui.powerkeeper.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public class DataTaskLoader<D> extends AsyncTaskLoader<D> {
    private D mData;

    public DataTaskLoader(Context context) {
        super(context);
    }

    @Override // android.content.Loader
    public void deliverResult(D d2) {
        if (!isReset() && isStarted()) {
            super.deliverResult(d2);
        }
    }

    @Override // android.content.AsyncTaskLoader
    public D loadInBackground() {
        return null;
    }

    @Override // android.content.AsyncTaskLoader
    protected D onLoadInBackground() {
        D d2 = (D) super.onLoadInBackground();
        this.mData = d2;
        return d2;
    }

    @Override // android.content.Loader
    protected void onStartLoading() {
        D d2 = this.mData;
        if (d2 != null) {
            deliverResult(d2);
        }
        if (takeContentChanged() || this.mData == null) {
            forceLoad();
        }
    }

    @Override // android.content.Loader
    protected void onStopLoading() {
        cancelLoad();
    }
}
