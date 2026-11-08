package com.xiaomi.push.sweetnotification;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import com.xiaomi.channel.commonutils.android.ToolUtils;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Stack;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: classes.dex */
class SweetTagHandler implements Html.TagHandler {
    private static final String COLOR_VALUE = "color";
    private static final String CUSTOM_TAG = "ft";
    private static final String SIZE_VALUE = "size";
    private static Context mContext;
    private Stack<Properties> propertyValues;
    private Stack<Integer> startIndexs;

    private static class Properties {
        String color;
        String size;

        public Properties(String str, String str2) {
            this.color = str;
            this.size = str2;
        }
    }

    protected SweetTagHandler(Context context) {
        mContext = context;
    }

    static int convertFontSize(int i2) {
        float dimension;
        Context context = mContext;
        if (context == null) {
            return i2;
        }
        try {
            Resources resources = context.getResources();
            dimension = resources.getDimension(resources.getIdentifier("sweet_font_scale", "dimen", mContext.getPackageName()));
        } catch (Throwable unused) {
            dimension = mContext.getResources().getDisplayMetrics().density;
        }
        return Math.round(dimension * i2);
    }

    private String getProperty(XMLReader xMLReader, String str) {
        try {
            Field declaredField = xMLReader.getClass().getDeclaredField("theNewElement");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(xMLReader);
            Field declaredField2 = obj.getClass().getDeclaredField("theAtts");
            declaredField2.setAccessible(true);
            Object obj2 = declaredField2.get(obj);
            Field declaredField3 = obj2.getClass().getDeclaredField("data");
            declaredField3.setAccessible(true);
            String[] strArr = (String[]) declaredField3.get(obj2);
            Field declaredField4 = obj2.getClass().getDeclaredField("length");
            declaredField4.setAccessible(true);
            int iIntValue = ((Integer) declaredField4.get(obj2)).intValue();
            for (int i2 = 0; i2 < iIntValue; i2++) {
                int i3 = i2 * 5;
                if (str.equals(strArr[i3 + 1])) {
                    return strArr[i3 + 4];
                }
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    private void handlerEndSIZE(Editable editable) {
        if (isEmpty(this.propertyValues)) {
            return;
        }
        Properties propertiesPop = this.propertyValues.pop();
        int iIntValue = this.startIndexs.pop().intValue();
        String str = propertiesPop.size;
        if (str != null) {
            try {
                editable.setSpan(new AbsoluteSizeSpan(convertFontSize(ToolUtils.parseInt(str, 12)), false), iIntValue, editable.length(), 33);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        String str2 = propertiesPop.color;
        if (str2 != null) {
            try {
                editable.setSpan(new ForegroundColorSpan(Color.parseColor(str2)), iIntValue, editable.length(), 33);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }

    private void handlerStartSIZE(Editable editable, XMLReader xMLReader) {
        if (this.startIndexs == null) {
            this.startIndexs = new Stack<>();
        }
        this.startIndexs.push(Integer.valueOf(editable.length()));
        if (this.propertyValues == null) {
            this.propertyValues = new Stack<>();
        }
        this.propertyValues.push(new Properties(getProperty(xMLReader, COLOR_VALUE), getProperty(xMLReader, SIZE_VALUE)));
    }

    static Spanned hideEmptyStr(Spanned spanned) {
        SpannableString spannableString = new SpannableString(spanned);
        int length = spanned.length();
        for (int i2 = 0; i2 < length; i2++) {
            if (spanned.charAt(i2) <= ' ') {
                spannableString.setSpan(new AbsoluteSizeSpan(0, false), i2, i2 + 1, 33);
            }
        }
        return spannableString;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    @Override // android.text.Html.TagHandler
    public void handleTag(boolean z2, String str, Editable editable, XMLReader xMLReader) {
        if (CUSTOM_TAG.equalsIgnoreCase(str)) {
            if (z2) {
                handlerStartSIZE(editable, xMLReader);
            } else {
                handlerEndSIZE(editable);
            }
        }
    }
}
