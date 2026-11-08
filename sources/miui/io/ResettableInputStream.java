package miui.io;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;
import com.xiaomi.push.service.PushConstants;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class ResettableInputStream extends InputStream {
    private final AssetManager mAssetManager;
    private final String mAssetPath;
    private final byte[] mByteArray;
    private final Context mContext;
    private final Object mFinalizeGuardian;
    private volatile InputStream mInputStream;
    private IOException mLastException;
    private Throwable mOpenStack;
    private final String mPath;
    private final Type mType;
    private final Uri mUri;

    private enum Type {
        File,
        Uri,
        Asset,
        ByteArray
    }

    public ResettableInputStream(String str) {
        this.mFinalizeGuardian = new Object() { // from class: miui.io.ResettableInputStream.1
            protected void finalize() throws Throwable {
                try {
                    if (ResettableInputStream.this.mOpenStack != null) {
                        Log.e("ResettableInputStream", "InputStream is opened but never closed here", ResettableInputStream.this.mOpenStack);
                    }
                    ResettableInputStream.this.close();
                } finally {
                    super.finalize();
                }
            }
        };
        this.mType = Type.File;
        this.mPath = str;
        this.mContext = null;
        this.mUri = null;
        this.mAssetManager = null;
        this.mAssetPath = null;
        this.mByteArray = null;
    }

    private void openStream() throws IOException {
        IOException iOException = this.mLastException;
        if (iOException != null) {
            throw iOException;
        }
        if (this.mInputStream != null) {
            return;
        }
        synchronized (this.mFinalizeGuardian) {
            try {
                if (this.mInputStream != null) {
                    return;
                }
                int iOrdinal = this.mType.ordinal();
                if (iOrdinal == 0) {
                    this.mInputStream = new FileInputStream(this.mPath);
                } else if (iOrdinal == 1) {
                    this.mInputStream = this.mContext.getContentResolver().openInputStream(this.mUri);
                } else if (iOrdinal == 2) {
                    this.mInputStream = this.mAssetManager.open(this.mAssetPath);
                } else {
                    if (iOrdinal != 3) {
                        throw new IllegalStateException("Unkown type " + this.mType);
                    }
                    this.mInputStream = new ByteArrayInputStream(this.mByteArray);
                }
                this.mOpenStack = new Throwable();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        openStream();
        return this.mInputStream.available();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.mInputStream == null) {
            return;
        }
        synchronized (this.mFinalizeGuardian) {
            if (this.mInputStream == null) {
                return;
            }
            try {
                this.mInputStream.close();
                this.mOpenStack = null;
                this.mInputStream = null;
                this.mLastException = null;
            } catch (Throwable th) {
                this.mOpenStack = null;
                this.mInputStream = null;
                this.mLastException = null;
                throw th;
            }
        }
    }

    @Override // java.io.InputStream
    public void mark(int i2) {
        try {
            openStream();
            this.mInputStream.mark(i2);
        } catch (IOException e2) {
            this.mLastException = e2;
        }
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        try {
            openStream();
            return this.mInputStream.markSupported();
        } catch (IOException e2) {
            this.mLastException = e2;
            return super.markSupported();
        }
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        openStream();
        return this.mInputStream.read();
    }

    @Override // java.io.InputStream
    public synchronized void reset() throws IOException {
        if (this.mInputStream != null) {
            if (this.mInputStream instanceof FileInputStream) {
                ((FileInputStream) this.mInputStream).getChannel().position(0L);
                return;
            }
            if (!(this.mInputStream instanceof AssetManager.AssetInputStream) && !(this.mInputStream instanceof ByteArrayInputStream)) {
                close();
            }
            this.mInputStream.reset();
        }
    }

    @Override // java.io.InputStream
    public long skip(long j2) throws IOException {
        openStream();
        return this.mInputStream.skip(j2);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        openStream();
        return this.mInputStream.read(bArr);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i2, int i3) throws IOException {
        openStream();
        return this.mInputStream.read(bArr, i2, i3);
    }

    public ResettableInputStream(Context context, Uri uri) {
        this.mFinalizeGuardian = new Object() { // from class: miui.io.ResettableInputStream.1
            protected void finalize() throws Throwable {
                try {
                    if (ResettableInputStream.this.mOpenStack != null) {
                        Log.e("ResettableInputStream", "InputStream is opened but never closed here", ResettableInputStream.this.mOpenStack);
                    }
                    ResettableInputStream.this.close();
                } finally {
                    super.finalize();
                }
            }
        };
        if (PushConstants.UPLOAD_FILE_POST_KEY.equals(uri.getScheme())) {
            this.mType = Type.File;
            this.mPath = uri.getPath();
            this.mContext = null;
            this.mUri = null;
            this.mAssetManager = null;
            this.mAssetPath = null;
            this.mByteArray = null;
            return;
        }
        this.mType = Type.Uri;
        this.mContext = context;
        this.mUri = uri;
        this.mPath = null;
        this.mAssetManager = null;
        this.mAssetPath = null;
        this.mByteArray = null;
    }

    public ResettableInputStream(AssetManager assetManager, String str) {
        this.mFinalizeGuardian = new Object() { // from class: miui.io.ResettableInputStream.1
            protected void finalize() throws Throwable {
                try {
                    if (ResettableInputStream.this.mOpenStack != null) {
                        Log.e("ResettableInputStream", "InputStream is opened but never closed here", ResettableInputStream.this.mOpenStack);
                    }
                    ResettableInputStream.this.close();
                } finally {
                    super.finalize();
                }
            }
        };
        this.mType = Type.Asset;
        this.mAssetManager = assetManager;
        this.mAssetPath = str;
        this.mPath = null;
        this.mContext = null;
        this.mUri = null;
        this.mByteArray = null;
    }

    public ResettableInputStream(byte[] bArr) {
        this.mFinalizeGuardian = new Object() { // from class: miui.io.ResettableInputStream.1
            protected void finalize() throws Throwable {
                try {
                    if (ResettableInputStream.this.mOpenStack != null) {
                        Log.e("ResettableInputStream", "InputStream is opened but never closed here", ResettableInputStream.this.mOpenStack);
                    }
                    ResettableInputStream.this.close();
                } finally {
                    super.finalize();
                }
            }
        };
        this.mType = Type.ByteArray;
        this.mByteArray = bArr;
        this.mPath = null;
        this.mContext = null;
        this.mUri = null;
        this.mAssetManager = null;
        this.mAssetPath = null;
    }
}
