package miui.yellowpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Binder;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class YellowPage {
    private static final String TAG = "YellowPage";
    private String mAddress;
    private String mAlias;
    private String mAuthIconName;
    private String mBrief;
    private String mCatId;
    private String mContent;
    private String mCreditImg;
    private String mExtraData;
    private String mFirmUrl;
    private List<String> mGallery;
    private String mHotCatId;
    private int mHotSort;
    private boolean mIsHot;
    private boolean mIsMasterPage;
    private boolean mIsPreset;
    private String mLatitude;
    private String mLocId;
    private String mLongitude;
    private String mMiId;
    private String mName;
    private List<YellowPagePhone> mPhones = new ArrayList();
    private String mPhotoUrl;
    private String mPinyin;
    private int mProviderId;
    private List<Provider> mProviders;
    private String mSlogan;
    private List<Social> mSocials;
    private String mSourceId;
    private String mSourceUrl;
    private String mThumbnailUrl;
    private String mUrl;
    private long mYid;

    public static class Provider {
        private int mId;
        private String mSourceUrl;

        public static Provider fromJson(JSONObject jSONObject) {
            try {
                int i2 = jSONObject.getInt("provider");
                return new Provider().setId(i2).setSourceUrl(jSONObject.getString(Tag.TagYellowPage.SOURCE_URL));
            } catch (JSONException e2) {
                e2.printStackTrace();
                return null;
            }
        }

        public int getId() {
            return this.mId;
        }

        public String getSourceUrl() {
            return this.mSourceUrl;
        }

        public Provider setId(int i2) {
            this.mId = i2;
            return this;
        }

        public Provider setSourceUrl(String str) {
            this.mSourceUrl = str;
            return this;
        }
    }

    public static class Social {
        private String mName;
        private int mProviderId;
        private String mUrl;

        public Social(String str, String str2, int i2) {
            this.mUrl = str;
            this.mName = str2;
            this.mProviderId = i2;
        }

        public String getName() {
            return this.mName;
        }

        public int getProviderId() {
            return this.mProviderId;
        }

        public String getUrl() {
            return this.mUrl;
        }
    }

    private interface TagCallMenuNIvr {
        public static final String CALL_MENU = "callMenu";
        public static final String ICON = "icon";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String TYPE = "type";
    }

    private interface TagSocial {
        public static final String NAME = "name";
        public static final String PROVIDER = "provider";
        public static final String PROVIDER_NAME = "providerName";
        public static final String URL = "url";
    }

    public static YellowPage fromJson(String str) {
        try {
            return fromJson(new JSONObject(str));
        } catch (JSONException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private YellowPage setContent(String str) {
        this.mContent = str;
        return this;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public String getAlias() {
        return this.mAlias;
    }

    public String getAuthIconName() {
        return this.mAuthIconName;
    }

    public String getBrief() {
        return this.mBrief;
    }

    public String getCatId() {
        return this.mCatId;
    }

    public String getContent() {
        return this.mContent;
    }

    public String getCreditImg() {
        return this.mCreditImg;
    }

    public String getExtraData() {
        return this.mExtraData;
    }

    public String getFirmUrl() {
        return this.mFirmUrl;
    }

    public List<String> getGallery() {
        return this.mGallery;
    }

    public String getHotCatId() {
        String str = this.mHotCatId;
        return str == null ? "" : str;
    }

    public int getHotSort() {
        return this.mHotSort;
    }

    public long getId() {
        return this.mYid;
    }

    public String getLatitude() {
        return this.mLatitude;
    }

    public String getLocId() {
        return this.mLocId;
    }

    public String getLongitude() {
        return this.mLongitude;
    }

    public String getMiId() {
        return this.mMiId;
    }

    public String getName() {
        return this.mName;
    }

    public YellowPagePhone getPhoneInfo(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        String normalizedNumber = YellowPageUtils.getNormalizedNumber(context, str);
        Binder.restoreCallingIdentity(jClearCallingIdentity);
        List<YellowPagePhone> list = this.mPhones;
        if (list != null) {
            for (YellowPagePhone yellowPagePhone : list) {
                if (TextUtils.equals(normalizedNumber, yellowPagePhone.getNormalizedNumber())) {
                    return yellowPagePhone;
                }
            }
        }
        return null;
    }

    public List<YellowPagePhone> getPhones() {
        return this.mPhones;
    }

    public byte[] getPhoto() {
        return null;
    }

    public String getPhotoName() {
        return this.mPhotoUrl;
    }

    public String getPinyin() {
        return this.mPinyin;
    }

    public Bitmap getProviderIcon(Context context) {
        return YellowPageUtils.getProvider(context, this.mProviderId).getIcon();
    }

    public int getProviderId() {
        return this.mProviderId;
    }

    public List<Provider> getProviderList() {
        return this.mProviders;
    }

    public String getProviderName(Context context) {
        return YellowPageUtils.getProvider(context, this.mProviderId).getName();
    }

    public String getSlogan() {
        return this.mSlogan;
    }

    public List<Social> getSocials() {
        return this.mSocials;
    }

    public String getSourceId() {
        return this.mSourceId;
    }

    public String getSourceUrl() {
        return this.mSourceUrl;
    }

    public byte[] getThumbnail() {
        return null;
    }

    public String getThumbnailName() {
        return this.mThumbnailUrl;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public boolean isHot() {
        return this.mIsHot;
    }

    public boolean isMasterPage() {
        return this.mIsMasterPage;
    }

    public boolean isPreset() {
        return this.mIsPreset;
    }

    public boolean isProviderMiui() {
        return this.mProviderId == 0;
    }

    public YellowPage setAddress(String str) {
        this.mAddress = str;
        return this;
    }

    public YellowPage setAlias(String str) {
        this.mAlias = str;
        return this;
    }

    public YellowPage setAuthIconName(String str) {
        this.mAuthIconName = str;
        return this;
    }

    public YellowPage setBrief(String str) {
        this.mBrief = str;
        return this;
    }

    public YellowPage setCatId(String str) {
        this.mCatId = str;
        return this;
    }

    public YellowPage setCreditImg(String str) {
        this.mCreditImg = str;
        return this;
    }

    public YellowPage setExtraData(String str) {
        this.mExtraData = str;
        return this;
    }

    public YellowPage setFirmUrl(String str) {
        this.mFirmUrl = str;
        return this;
    }

    public YellowPage setGallery(List<String> list) {
        this.mGallery = list;
        return this;
    }

    public YellowPage setHotCatId(String str) {
        this.mHotCatId = str;
        return this;
    }

    public YellowPage setHotSort(int i2) {
        this.mHotSort = i2;
        return this;
    }

    public YellowPage setId(long j2) {
        this.mYid = j2;
        return this;
    }

    public YellowPage setIsHot(boolean z2) {
        this.mIsHot = z2;
        return this;
    }

    public YellowPage setIsMasterPage(boolean z2) {
        this.mIsMasterPage = z2;
        return this;
    }

    public YellowPage setIsPreset(boolean z2) {
        this.mIsPreset = z2;
        return this;
    }

    public YellowPage setLatitude(String str) {
        this.mLatitude = str;
        return this;
    }

    public YellowPage setLocId(String str) {
        this.mLocId = str;
        return this;
    }

    public YellowPage setLongitude(String str) {
        this.mLongitude = str;
        return this;
    }

    public YellowPage setMiId(String str) {
        this.mMiId = str;
        return this;
    }

    public YellowPage setName(String str) {
        this.mName = str;
        return this;
    }

    public YellowPage setPhones(List<YellowPagePhone> list) {
        this.mPhones = list;
        return this;
    }

    public YellowPage setPhotoName(String str) {
        this.mPhotoUrl = str;
        return this;
    }

    public YellowPage setPinyin(String str) {
        this.mPinyin = str;
        return this;
    }

    public YellowPage setProviderId(int i2) {
        this.mProviderId = i2;
        return this;
    }

    public YellowPage setProviderList(List<Provider> list) {
        this.mProviders = list;
        return this;
    }

    public YellowPage setSlogan(String str) {
        this.mSlogan = str;
        return this;
    }

    public YellowPage setSocials(List<Social> list) {
        this.mSocials = list;
        return this;
    }

    public YellowPage setSourceId(String str) {
        this.mSourceId = str;
        return this;
    }

    public YellowPage setSourceUrl(String str) {
        this.mSourceUrl = str;
        return this;
    }

    public YellowPage setThumbnailName(String str) {
        this.mThumbnailUrl = str;
        return this;
    }

    public YellowPage setUrl(String str) {
        this.mUrl = str;
        return this;
    }

    public static YellowPage fromJson(JSONObject jSONObject) {
        boolean z2;
        boolean z3;
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        boolean z4;
        long j2;
        String str6;
        String str7;
        int i2;
        String str8;
        String str9;
        String str10;
        String str11;
        String str12;
        String str13;
        String str14;
        String str15;
        String str16;
        String str17;
        String str18;
        int i3;
        ArrayList arrayList;
        ArrayList arrayList2;
        String str19 = "phone";
        try {
            long j3 = jSONObject.getLong("sid");
            String string = jSONObject.getString(Tag.TagYellowPage.NAME);
            int iOptInt = jSONObject.optInt("provider");
            String strOptString = jSONObject.optString(Tag.TagYellowPage.PINYIN);
            String strOptString2 = jSONObject.optString(Tag.TagYellowPage.ALIAS);
            String strOptString3 = jSONObject.optString(Tag.TagYellowPage.ADDRESS);
            String strOptString4 = jSONObject.optString(Tag.TagYellowPage.URL);
            String strOptString5 = jSONObject.optString(Tag.TagYellowPage.FIRMURL);
            String strOptString6 = jSONObject.optString(Tag.TagYellowPage.CREDITIMG);
            String strOptString7 = jSONObject.optString(Tag.TagYellowPage.SOURCE_URL);
            String strOptString8 = jSONObject.optString(Tag.TagYellowPage.SOURCE_ID);
            String strOptString9 = jSONObject.optString(Tag.TagYellowPage.BRIEF_INFO);
            String str20 = strOptString8;
            String strOptString10 = jSONObject.optString("hotCatId");
            int iOptInt2 = jSONObject.optInt("hotSort");
            String strOptString11 = jSONObject.optString("catId");
            String strOptString12 = jSONObject.optString("locId");
            String strOptString13 = jSONObject.optString("longitude");
            String strOptString14 = jSONObject.optString("latitude");
            String strOptString15 = jSONObject.optString("miid");
            String strOptString16 = jSONObject.optString(Tag.TagYellowPage.MI_SUB_ID);
            String strOptString17 = jSONObject.optString("slogan");
            String strOptString18 = jSONObject.optString(Tag.TagYellowPage.AUTH_ICON);
            String str21 = strOptString6;
            String str22 = strOptString;
            boolean z5 = jSONObject.optInt(Tag.TagYellowPage.TYPE) == 2;
            boolean z6 = jSONObject.optInt(Tag.TagYellowPage.HOT) == 1;
            int i4 = iOptInt;
            boolean z7 = jSONObject.optInt("buildIn") == 1;
            boolean zHas = jSONObject.has(TagCallMenuNIvr.CALL_MENU);
            JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("phone");
            if (jSONArrayOptJSONArray != null) {
                boolean z8 = z6;
                boolean z9 = z7;
                int i5 = 0;
                ArrayList arrayList3 = null;
                while (i5 < jSONArrayOptJSONArray.length()) {
                    JSONObject jSONObject2 = jSONArrayOptJSONArray.getJSONObject(i5);
                    String string2 = jSONObject2.getString(str19);
                    String str23 = str19;
                    String strOptString19 = jSONObject2.optString(Tag.TagPhone.NORMALIZED_NUMBER);
                    boolean z10 = z5;
                    String string3 = jSONObject2.getString(Tag.TagPhone.Tag);
                    String string4 = jSONObject2.getString(Tag.TagPhone.PINYIN);
                    int i6 = i5;
                    JSONArray jSONArray = jSONArrayOptJSONArray;
                    long jOptLong = jSONObject2.optLong(Tag.TagPhone.T9_RANK);
                    int iOptInt3 = jSONObject2.optInt(Tag.TagPhone.ATD_CAT_ID);
                    int iOptInt4 = jSONObject2.optInt("count");
                    int iOptInt5 = jSONObject2.optInt("provider");
                    int iOptInt6 = jSONObject2.optInt("flag");
                    boolean z11 = jSONObject2.getInt("hide") == 0;
                    if (arrayList3 == null) {
                        arrayList3 = new ArrayList();
                    }
                    ArrayList arrayList4 = arrayList3;
                    String str24 = strOptString17;
                    String str25 = str21;
                    String str26 = str22;
                    int i7 = i4;
                    YellowPagePhone yellowPagePhone = new YellowPagePhone(j3, string, string3, string2, strOptString19, 1, i7, iOptInt4, z11, str26, string4, zHas);
                    yellowPagePhone.setT9Rank(jOptLong);
                    yellowPagePhone.setRawSlogan(str24);
                    yellowPagePhone.setCid(iOptInt3);
                    yellowPagePhone.setFlag(iOptInt6);
                    yellowPagePhone.setAntispamProviderId(iOptInt5);
                    yellowPagePhone.setCreditImg(str25);
                    arrayList4.add(yellowPagePhone);
                    i5 = i6 + 1;
                    str22 = str26;
                    str21 = str25;
                    arrayList3 = arrayList4;
                    string = string;
                    i4 = i7;
                    jSONArrayOptJSONArray = jSONArray;
                    str19 = str23;
                    str20 = str20;
                    strOptString10 = strOptString10;
                    iOptInt2 = iOptInt2;
                    strOptString11 = strOptString11;
                    strOptString12 = strOptString12;
                    strOptString13 = strOptString13;
                    strOptString14 = strOptString14;
                    strOptString18 = strOptString18;
                    z8 = z8;
                    strOptString7 = strOptString7;
                    z5 = z10;
                    strOptString5 = strOptString5;
                    strOptString4 = strOptString4;
                    strOptString3 = strOptString3;
                    strOptString2 = strOptString2;
                    z9 = z9;
                    j3 = j3;
                    strOptString17 = str24;
                    strOptString9 = strOptString9;
                    strOptString15 = strOptString15;
                }
                z3 = z5;
                str = strOptString7;
                str2 = strOptString5;
                str3 = strOptString4;
                str4 = strOptString3;
                str5 = strOptString2;
                j2 = j3;
                str6 = str20;
                str7 = strOptString10;
                i2 = iOptInt2;
                str8 = strOptString11;
                str9 = strOptString12;
                str10 = strOptString13;
                str11 = strOptString14;
                str12 = strOptString15;
                str13 = strOptString18;
                str14 = str22;
                z4 = z9;
                z2 = z8;
                str15 = strOptString9;
                str16 = string;
                str17 = strOptString17;
                str18 = str21;
                i3 = i4;
                arrayList = arrayList3;
            } else {
                z2 = z6;
                z3 = z5;
                str = strOptString7;
                str2 = strOptString5;
                str3 = strOptString4;
                str4 = strOptString3;
                str5 = strOptString2;
                z4 = z7;
                j2 = j3;
                str6 = str20;
                str7 = strOptString10;
                i2 = iOptInt2;
                str8 = strOptString11;
                str9 = strOptString12;
                str10 = strOptString13;
                str11 = strOptString14;
                str12 = strOptString15;
                str13 = strOptString18;
                str14 = str22;
                str15 = strOptString9;
                str16 = string;
                str17 = strOptString17;
                str18 = str21;
                i3 = i4;
                arrayList = null;
            }
            JSONArray jSONArrayOptJSONArray2 = jSONObject.optJSONArray(Tag.TagYellowPage.SOCIAL);
            if (jSONArrayOptJSONArray2 == null || jSONArrayOptJSONArray2.length() <= 0) {
                arrayList2 = null;
            } else {
                arrayList2 = new ArrayList();
                for (int i8 = 0; i8 < jSONArrayOptJSONArray2.length(); i8++) {
                    JSONObject jSONObject3 = jSONArrayOptJSONArray2.getJSONObject(i8);
                    arrayList2.add(new Social(jSONObject3.getString("url"), jSONObject3.getString("name"), jSONObject3.optInt("provider")));
                }
            }
            ArrayList arrayList5 = new ArrayList();
            JSONArray jSONArrayOptJSONArray3 = jSONObject.optJSONArray(Tag.TagYellowPage.PROVIDER_LIST);
            if (jSONArrayOptJSONArray3 != null) {
                for (int i9 = 0; i9 < jSONArrayOptJSONArray3.length(); i9++) {
                    Provider providerFromJson = Provider.fromJson(jSONArrayOptJSONArray3.getJSONObject(i9));
                    if (providerFromJson != null) {
                        arrayList5.add(providerFromJson);
                    }
                }
            }
            String strOptString20 = jSONObject.optString(Tag.TagYellowPage.PHOTO);
            String strOptString21 = jSONObject.optString(Tag.TagYellowPage.THUMBNAIL);
            return new YellowPage().setId(j2).setName(str16).setPinyin(str14).setBrief(str15).setAlias(str5).setAddress(str4).setPhones(arrayList).setSocials(arrayList2).setThumbnailName(strOptString21).setPhotoName(strOptString20).setProviderId(i3).setUrl(str3).setFirmUrl(str2).setCreditImg(str18).setSourceUrl(str).setSourceId(str6).setIsMasterPage(z3).setIsPreset(z4).setIsHot(z2).setHotCatId(str7).setHotSort(i2).setCatId(str8).setLocId(str9).setLongitude(str10).setLatitude(str11).setContent(jSONObject.toString()).setSlogan(str17).setProviderList(arrayList5).setMiId(TextUtils.isEmpty(strOptString16) ? str12 : str12 + "/" + strOptString16).setAuthIconName(str13).setExtraData(jSONObject.optString("extraData"));
        } catch (JSONException e2) {
            e2.printStackTrace();
            return null;
        }
    }
}
