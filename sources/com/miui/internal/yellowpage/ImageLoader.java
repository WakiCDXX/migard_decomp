package com.miui.internal.yellowpage;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.WindowManager;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import miui.provider.MiCloudSmsCmd;
import miui.yellowpage.Log;
import miui.yellowpage.YellowPageContract;
import miui.yellowpage.YellowPageImgLoader;

/* JADX INFO: loaded from: classes.dex */
public class ImageLoader implements Handler.Callback {
    private static final int BITMAP_CACHE_SIZE = 10485760;
    private static final int MESSAGE_REQUEST_LOAD = 2;
    private static final int MESSAGE_REQUEST_LOADED = 1;
    private static final String TAG = "ImageLoader";
    private static final int THREAD_POOL_COUNT = 6;
    private static final int UI_MODE_INIT = -1;
    private static int sDisplayHeight;
    private static int sDisplayWidth;
    private static ImageLoader sLoader;
    private Context mContext;
    private Handler mMainThreadHandler;
    private int mNetworkAccess;
    private volatile boolean mPauseLoading;
    private final Object BITMAP_CACHE_LOCK = new Object();
    private int mCurrentUiMode = -1;
    private final LruCache<YellowPageImgLoader.Image, BitmapHolder> mBitmapCache = new LruCache<YellowPageImgLoader.Image, BitmapHolder>(BITMAP_CACHE_SIZE) { // from class: com.miui.internal.yellowpage.ImageLoader.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.util.LruCache
        public int sizeOf(YellowPageImgLoader.Image image, BitmapHolder bitmapHolder) {
            Bitmap bitmap;
            if (bitmapHolder == null || (bitmap = bitmapHolder.mBitmap) == null) {
                return 0;
            }
            return bitmap.getByteCount();
        }
    };
    private final Map<Object, Bitmap> mDefaultBitmapCache = new HashMap();
    private final Map<YellowPageImgLoader.Image, WeakReference<ImageView>> mPendingRequests = new LinkedHashMap();
    private final Map<YellowPageImgLoader.Image, LinkedList<WeakReference<ImageView>>> mRequestingImageViews = new HashMap();
    private ExecutorService mExecutor = Executors.newFixedThreadPool(6);

    private static class BitmapHolder {
        Bitmap mBitmap;
        State mState;

        private BitmapHolder() {
        }
    }

    private class LoadImageRunnable implements Runnable {
        private YellowPageImgLoader.Image mImage;

        public LoadImageRunnable(YellowPageImgLoader.Image image) {
            this.mImage = image;
        }

