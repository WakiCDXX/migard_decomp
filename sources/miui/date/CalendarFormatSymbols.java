package miui.date;

import android.content.res.Resources;
import com.miui.internal.app.SystemApplication;
import java.util.Locale;
import miui.util.SoftReferenceSingleton;
import q.a;

/* JADX INFO: loaded from: classes.dex */
public class CalendarFormatSymbols {
    private static final SoftReferenceSingleton<CalendarFormatSymbols> INSTANCE = new SoftReferenceSingleton<CalendarFormatSymbols>() { // from class: miui.date.CalendarFormatSymbols.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // miui.util.SoftReferenceSingleton
        public CalendarFormatSymbols createInstance() {
            return new CalendarFormatSymbols();
        }
    };
    private Resources mResources = SystemApplication.getCurrentApplication().getResources();

    public static CalendarFormatSymbols getDefault() {
        return INSTANCE.get();
    }

    public String[] getAmPms() {
        return this.mResources.getStringArray(a.f2169a);
    }

    public String[] getChineseDays() {
        return this.mResources.getStringArray(a.f2170b);
    }

    public String[] getChineseDigits() {
        return this.mResources.getStringArray(a.f2171c);
    }

    public String[] getChineseLeapMonths() {
        return this.mResources.getStringArray(a.f2172d);
    }

    public String[] getChineseMonths() {
        return this.mResources.getStringArray(a.f2173e);
    }

    public String[] getChineseSymbolAnimals() {
        return this.mResources.getStringArray(a.f2174f);
    }

    public String[] getDetailedAmPms() {
        return this.mResources.getStringArray(a.f2175g);
    }

    public String[] getEarthlyBranches() {
        return this.mResources.getStringArray(a.f2177i);
    }

    public String[] getEras() {
        return this.mResources.getStringArray(a.f2178j);
    }

    public String[] getHeavenlyStems() {
        return this.mResources.getStringArray(a.f2180l);
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public String[] getMonths() {
        return this.mResources.getStringArray(a.f2182n);
    }

    public String[] getShortMonths() {
        return this.mResources.getStringArray(a.f2183o);
    }

    public String[] getShortWeekDays() {
        return this.mResources.getStringArray(a.f2190v);
    }

    public String[] getShortestMonths() {
        return this.mResources.getStringArray(a.f2184p);
    }

    public String[] getShortestWeekDays() {
        return this.mResources.getStringArray(a.f2191w);
    }

    public String[] getSolarTerms() {
        return this.mResources.getStringArray(a.f2187s);
    }

    public String[] getWeekDays() {
        return this.mResources.getStringArray(a.f2189u);
    }
}