        private void notifyBindImage(YellowPageImgLoader.Image image) {
            Message messageObtainMessage = ImageLoader.this.mMainThreadHandler.obtainMessage(1);
            messageObtainMessage.obj = image;
            ImageLoader.this.mMainThreadHandler.sendMessage(messageObtainMessage);
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (ImageLoader.this.BITMAP_CACHE_LOCK) {
                try {
                    BitmapHolder bitmapHolder = (BitmapHolder) ImageLoader.this.mBitmapCache.get(this.mImage);
                    if (bitmapHolder != null) {
                        State state = bitmapHolder.mState;
                        if (state == State.LOADING) {
                            return;
                        }
                        if (state == State.LOADED) {
                            notifyBindImage(this.mImage);
                            return;
                        }
                    }
                    if (bitmapHolder == null) {
                        bitmapHolder = new BitmapHolder();
                    }
                    bitmapHolder.mState = State.LOADING;
                    ImageLoader.this.mBitmapCache.put(this.mImage, bitmapHolder);
                    byte[] bArrLoadImage = ImageLoader.this.loadImage(this.mImage, true);
                    if (bArrLoadImage == null) {
                        ImageLoader.this.removeFailedImage(this.mImage);
                    } else {
                        ImageLoader.this.cacheBitmap(this.mImage, bArrLoadImage);
                        notifyBindImage(this.mImage);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    private enum State {
        NEEDED,
        LOADED,
        LOADING
    }

    private ImageLoader(Context context) {
        this.mMainThreadHandler = new Handler(context.getMainLooper(), this);
        this.mContext = context.getApplicationContext();
    }

    private static void acquireScreenAttr(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        sDisplayHeight = displayMetrics.heightPixels;
        sDisplayWidth = displayMetrics.widthPixels;
    }

    private State bindCachedImage(ImageView imageView, YellowPageImgLoader.Image image) {
        synchronized (this.BITMAP_CACHE_LOCK) {
            try {
                BitmapHolder bitmapHolder = this.mBitmapCache.get(image);
                if (bitmapHolder != null) {
                    State state = bitmapHolder.mState;
                    State state2 = State.LOADED;
                    if (state == state2) {
                        bindImage(imageView, bitmapHolder.mBitmap);
                        return state2;
                    }
                }
                if (bitmapHolder != null) {
                    return bitmapHolder.mState;
                }
                Log.d(TAG, "cannot get image");
                return State.NEEDED;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:16:0x0044  */
    private void bindDefaultImage(ImageView imageView, YellowPageImgLoader.Image image, Bitmap bitmap, int i2) {
        int currentUiMode = getCurrentUiMode(this.mContext);
        Object defaultImageKey = getDefaultImageKey(bitmap, i2);
        if (this.mCurrentUiMode != currentUiMode) {
            this.mDefaultBitmapCache.remove(defaultImageKey);
            this.mCurrentUiMode = currentUiMode;
        }
        Bitmap bitmapProccessImage = this.mDefaultBitmapCache.get(defaultImageKey);
        if (bitmapProccessImage == null) {
            try {
                if (bitmap != null) {
                    bitmapProccessImage = image.proccessImage(bitmap);
                } else if (i2 != 0) {
                    bitmapProccessImage = image.proccessImage(((BitmapDrawable) this.mContext.getResources().getDrawable(i2)).getBitmap());
                } else if (bitmapProccessImage != null) {
                    this.mDefaultBitmapCache.put(getDefaultImageKey(bitmap, i2), bitmapProccessImage);
                }
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
            }
            if (bitmapProccessImage != null) {
                this.mDefaultBitmapCache.put(getDefaultImageKey(bitmap, i2), bitmapProccessImage);
            }
        }
        imageView.setImageBitmap(bitmapProccessImage);
    }

    private void bindImage(ImageView imageView, Bitmap bitmap) {
        if (imageView == null || bitmap == null) {
            return;
        }
        imageView.setImageBitmap(bitmap);
    }

    private static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cacheBitmap(YellowPageImgLoader.Image image, byte[] bArr) {
        synchronized (this.BITMAP_CACHE_LOCK) {
            try {
                BitmapHolder bitmapHolder = this.mBitmapCache.get(image) == null ? new BitmapHolder() : this.mBitmapCache.remove(image);
                inflateBitmap(image, bitmapHolder, bArr);
                this.mBitmapCache.put(image, bitmapHolder);
                if (bitmapHolder.mBitmap != null) {
                    bitmapHolder.mState = State.LOADED;
                } else {
                    bitmapHolder.mState = State.NEEDED;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int i2, int i3) {
        int i4 = options.outHeight;
        int i5 = options.outWidth;
        if (i4 <= i3 && i5 <= i2) {
            return 1;
        }
        int iRound = Math.round(i4 / i3);
        int iRound2 = Math.round(i5 / i2);
        return iRound < iRound2 ? iRound : iRound2;
    }

    private Bitmap decodeBitmap(byte[] bArr) {
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
        options.inSampleSize = calculateInSampleSize(options, getDisplayWidth(), getDisplayHeight());
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        return BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
    }

    public static int getCurrentUiMode(Context context) {
        Resources resources;
        Configuration configuration;
        if (context == null || (resources = context.getApplicationContext().getResources()) == null || (configuration = resources.getConfiguration()) == null) {
            return -1;
        }
        return configuration.uiMode & 48;
    }

    private static Object getDefaultImageKey(Bitmap bitmap, int i2) {
        return bitmap != null ? bitmap : Integer.valueOf(i2);
    }

    private int getDisplayHeight() {
        if (sDisplayHeight == 0) {
            acquireScreenAttr(this.mContext);
        }
        return sDisplayHeight;
    }

    private int getDisplayWidth() {
        if (sDisplayWidth == 0) {
            acquireScreenAttr(this.mContext);
        }
        return sDisplayWidth;
    }

    public static synchronized ImageLoader getInstance(Context context) {
        try {
            if (sLoader == null) {
                sLoader = new ImageLoader(context);
            }
        } catch (Throwable th) {
            throw th;
        }
        return sLoader;
    }

    private void inflateBitmap(YellowPageImgLoader.Image image, BitmapHolder bitmapHolder, byte[] bArr) {
        try {
            if (bArr != null) {
                Bitmap bitmapDecodeBitmap = decodeBitmap(bArr);
                if (bitmapDecodeBitmap != null) {
                    bitmapHolder.mBitmap = image.proccessImage(bitmapDecodeBitmap);
                } else {
                    Log.e(TAG, "Can not decode bitmap bytes.");
                }
            } else {
                Log.e(TAG, "The holder's bytes should not be null");
            }
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
        }
    }

    private byte[] loadCachedImageBytes(YellowPageImgLoader.Image image) {
        synchronized (this.BITMAP_CACHE_LOCK) {
            try {
                BitmapHolder bitmapHolder = this.mBitmapCache.get(image);
                if (bitmapHolder == null || bitmapHolder.mState != State.LOADED) {
                    return null;
                }
                return bitmapToByteArray(bitmapHolder.mBitmap);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void onRequestLoad(Message message) {
        ImageView imageView;
        Object obj = message.obj;
        if (obj == null || (imageView = (ImageView) ((WeakReference) obj).get()) == null) {
            return;
        }
        requestLoading(imageView);
    }

    private void onRequestLoaded(Message message) {
        Object obj = message.obj;
        if (obj == null) {
            return;
        }
        YellowPageImgLoader.Image image = (YellowPageImgLoader.Image) obj;
        synchronized (this.BITMAP_CACHE_LOCK) {
            try {
                LinkedList<WeakReference<ImageView>> linkedList = this.mRequestingImageViews.get(image);
                Iterator<WeakReference<ImageView>> it = linkedList.iterator();
                while (it.hasNext()) {
                    ImageView imageView = it.next().get();
                    if (imageView != null && !this.mPendingRequests.containsKey(image) && image.equals(imageView.getTag())) {
                        BitmapHolder bitmapHolder = this.mBitmapCache.get(image);
                        if (bitmapHolder == null || bitmapHolder.mState != State.LOADED) {
                            Log.d(TAG, "handleMessage:image " + image + " was garbage collected");
                            Message messageObtain = Message.obtain();
                            messageObtain.what = 2;
                            messageObtain.obj = new WeakReference(imageView);
                            this.mMainThreadHandler.sendMessage(messageObtain);
                        } else {
                            bindImage(imageView, bitmapHolder.mBitmap);
                            Log.d(TAG, "handleMessage:ImageView with image " + image + " bound");
                        }
                    }
                }
                linkedList.clear();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeFailedImage(YellowPageImgLoader.Image image) {
        synchronized (this.BITMAP_CACHE_LOCK) {
            this.mBitmapCache.remove(image);
        }
    }

    private boolean requestLoading(ImageView imageView) {
        if (this.mPauseLoading || !(imageView.getTag() instanceof YellowPageImgLoader.Image)) {
            return false;
        }
        YellowPageImgLoader.Image image = (YellowPageImgLoader.Image) imageView.getTag();
        LinkedList<WeakReference<ImageView>> linkedList = this.mRequestingImageViews.get(image);
        if (linkedList == null) {
            linkedList = new LinkedList<>();
            this.mRequestingImageViews.put(image, linkedList);
        }
        linkedList.add(new WeakReference<>(imageView));
        this.mExecutor.execute(new LoadImageRunnable(image));
        return true;
    }

    public void cancelRequest(ImageView imageView) {
        if (imageView != null) {
            imageView.setTag(null);
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        int i2 = message.what;
        if (i2 == 1) {
            onRequestLoaded(message);
            return false;
        }
        if (i2 != 2) {
            return false;
        }
        onRequestLoad(message);
        return false;
    }

    public void loadImage(ImageView imageView, YellowPageImgLoader.Image image, int i2) {
        loadImage(imageView, image, null, i2);
    }

    public Bitmap loadImageBitmap(YellowPageImgLoader.Image image, boolean z2) {
        return decodeBitmap(loadImageBytes(image, z2));
    }

    public byte[] loadImageBytes(YellowPageImgLoader.Image image, boolean z2) {
        return loadImageBytes(image, z2, 0, 0);
    }

    public void pauseLoading() {
        this.mPauseLoading = true;
    }

    public void resumeLoading() {
        this.mPauseLoading = false;
        Iterator<YellowPageImgLoader.Image> it = this.mPendingRequests.keySet().iterator();
        while (it.hasNext()) {
            ImageView imageView = this.mPendingRequests.get(it.next()).get();
            if (imageView == null) {
                it.remove();
            } else if (requestLoading(imageView)) {
                it.remove();
            }
        }
    }

    public void loadImage(ImageView imageView, YellowPageImgLoader.Image image, Bitmap bitmap) {
        loadImage(imageView, image, bitmap, 0);
    }

    public byte[] loadImageBytes(YellowPageImgLoader.Image image, boolean z2, int i2, int i3) throws Throwable {
        byte[] bArrLoadCachedImageBytes = loadCachedImageBytes(image);
        if (bArrLoadCachedImageBytes != null) {
            return bArrLoadCachedImageBytes;
        }
        byte[] bArrLoadImage = loadImage(image, z2, i3);
        if (bArrLoadImage != null) {
            cacheBitmap(image, bArrLoadImage);
            return bArrLoadImage;
        }
        if (i2 == 0) {
            return bArrLoadImage;
        }
        Log.v(TAG, "failed to load image, return default res");
        return bitmapToByteArray(((BitmapDrawable) this.mContext.getResources().getDrawable(i2)).getBitmap());
    }

    private void loadImage(ImageView imageView, YellowPageImgLoader.Image image, Bitmap bitmap, int i2) {
        if (image != null && image.isValid()) {
            imageView.setTag(image);
            if (bindCachedImage(imageView, image) == State.LOADED) {
                this.mPendingRequests.remove(image);
                return;
            }
            bindDefaultImage(imageView, image, bitmap, i2);
            this.mPendingRequests.put(image, new WeakReference<>(imageView));
            if (requestLoading(imageView)) {
                this.mPendingRequests.remove(image);
                return;
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("loadImage: invalid image : ");
        sb.append(image == null ? null : image.getUrl());
        Log.e(TAG, sb.toString());
        this.mPendingRequests.remove(image);
        imageView.setTag(null);
        bindDefaultImage(imageView, image, bitmap, i2);
    }

    public static synchronized ImageLoader getInstance(Context context, int i2) {
        ImageLoader imageLoader;
        try {
            if (sLoader == null) {
                sLoader = new ImageLoader(context);
            }
            imageLoader = sLoader;
            imageLoader.mNetworkAccess = i2;
        } catch (Throwable th) {
            throw th;
        }
        return imageLoader;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] loadImage(YellowPageImgLoader.Image image, boolean z2) {
        return loadImage(image, z2, 0);
    }

    /* JADX INFO: Removed unreachable split cross block B:349:0x02c0 */
    /* JADX WARN: Code duplicated, block: B:194:0x025a A[Catch: IOException -> 0x020e, TRY_ENTER, TRY_LEAVE, TryCatch #15 {IOException -> 0x020e, blocks: (B:194:0x025a, B:218:0x028c, B:160:0x0209, B:242:0x02bd), top: B:279:0x000c }] */
    /* JADX WARN: Code duplicated, block: B:218:0x028c A[Catch: IOException -> 0x020e, TRY_ENTER, TRY_LEAVE, TryCatch #15 {IOException -> 0x020e, blocks: (B:194:0x025a, B:218:0x028c, B:160:0x0209, B:242:0x02bd), top: B:279:0x000c }] */
    /* JADX WARN: Code duplicated, block: B:242:0x02bd A[Catch: IOException -> 0x020e, TRY_ENTER, TRY_LEAVE, TryCatch #15 {IOException -> 0x020e, blocks: (B:194:0x025a, B:218:0x028c, B:160:0x0209, B:242:0x02bd), top: B:279:0x000c }] */
    /* JADX WARN: Code duplicated, block: B:275:0x026e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:277:0x02eb A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:283:0x0201 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:287:0x0282 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:289:0x01f7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:291:0x0278 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:293:0x0295 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:297:0x029f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:301:0x02b3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:303:0x0232 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:307:0x02a9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:311:0x0246 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:313:0x02c3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:323:0x023c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:327:0x0250 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:329:0x02cd A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:337:0x01e5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:341:0x0264 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:343:0x02e1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:347:0x02d7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:360:? A[SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Not initialized variable reg: 5, insn: 0x00ed: MOVE (r2 I:??[OBJECT, ARRAY]) = (r5 I:??[OBJECT, ARRAY]), block:B:59:0x00ed */
    /* JADX WARN: Type inference failed for: r14v10 */
    /* JADX WARN: Type inference failed for: r14v17 */
    /* JADX WARN: Type inference failed for: r14v2, types: [android.content.res.AssetFileDescriptor] */
    /* JADX WARN: Type inference failed for: r14v3 */
    /* JADX WARN: Type inference failed for: r14v35 */
    /* JADX WARN: Type inference failed for: r14v36 */
    /* JADX WARN: Type inference failed for: r14v37 */
    /* JADX WARN: Type inference failed for: r15v1, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r15v2 */
    /* JADX WARN: Type inference failed for: r15v27 */
    /* JADX WARN: Type inference failed for: r15v36 */
    /* JADX WARN: Type inference failed for: r15v37 */
    /* JADX WARN: Type inference failed for: r15v38 */
    /* JADX WARN: Type inference failed for: r15v9 */
    /* JADX WARN: Type inference failed for: r1v1, types: [android.content.res.AssetFileDescriptor] */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7 */
    /* JADX WARN: Type inference failed for: r1v8 */
    /* JADX WARN: Type inference failed for: r1v9 */
    private byte[] loadImage(YellowPageImgLoader.Image image, boolean z2, int i2) throws Throwable {
        OutputStream outputStream;
        ?? r15;
        ?? r14;
        ByteArrayOutputStream byteArrayOutputStream;
        ?? r1;
        AssetFileDescriptor assetFileDescriptorOpenAssetFileDescriptor;
        FileInputStream fileInputStreamCreateInputStream;
        FileOutputStream fileOutputStreamCreateOutputStream;
        AssetFileDescriptor assetFileDescriptor;
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        String url = image.getUrl();
        boolean zIsEmpty = TextUtils.isEmpty(url);
        OutputStream outputStream2 = null;
        if (zIsEmpty) {
            return null;
        }
        try {
            try {
                try {
                    AssetFileDescriptor assetFileDescriptorOpenAssetFileDescriptor2 = this.mContext.getContentResolver().openAssetFileDescriptor(Uri.withAppendedPath(YellowPageContract.ImageLookup.CONTENT_URI_IMAGE, image.getName()), MiCloudSmsCmd.TYPE_WIPE);
                    try {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        try {
                            try {
                                if (assetFileDescriptorOpenAssetFileDescriptor2.getLength() == 0 && z2) {
                                    try {
                                        Uri.Builder builderBuildUpon = Uri.withAppendedPath(YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_CLOUD, URLEncoder.encode(url)).buildUpon();
                                        builderBuildUpon.appendQueryParameter("timeout", String.valueOf(i2));
                                        builderBuildUpon.appendQueryParameter("overwrite_network_access", String.valueOf(this.mNetworkAccess));
                                        assetFileDescriptorOpenAssetFileDescriptor = this.mContext.getContentResolver().openAssetFileDescriptor(builderBuildUpon.build(), "r");
                                        if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                            try {
                                                try {
                                                    if (assetFileDescriptorOpenAssetFileDescriptor.getLength() != 0) {
                                                        fileInputStreamCreateInputStream = assetFileDescriptorOpenAssetFileDescriptor.createInputStream();
                                                        try {
                                                            byte[] bArr = new byte[4096];
                                                            while (true) {
                                                                int i3 = fileInputStreamCreateInputStream.read(bArr);
                                                                if (i3 == -1) {
                                                                    break;
                                                                }
                                                                byteArrayOutputStream.write(bArr, 0, i3);
                                                            }
                                                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                                                            BitmapFactory.Options options = new BitmapFactory.Options();
                                                            options.inJustDecodeBounds = true;
                                                            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
                                                            if (!TextUtils.isEmpty(options.outMimeType) && options.outMimeType.startsWith(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                                                                if (byteArray.length > 0) {
                                                                    fileOutputStreamCreateOutputStream = assetFileDescriptorOpenAssetFileDescriptor2.createOutputStream();
                                                                    try {
                                                                        try {
                                                                            fileOutputStreamCreateOutputStream.write(byteArray);
                                                                            fileOutputStreamCreateOutputStream.flush();
                                                                            try {
                                                                                fileOutputStreamCreateOutputStream.close();
                                                                            } catch (IOException e2) {
                                                                                e2.printStackTrace();
                                                                            }
                                                                            try {
                                                                                byteArrayOutputStream.close();
                                                                            } catch (IOException e3) {
                                                                                e3.printStackTrace();
                                                                            }
                                                                            try {
                                                                                fileInputStreamCreateInputStream.close();
                                                                            } catch (IOException e4) {
                                                                                e4.printStackTrace();
                                                                            }
                                                                            try {
                                                                                assetFileDescriptorOpenAssetFileDescriptor.close();
                                                                            } catch (IOException e5) {
                                                                                e5.printStackTrace();
                                                                            }
                                                                            try {
                                                                                assetFileDescriptorOpenAssetFileDescriptor2.close();
                                                                            } catch (IOException e6) {
                                                                                e6.printStackTrace();
                                                                            }
                                                                            return byteArray;
                                                                        } catch (FileNotFoundException e7) {
                                                                            e = e7;
                                                                            e.printStackTrace();
                                                                            this.mContext.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_CLOUD, Uri.encode(url)).buildUpon().appendQueryParameter("fileName", image.getName()).build(), null, null, null, null);
                                                                            fileOutputStream = fileOutputStreamCreateOutputStream;
                                                                            assetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                                                            fileInputStream = fileInputStreamCreateInputStream;
                                                                            if (fileOutputStream != null) {
                                                                                fileInputStream = fileInputStreamCreateInputStream;
                                                                                assetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                                                                fileInputStream = fileInputStreamCreateInputStream;
                                                                                fileOutputStream.close();
                                                                            }
                                                                            fileInputStream = fileInputStreamCreateInputStream;
                                                                            assetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                                                            fileInputStream = fileInputStreamCreateInputStream;
                                                                            byteArrayOutputStream.close();
                                                                            if (fileInputStream != null) {
                                                                                fileInputStream.close();
                                                                            }
                                                                            if (assetFileDescriptor != null) {
                                                                                assetFileDescriptor.close();
                                                                            }
                                                                            assetFileDescriptorOpenAssetFileDescriptor2.close();
                                                                            return null;
                                                                        }
                                                                    } catch (IOException e8) {
                                                                        e = e8;
                                                                        e.printStackTrace();
                                                                        if (fileOutputStreamCreateOutputStream != null) {
                                                                            fileOutputStreamCreateOutputStream.close();
                                                                        }
                                                                        if (byteArrayOutputStream != null) {
                                                                            byteArrayOutputStream.close();
                                                                        }
                                                                        if (fileInputStreamCreateInputStream != null) {
                                                                            fileInputStreamCreateInputStream.close();
                                                                        }
                                                                        if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                                                            assetFileDescriptorOpenAssetFileDescriptor.close();
                                                                        }
                                                                        if (assetFileDescriptorOpenAssetFileDescriptor2 != null) {
                                                                            assetFileDescriptorOpenAssetFileDescriptor2.close();
                                                                        }
                                                                        return null;
                                                                    } catch (Exception e9) {
                                                                        e = e9;
                                                                        e.printStackTrace();
                                                                        if (fileOutputStreamCreateOutputStream != null) {
                                                                            fileOutputStreamCreateOutputStream.close();
                                                                        }
                                                                        if (byteArrayOutputStream != null) {
                                                                            byteArrayOutputStream.close();
                                                                        }
                                                                        if (fileInputStreamCreateInputStream != null) {
                                                                            fileInputStreamCreateInputStream.close();
                                                                        }
                                                                        if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                                                            assetFileDescriptorOpenAssetFileDescriptor.close();
                                                                        }
                                                                        if (assetFileDescriptorOpenAssetFileDescriptor2 != null) {
                                                                            assetFileDescriptorOpenAssetFileDescriptor2.close();
                                                                        }
                                                                        return null;
                                                                    }
                                                                }
                                                                fileOutputStream = null;
                                                                if (fileOutputStream != null) {
                                                                    fileInputStream = fileInputStreamCreateInputStream;
                                                                    assetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                                                    fileInputStream = fileInputStreamCreateInputStream;
                                                                    fileOutputStream.close();
                                                                }
                                                                fileInputStream = fileInputStreamCreateInputStream;
                                                                assetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                                                fileInputStream = fileInputStreamCreateInputStream;
                                                                byteArrayOutputStream.close();
                                                                if (fileInputStream != null) {
                                                                    fileInputStream.close();
                                                                }
                                                                if (assetFileDescriptor != null) {
                                                                    assetFileDescriptor.close();
                                                                }
                                                                assetFileDescriptorOpenAssetFileDescriptor2.close();
                                                            } else {
                                                                throw new IOException("Invalid mime type [" + options.outMimeType + "]");
                                                            }
                                                        } catch (FileNotFoundException e10) {
                                                            e = e10;
                                                            fileOutputStreamCreateOutputStream = null;
                                                        } catch (IOException e11) {
                                                            e = e11;
                                                            fileOutputStreamCreateOutputStream = null;
                                                            e.printStackTrace();
                                                            if (fileOutputStreamCreateOutputStream != null) {
                                                                fileOutputStreamCreateOutputStream.close();
                                                            }
                                                            if (byteArrayOutputStream != null) {
                                                                byteArrayOutputStream.close();
                                                            }
                                                            if (fileInputStreamCreateInputStream != null) {
                                                                fileInputStreamCreateInputStream.close();
                                                            }
                                                            if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                                                assetFileDescriptorOpenAssetFileDescriptor.close();
                                                            }
                                                            if (assetFileDescriptorOpenAssetFileDescriptor2 != null) {
                                                                assetFileDescriptorOpenAssetFileDescriptor2.close();
                                                            }
                                                            return null;
                                                        } catch (Exception e12) {
                                                            e = e12;
                                                            fileOutputStreamCreateOutputStream = null;
                                                            e.printStackTrace();
                                                            if (fileOutputStreamCreateOutputStream != null) {
                                                                fileOutputStreamCreateOutputStream.close();
                                                            }
                                                            if (byteArrayOutputStream != null) {
                                                                byteArrayOutputStream.close();
                                                            }
                                                            if (fileInputStreamCreateInputStream != null) {
                                                                fileInputStreamCreateInputStream.close();
                                                            }
                                                            if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                                                assetFileDescriptorOpenAssetFileDescriptor.close();
                                                            }
                                                            if (assetFileDescriptorOpenAssetFileDescriptor2 != null) {
                                                                assetFileDescriptorOpenAssetFileDescriptor2.close();
                                                            }
                                                            return null;
                                                        } catch (Throwable th) {
                                                            th = th;
                                                            r1 = assetFileDescriptorOpenAssetFileDescriptor2;
                                                            r14 = assetFileDescriptorOpenAssetFileDescriptor;
                                                            r15 = fileInputStreamCreateInputStream;
                                                            if (outputStream2 != null) {
                                                                outputStream2.close();
                                                            }
                                                            if (byteArrayOutputStream != null) {
                                                                byteArrayOutputStream.close();
                                                            }
                                                            if (r15 != 0) {
                                                                r15.close();
                                                            }
                                                            if (r14 != 0) {
                                                                r14.close();
                                                            }
                                                            if (r1 != 0) {
                                                                r1.close();
                                                                throw th;
                                                            }
                                                            throw th;
                                                        }
                                                    }
                                                } catch (FileNotFoundException e13) {
                                                    e = e13;
                                                    fileInputStreamCreateInputStream = null;
                                                    assetFileDescriptorOpenAssetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                                    fileOutputStreamCreateOutputStream = fileInputStreamCreateInputStream;
                                                } catch (IOException e14) {
                                                    e = e14;
                                                    fileInputStreamCreateInputStream = null;
                                                    assetFileDescriptorOpenAssetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                                    fileOutputStreamCreateOutputStream = fileInputStreamCreateInputStream;
                                                    e.printStackTrace();
                                                    if (fileOutputStreamCreateOutputStream != null) {
                                                        fileOutputStreamCreateOutputStream.close();
                                                    }
                                                    if (byteArrayOutputStream != null) {
                                                        byteArrayOutputStream.close();
                                                    }
                                                    if (fileInputStreamCreateInputStream != null) {
                                                        fileInputStreamCreateInputStream.close();
                                                    }
                                                    if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                                        assetFileDescriptorOpenAssetFileDescriptor.close();
                                                    }
                                                    if (assetFileDescriptorOpenAssetFileDescriptor2 != null) {
                                                        assetFileDescriptorOpenAssetFileDescriptor2.close();
                                                    }
                                                    return null;
                                                } catch (Exception e15) {
                                                    e = e15;
                                                    fileInputStreamCreateInputStream = null;
                                                    assetFileDescriptorOpenAssetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                                    fileOutputStreamCreateOutputStream = fileInputStreamCreateInputStream;
                                                    e.printStackTrace();
                                                    if (fileOutputStreamCreateOutputStream != null) {
                                                        fileOutputStreamCreateOutputStream.close();
                                                    }
                                                    if (byteArrayOutputStream != null) {
                                                        byteArrayOutputStream.close();
                                                    }
                                                    if (fileInputStreamCreateInputStream != null) {
                                                        fileInputStreamCreateInputStream.close();
                                                    }
                                                    if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                                        assetFileDescriptorOpenAssetFileDescriptor.close();
                                                    }
                                                    if (assetFileDescriptorOpenAssetFileDescriptor2 != null) {
                                                        assetFileDescriptorOpenAssetFileDescriptor2.close();
                                                    }
                                                    return null;
                                                } catch (Throwable th2) {
                                                    th = th2;
                                                    r15 = 0;
                                                    r1 = assetFileDescriptorOpenAssetFileDescriptor2;
                                                    r14 = assetFileDescriptorOpenAssetFileDescriptor;
                                                }
                                                e.printStackTrace();
                                                this.mContext.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_CLOUD, Uri.encode(url)).buildUpon().appendQueryParameter("fileName", image.getName()).build(), null, null, null, null);
                                                fileOutputStream = fileOutputStreamCreateOutputStream;
                                                assetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                                fileInputStream = fileInputStreamCreateInputStream;
                                                if (fileOutputStream != null) {
                                                    fileInputStream = fileInputStreamCreateInputStream;
                                                    assetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                                    fileInputStream = fileInputStreamCreateInputStream;
                                                    fileOutputStream.close();
                                                }
                                                fileInputStream = fileInputStreamCreateInputStream;
                                                assetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                                fileInputStream = fileInputStreamCreateInputStream;
                                                byteArrayOutputStream.close();
                                                if (fileInputStream != null) {
                                                    fileInputStream.close();
                                                }
                                                if (assetFileDescriptor != null) {
                                                    assetFileDescriptor.close();
                                                }
                                                assetFileDescriptorOpenAssetFileDescriptor2.close();
                                            } catch (FileNotFoundException e16) {
                                                e = e16;
                                                e.printStackTrace();
                                                if (fileOutputStreamCreateOutputStream != null) {
                                                    fileOutputStreamCreateOutputStream.close();
                                                }
                                                if (byteArrayOutputStream != null) {
                                                    byteArrayOutputStream.close();
                                                }
                                                if (fileInputStreamCreateInputStream != null) {
                                                    fileInputStreamCreateInputStream.close();
                                                }
                                                if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                                    assetFileDescriptorOpenAssetFileDescriptor.close();
                                                }
                                                if (assetFileDescriptorOpenAssetFileDescriptor2 != null) {
                                                    assetFileDescriptorOpenAssetFileDescriptor2.close();
                                                }
                                                return null;
                                            }
                                        }
                                        try {
                                            byteArrayOutputStream.close();
                                        } catch (IOException e17) {
                                            e17.printStackTrace();
                                        }
                                        if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                            try {
                                                assetFileDescriptorOpenAssetFileDescriptor.close();
                                            } catch (IOException e18) {
                                                e18.printStackTrace();
                                            }
                                        }
                                        try {
                                            assetFileDescriptorOpenAssetFileDescriptor2.close();
                                        } catch (IOException e19) {
                                            e19.printStackTrace();
                                        }
                                        return null;
                                    } catch (FileNotFoundException e20) {
                                        e = e20;
                                        assetFileDescriptorOpenAssetFileDescriptor = null;
                                        fileInputStreamCreateInputStream = null;
                                    }
                                } else {
                                    if (assetFileDescriptorOpenAssetFileDescriptor2.getLength() > 0) {
                                        fileInputStreamCreateInputStream = assetFileDescriptorOpenAssetFileDescriptor2.createInputStream();
                                        try {
                                            byte[] bArr2 = new byte[4096];
                                            while (true) {
                                                int i4 = fileInputStreamCreateInputStream.read(bArr2);
                                                if (i4 == -1) {
                                                    break;
                                                }
                                                byteArrayOutputStream.write(bArr2, 0, i4);
                                            }
                                            if (byteArrayOutputStream.size() > 0) {
                                                byte[] byteArray2 = byteArrayOutputStream.toByteArray();
                                                try {
                                                    byteArrayOutputStream.close();
                                                } catch (IOException e21) {
                                                    e21.printStackTrace();
                                                }
                                                try {
                                                    fileInputStreamCreateInputStream.close();
                                                } catch (IOException e22) {
                                                    e22.printStackTrace();
                                                }
                                                try {
                                                    assetFileDescriptorOpenAssetFileDescriptor2.close();
                                                } catch (IOException e23) {
                                                    e23.printStackTrace();
                                                }
                                                return byteArray2;
                                            }
                                            assetFileDescriptor = null;
                                            fileOutputStream = null;
                                        } catch (FileNotFoundException e24) {
                                            e = e24;
                                            assetFileDescriptorOpenAssetFileDescriptor = null;
                                            fileOutputStreamCreateOutputStream = null;
                                            e.printStackTrace();
                                            if (fileOutputStreamCreateOutputStream != null) {
                                                try {
                                                    fileOutputStreamCreateOutputStream.close();
                                                } catch (IOException e25) {
                                                    e25.printStackTrace();
                                                }
                                            }
                                            if (byteArrayOutputStream != null) {
                                                try {
                                                    byteArrayOutputStream.close();
                                                } catch (IOException e26) {
                                                    e26.printStackTrace();
                                                }
                                            }
                                            if (fileInputStreamCreateInputStream != null) {
                                                try {
                                                    fileInputStreamCreateInputStream.close();
                                                } catch (IOException e27) {
                                                    e27.printStackTrace();
                                                }
                                            }
                                            if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                                try {
                                                    assetFileDescriptorOpenAssetFileDescriptor.close();
                                                } catch (IOException e28) {
                                                    e28.printStackTrace();
                                                }
                                            }
                                            if (assetFileDescriptorOpenAssetFileDescriptor2 != null) {
                                                assetFileDescriptorOpenAssetFileDescriptor2.close();
                                            }
                                            return null;
                                        } catch (IOException e29) {
                                            e = e29;
                                            assetFileDescriptorOpenAssetFileDescriptor = null;
                                            fileOutputStreamCreateOutputStream = null;
                                            e.printStackTrace();
                                            if (fileOutputStreamCreateOutputStream != null) {
                                                try {
                                                    fileOutputStreamCreateOutputStream.close();
                                                } catch (IOException e30) {
                                                    e30.printStackTrace();
                                                }
                                            }
                                            if (byteArrayOutputStream != null) {
                                                try {
                                                    byteArrayOutputStream.close();
                                                } catch (IOException e31) {
                                                    e31.printStackTrace();
                                                }
                                            }
                                            if (fileInputStreamCreateInputStream != null) {
                                                try {
                                                    fileInputStreamCreateInputStream.close();
                                                } catch (IOException e32) {
                                                    e32.printStackTrace();
                                                }
                                            }
                                            if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                                try {
                                                    assetFileDescriptorOpenAssetFileDescriptor.close();
                                                } catch (IOException e33) {
                                                    e33.printStackTrace();
                                                }
                                            }
                                            if (assetFileDescriptorOpenAssetFileDescriptor2 != null) {
                                                assetFileDescriptorOpenAssetFileDescriptor2.close();
                                            }
                                            return null;
                                        } catch (Exception e34) {
                                            e = e34;
                                            assetFileDescriptorOpenAssetFileDescriptor = null;
                                            fileOutputStreamCreateOutputStream = null;
                                            e.printStackTrace();
                                            if (fileOutputStreamCreateOutputStream != null) {
                                                try {
                                                    fileOutputStreamCreateOutputStream.close();
                                                } catch (IOException e35) {
                                                    e35.printStackTrace();
                                                }
                                            }
                                            if (byteArrayOutputStream != null) {
                                                try {
                                                    byteArrayOutputStream.close();
                                                } catch (IOException e36) {
                                                    e36.printStackTrace();
                                                }
                                            }
                                            if (fileInputStreamCreateInputStream != null) {
                                                try {
                                                    fileInputStreamCreateInputStream.close();
                                                } catch (IOException e37) {
                                                    e37.printStackTrace();
                                                }
                                            }
                                            if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                                                try {
                                                    assetFileDescriptorOpenAssetFileDescriptor.close();
                                                } catch (IOException e38) {
                                                    e38.printStackTrace();
                                                }
                                            }
                                            if (assetFileDescriptorOpenAssetFileDescriptor2 != null) {
                                                assetFileDescriptorOpenAssetFileDescriptor2.close();
                                            }
                                            return null;
                                        } catch (Throwable th3) {
                                            th = th3;
                                            r14 = 0;
                                            r1 = assetFileDescriptorOpenAssetFileDescriptor2;
                                            r15 = fileInputStreamCreateInputStream;
                                            if (outputStream2 != null) {
                                                try {
                                                    outputStream2.close();
                                                } catch (IOException e39) {
                                                    e39.printStackTrace();
                                                }
                                            }
                                            if (byteArrayOutputStream != null) {
                                                try {
                                                    byteArrayOutputStream.close();
                                                } catch (IOException e40) {
                                                    e40.printStackTrace();
                                                }
                                            }
                                            if (r15 != 0) {
                                                try {
                                                    r15.close();
                                                } catch (IOException e41) {
                                                    e41.printStackTrace();
                                                }
                                            }
                                            if (r14 != 0) {
                                                try {
                                                    r14.close();
                                                } catch (IOException e42) {
                                                    e42.printStackTrace();
                                                }
                                            }
                                            if (r1 != 0) {
                                                try {
                                                    r1.close();
                                                    throw th;
                                                } catch (IOException e43) {
                                                    e43.printStackTrace();
                                                    throw th;
                                                }
                                            }
                                            throw th;
                                        }
                                    } else {
                                        assetFileDescriptor = null;
                                        fileInputStream = null;
                                        fileOutputStream = null;
                                    }
                                    if (fileOutputStream != null) {
                                        try {
                                            fileInputStream = fileInputStreamCreateInputStream;
                                            assetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                            fileInputStream = fileInputStreamCreateInputStream;
                                            fileOutputStream.close();
                                        } catch (IOException e44) {
                                            e44.printStackTrace();
                                        }
                                    }
                                    try {
                                        fileInputStream = fileInputStreamCreateInputStream;
                                        assetFileDescriptor = assetFileDescriptorOpenAssetFileDescriptor;
                                        fileInputStream = fileInputStreamCreateInputStream;
                                        byteArrayOutputStream.close();
                                    } catch (IOException e45) {
                                        e45.printStackTrace();
                                    }
                                    if (fileInputStream != null) {
                                        try {
                                            fileInputStream.close();
                                        } catch (IOException e46) {
                                            e46.printStackTrace();
                                        }
                                    }
                                    if (assetFileDescriptor != null) {
                                        try {
                                            assetFileDescriptor.close();
                                        } catch (IOException e47) {
                                            e47.printStackTrace();
                                        }
                                    }
                                    assetFileDescriptorOpenAssetFileDescriptor2.close();
                                }
                            } catch (FileNotFoundException e48) {
                                e = e48;
                                assetFileDescriptorOpenAssetFileDescriptor = null;
                                fileInputStreamCreateInputStream = null;
                                fileOutputStreamCreateOutputStream = null;
                            }
                        } catch (IOException e49) {
                            e = e49;
                            assetFileDescriptorOpenAssetFileDescriptor = null;
                            fileInputStreamCreateInputStream = null;
                        } catch (Exception e50) {
                            e = e50;
                            assetFileDescriptorOpenAssetFileDescriptor = null;
                            fileInputStreamCreateInputStream = null;
                        } catch (Throwable th4) {
                            th = th4;
                            r14 = 0;
                            r15 = 0;
                            r1 = assetFileDescriptorOpenAssetFileDescriptor2;
                        }
                    } catch (FileNotFoundException e51) {
                        e = e51;
                        assetFileDescriptorOpenAssetFileDescriptor = null;
                        fileInputStreamCreateInputStream = null;
                        byteArrayOutputStream = null;
                        fileOutputStreamCreateOutputStream = null;
                    } catch (IOException e52) {
                        e = e52;
                        assetFileDescriptorOpenAssetFileDescriptor = null;
                        fileInputStreamCreateInputStream = null;
                        byteArrayOutputStream = null;
                        fileOutputStreamCreateOutputStream = null;
                    } catch (Exception e53) {
                        e = e53;
                        assetFileDescriptorOpenAssetFileDescriptor = null;
                        fileInputStreamCreateInputStream = null;
                        byteArrayOutputStream = null;
                        fileOutputStreamCreateOutputStream = null;
                    } catch (Throwable th5) {
                        th = th5;
                        r14 = 0;
                        r15 = 0;
                        byteArrayOutputStream = null;
                        r1 = assetFileDescriptorOpenAssetFileDescriptor2;
                    }
                } catch (Exception e54) {
                    Log.e(TAG, "The yellowpage provider's image can not be written now", e54);
                    return null;
                }
            } catch (IOException e55) {
                e55.printStackTrace();
            }
            return null;
        } catch (Throwable th6) {
            th = th6;
            outputStream2 = outputStream;
            r1 = zIsEmpty;
            r14 = z2;
            r15 = i2;
        }
    }
}
