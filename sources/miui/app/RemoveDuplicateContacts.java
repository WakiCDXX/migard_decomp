package miui.app;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Entity;
import android.content.EntityIterator;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.push.mpcd.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import miui.provider.BatchOperation;
import miui.provider.ExtraContacts;

/* JADX INFO: loaded from: classes.dex */
public class RemoveDuplicateContacts {
    public static final String CALLER_IS_REMOVE_DUPLICATE = "caller_is_remove_duplicate";
    private static final Uri CONTENT_URI = ContactsContract.RawContactsEntity.CONTENT_URI;
    private static final boolean DBG = false;
    private static final String NAME_SELECTION = "deleted=0 AND account_name=? AND account_type=? AND data_set IS NULL ";
    public static final String TAG = "RemoveDuplicateContacts";
    private static final HashMap<Long, String> sGroups;
    private static final HashSet<String> sOtherMimeTypes;

    public static class GroupsData {
        public long id;
        public String sourceid;
        public String title;
    }

    public static class MergeContacts {
        private boolean mChecked = true;
        private ArrayList<ContactsInfo> mContacts;
        private String mName;

        public MergeContacts(ArrayList<ContactsInfo> arrayList, String str) {
            this.mContacts = arrayList;
            this.mName = str;
        }

        public ArrayList<ContactsInfo> getContacts() {
            return this.mContacts;
        }

        public String getName() {
            return this.mName;
        }

        public boolean isChecked() {
            return this.mChecked;
        }

        public void setChecked(boolean z2) {
            this.mChecked = z2;
        }
    }

    public static class RawContactData {
        public static final int HAS_DISPLAY_PHOTO = 100;
        public static final int HAS_PHOTO = 10;
        private HashMap<String, List<String>> mDatas = new HashMap<>();
        private boolean mDeleted;
        public int mN;
        public String mName;
        public long mPhotoId;
        public long mRawContactId;
        public String mSourceId;

        public void addData(String str, String str2) {
            if (str2 == null) {
                return;
            }
            List<String> list = this.mDatas.get(str);
            if (list == null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(str2);
                this.mDatas.put(str, arrayList);
            } else {
                if (list.contains(str2)) {
                    return;
                }
                list.add(str2);
            }
        }

        public boolean compare(RawContactData rawContactData) {
            String str;
            if (rawContactData == null || rawContactData.mDatas.size() != this.mDatas.size()) {
                return false;
            }
            for (String str2 : this.mDatas.keySet()) {
                if (!rawContactData.mDatas.containsKey(str2)) {
                    return false;
                }
                List<String> list = this.mDatas.get(str2);
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(rawContactData.mDatas.get(str2));
                if (list.size() != arrayList.size()) {
                    return false;
                }
                if ("vnd.android.cursor.item/phone_v2".equals(str2)) {
                    for (String str3 : list) {
                        Iterator it = arrayList.iterator();
                        do {
                            if (!it.hasNext()) {
                                str = null;
                                break;
                            }
                            str = (String) it.next();
                            if (str3.equals(arrayList)) {
                                break;
                            }
                        } while (!PhoneNumberUtils.compare(str3, str));
                        if (str == null) {
                            return false;
                        }
                        arrayList.remove(str);
                    }
                } else if (!list.equals(arrayList)) {
                    return false;
                }
            }
            return true;
        }

        public HashMap<String, List<String>> getDatas() {
            return this.mDatas;
        }

        public long getRawContactId() {
            return this.mRawContactId;
        }

        public boolean isDeleted() {
            return this.mDeleted;
        }

        public void setDeleted(boolean z2) {
            this.mDeleted = z2;
        }

        public void setRawContactId(long j2) {
            this.mRawContactId = j2;
        }
    }

    public interface RemoveDuplicateContactsListener {
        void onBegin(int i2);

        void onEnd(boolean z2);

        void onProgress(int i2);
    }

    static {
        HashSet<String> hashSet = new HashSet<>();
        sOtherMimeTypes = hashSet;
        hashSet.add("vnd.android.cursor.item/phone_v2");
        hashSet.add("vnd.android.cursor.item/email_v2");
        hashSet.add("vnd.android.cursor.item/postal-address_v2");
        hashSet.add("vnd.android.cursor.item/organization");
        hashSet.add("vnd.android.cursor.item/website");
        hashSet.add("vnd.android.cursor.item/contact_event");
        hashSet.add("vnd.android.cursor.item/sip_address");
        hashSet.add("vnd.android.cursor.item/relation");
        hashSet.add("vnd.android.cursor.item/note");
        hashSet.add("vnd.android.cursor.item/nickname");
        hashSet.add(ExtraContacts.Gender.CONTENT_ITEM_TYPE);
        hashSet.add(ExtraContacts.BloodType.CONTENT_ITEM_TYPE);
        hashSet.add(ExtraContacts.Constellation.CONTENT_ITEM_TYPE);
        hashSet.add(ExtraContacts.AnimalSign.CONTENT_ITEM_TYPE);
        hashSet.add(ExtraContacts.EmotionStatus.CONTENT_ITEM_TYPE);
        hashSet.add(ExtraContacts.Interest.CONTENT_ITEM_TYPE);
        hashSet.add(ExtraContacts.Hobby.CONTENT_ITEM_TYPE);
        hashSet.add(ExtraContacts.Degree.CONTENT_ITEM_TYPE);
        hashSet.add(ExtraContacts.Schools.CONTENT_ITEM_TYPE);
        hashSet.add(ExtraContacts.Characteristic.CONTENT_ITEM_TYPE);
        hashSet.add(ExtraContacts.XiaomiId.CONTENT_ITEM_TYPE);
        hashSet.add(ExtraContacts.LunarBirthday.CONTENT_ITEM_TYPE);
        sGroups = new HashMap<>();
    }

    public static List<RawContactData> getDeletedRawContacts(Account account, ContentResolver contentResolver) {
        ArrayList<RawContactData> needDeletedRawContacts;
        String[] strArr = {account.name, account.type};
        ArrayList arrayList = new ArrayList();
        HashMap<String, List<Long>> nameWithRawContactIds = getNameWithRawContactIds(contentResolver, strArr);
        if (nameWithRawContactIds.size() > 0) {
            for (String str : nameWithRawContactIds.keySet()) {
                List<Long> list = nameWithRawContactIds.get(str);
                if (list.size() >= 2 && (needDeletedRawContacts = getNeedDeletedRawContacts(contentResolver, list, str)) != null) {
                    arrayList.addAll(needDeletedRawContacts);
                }
            }
        }
        return arrayList;
    }

    private static EntityIterator getEntityByIds(ContentResolver contentResolver, List<Long> list) {
        Cursor cursorQuery = contentResolver.query(CONTENT_URI, null, "_id IN (" + TextUtils.join(",", list) + Constants.SEPARATOR_RIGHT_PARENTESIS, null, null);
        if (cursorQuery == null) {
            return null;
        }
        return ContactsContract.RawContacts.newEntityIterator(cursorQuery);
    }

    private static List<GroupsData> getGroups(Account account, ContentResolver contentResolver) {
        String[] strArr = {account.name, account.type};
        Cursor cursorQuery = contentResolver.query(ContactsContract.Groups.CONTENT_URI, new String[]{"_id", "title", "sourceid"}, "account_name = ? AND account_type = ? ", strArr, "title,sourceid DESC");
        ArrayList arrayList = new ArrayList();
        if (cursorQuery != null) {
            while (cursorQuery.moveToNext()) {
                try {
                    GroupsData groupsData = new GroupsData();
                    groupsData.id = cursorQuery.getLong(0);
                    groupsData.title = cursorQuery.getString(1);
                    groupsData.sourceid = cursorQuery.getString(2);
                    arrayList.add(groupsData);
                } catch (Throwable th) {
                    cursorQuery.close();
                    throw th;
                }
            }
            cursorQuery.close();
        }
        return arrayList;
    }

    public static ArrayList<MergeContacts> getMergeRawContacts(Account[] accountArr, ContentResolver contentResolver) {
        String str;
        Account[] accountArr2 = accountArr;
        String str2 = "_id";
        ArrayList<MergeContacts> arrayList = new ArrayList<>();
        int length = accountArr2.length;
        int i2 = 0;
        while (i2 < length) {
            Account account = accountArr2[i2];
            HashMap<String, List<Long>> nameWithRawContactIds = getNameWithRawContactIds(contentResolver, new String[]{account.name, account.type});
            if (nameWithRawContactIds.size() != 0) {
                for (String str3 : nameWithRawContactIds.keySet()) {
                    List<Long> list = nameWithRawContactIds.get(str3);
                    if (list.size() >= 2) {
                        EntityIterator entityByIds = getEntityByIds(contentResolver, list);
                        ArrayList arrayList2 = new ArrayList();
                        while (entityByIds.hasNext()) {
                            try {
                                Entity entity = (Entity) entityByIds.next();
                                long jLongValue = entity.getEntityValues().getAsLong(str2).longValue();
                                ArrayList arrayList3 = new ArrayList();
                                ArrayList arrayList4 = new ArrayList();
                                Iterator<Entity.NamedContentValues> it = entity.getSubValues().iterator();
                                long jLongValue2 = 0;
                                while (it.hasNext()) {
                                    ContentValues contentValues = it.next().values;
                                    String asString = contentValues.getAsString("mimetype");
                                    if ("vnd.android.cursor.item/photo".equals(asString)) {
                                        jLongValue2 = contentValues.getAsLong(str2).longValue();
                                        str = str2;
                                    } else {
                                        str = str2;
                                        if ("vnd.android.cursor.item/phone_v2".equals(asString)) {
                                            arrayList3.add(contentValues.getAsString("data1"));
                                        } else if ("vnd.android.cursor.item/email_v2".equals(asString)) {
                                            arrayList4.add(contentValues.getAsString("data1"));
                                        }
                                    }
                                    str2 = str;
                                }
                                String str4 = str2;
                                ArrayList arrayList5 = arrayList2;
                                arrayList5.add(new ContactsInfo(jLongValue2, str3, arrayList3, arrayList4, jLongValue));
                                arrayList2 = arrayList5;
                                str2 = str4;
                            } catch (Throwable th) {
                                entityByIds.close();
                                throw th;
                            }
                        }
                        String str5 = str2;
                        ArrayList arrayList6 = arrayList2;
                        entityByIds.close();
                        if (arrayList6.size() > 1) {
                            arrayList.add(new MergeContacts(arrayList6, str3));
                        }
                        contentResolver = contentResolver;
                        str2 = str5;
                    }
                }
            }
            i2++;
            accountArr2 = accountArr;
            str2 = str2;
        }
        return arrayList;
    }

    private static HashMap<String, List<Long>> getNameWithRawContactIds(ContentResolver contentResolver, String[] strArr) {
        HashMap<String, List<Long>> map = new HashMap<>();
        Cursor cursorQuery = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, new String[]{"display_name", "_id"}, NAME_SELECTION, strArr, null);
        if (cursorQuery == null) {
            return null;
        }
        while (cursorQuery.moveToNext()) {
            try {
                String string = cursorQuery.getString(0);
                long j2 = cursorQuery.getLong(1);
                List<Long> list = map.get(string);
                if (list == null) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Long.valueOf(j2));
                    map.put(string, arrayList);
                } else {
                    list.add(Long.valueOf(j2));
                }
            } catch (Throwable th) {
                cursorQuery.close();
                throw th;
            }
        }
        cursorQuery.close();
        return map;
    }

    private static ArrayList<RawContactData> getNeedDeletedRawContacts(ContentResolver contentResolver, List<Long> list, String str) {
        if (list == null || list.size() == 0) {
            return null;
        }
        System.currentTimeMillis();
        EntityIterator entityByIds = getEntityByIds(contentResolver, list);
        if (entityByIds == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        while (entityByIds.hasNext()) {
            try {
                try {
                    Entity entity = (Entity) entityByIds.next();
                    RawContactData rawContactData = new RawContactData();
                    ContentValues entityValues = entity.getEntityValues();
                    rawContactData.setRawContactId(entityValues.getAsLong("_id").longValue());
                    rawContactData.mName = str;
                    rawContactData.mSourceId = entityValues.getAsString("sourceid");
                    Iterator<Entity.NamedContentValues> it = entity.getSubValues().iterator();
                    while (it.hasNext()) {
                        ContentValues contentValues = it.next().values;
                        String asString = contentValues.getAsString("mimetype");
                        if ("vnd.android.cursor.item/photo".equals(asString)) {
                            rawContactData.mPhotoId = contentValues.getAsLong("_id").longValue();
                            rawContactData.mN = contentValues.containsKey("data14") ? 100 : 10;
                        } else if ("vnd.android.cursor.item/group_membership".equals(asString)) {
                            Long asLong = contentValues.getAsLong("data1");
                            asLong.longValue();
                            String str2 = sGroups.get(asLong);
                            if (str2 == null) {
                                str2 = "";
                            }
                            rawContactData.addData(asString, str2);
                        } else if ("vnd.android.cursor.item/im".equals(asString)) {
                            rawContactData.addData(asString, contentValues.getAsString("data1"));
                        } else if (!"vnd.android.cursor.item/name".equals(asString)) {
                            if (sOtherMimeTypes.contains(asString)) {
                                String asString2 = contentValues.getAsString("data1");
                                if (!TextUtils.isEmpty(asString2)) {
                                    rawContactData.addData(asString, asString2);
                                }
                            } else {
                                Log.d(TAG, "ignore unknown mimetype " + asString);
                            }
                        }
                    }
                    arrayList.add(rawContactData);
                } catch (Exception e2) {
                    Log.w(TAG, "getNeedDeletedRawContacts(): " + e2.getMessage());
                    e2.printStackTrace();
                }
            } catch (Throwable th) {
                entityByIds.close();
                throw th;
            }
        }
        entityByIds.close();
        System.currentTimeMillis();
        int size = arrayList.size();
        if (size < 2) {
            return null;
        }
        ArrayList<RawContactData> arrayList2 = new ArrayList<>();
        for (int i2 = 0; i2 < size - 1; i2++) {
            RawContactData rawContactData2 = (RawContactData) arrayList.get(i2);
            if (!rawContactData2.isDeleted()) {
                for (int i3 = i2 + 1; i3 < size; i3++) {
                    RawContactData rawContactData3 = (RawContactData) arrayList.get(i3);
                    if (!rawContactData3.isDeleted() && rawContactData2.compare(rawContactData3)) {
                        if (rawContactData3.mSourceId != null) {
                            int i4 = rawContactData2.mN;
                            int i5 = rawContactData3.mN;
                            if (i4 <= i5) {
                                if (i4 >= i5) {
                                    rawContactData2.setDeleted(true);
                                    arrayList2.add(rawContactData2);
                                    break;
                                }
                                rawContactData2.setDeleted(true);
                                arrayList2.add(rawContactData2);
                                break;
                            }
                            rawContactData3.setDeleted(true);
                            arrayList2.add(rawContactData3);
                        } else {
                            rawContactData3.setDeleted(true);
                            arrayList2.add(rawContactData3);
                        }
                    }
                }
            }
        }
        System.currentTimeMillis();
        return arrayList2;
    }

    public static synchronized int remove(Account[] accountArr, ContentResolver contentResolver, RemoveDuplicateContactsListener removeDuplicateContactsListener, boolean z2) {
        int i2 = 0;
        if (accountArr == null || contentResolver == null) {
            return 0;
        }
        try {
            System.currentTimeMillis();
            ArrayList<RawContactData> arrayList = new ArrayList();
            if (removeDuplicateContactsListener != null) {
                removeDuplicateContactsListener.onBegin(0);
            }
            for (Account account : accountArr) {
                sGroups.clear();
                for (GroupsData groupsData : getGroups(account, contentResolver)) {
                    sGroups.put(Long.valueOf(groupsData.id), groupsData.title);
                }
                arrayList.addAll(getDeletedRawContacts(account, contentResolver));
            }
            if (arrayList.size() > 0) {
                BatchOperation batchOperation = new BatchOperation(contentResolver, "com.android.contacts");
                if (removeDuplicateContactsListener != null) {
                    removeDuplicateContactsListener.onBegin(arrayList.size());
                }
                for (RawContactData rawContactData : arrayList) {
                    Uri uriBuild = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactData.mRawContactId).buildUpon().appendQueryParameter(CALLER_IS_REMOVE_DUPLICATE, "true").build();
                    ContentProviderOperation contentProviderOperationBuild = rawContactData.mSourceId == null ? ContentProviderOperation.newDelete(uriBuild.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build()).build() : !z2 ? ContentProviderOperation.newDelete(uriBuild).build() : null;
                    if (contentProviderOperationBuild != null) {
                        batchOperation.add(contentProviderOperationBuild);
                    }
                    if (batchOperation.size() > 100) {
                        batchOperation.execute();
                    }
                    if (removeDuplicateContactsListener != null) {
                        removeDuplicateContactsListener.onProgress(i2);
                    }
                    i2++;
                }
                if (batchOperation.size() > 0) {
                    batchOperation.execute();
                }
            }
            if (removeDuplicateContactsListener != null) {
                removeDuplicateContactsListener.onEnd(true);
            }
            System.currentTimeMillis();
            return i2;
        } catch (Throwable th) {
            throw th;
        }
    }

    public static void removeGroups(Account account, ContentResolver contentResolver) {
        List<GroupsData> groups = getGroups(account, contentResolver);
        if (groups.size() <= 1) {
            return;
        }
        long j2 = 0;
        for (int i2 = 0; i2 < groups.size(); i2++) {
            if (i2 == 0) {
                j2 = groups.get(i2).id;
            } else {
                String str = groups.get(i2 - 1).title;
                String str2 = groups.get(i2).title;
                long j3 = groups.get(i2).id;
                String str3 = groups.get(i2).sourceid;
                if (TextUtils.equals(str, str2)) {
                    String[] strArr = {"vnd.android.cursor.item/group_membership", String.valueOf(j3)};
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("data1", Long.valueOf(j2));
                    contentResolver.update(ContactsContract.Data.CONTENT_URI, contentValues, "mimetype=? AND data1=?", strArr);
                    Log.d(TAG, "update contacts group from " + j3 + " to " + j2);
                    contentResolver.delete(str3 == null ? ContentUris.withAppendedId(ContactsContract.Groups.CONTENT_URI, j3).buildUpon().appendQueryParameter("caller_is_syncadapter", "true").build() : ContentUris.withAppendedId(ContactsContract.Groups.CONTENT_URI, j3), null, null);
                    Log.d(TAG, "delete group " + j3);
                } else {
                    j2 = groups.get(i2).id;
                }
            }
        }
        List<GroupsData> groups2 = getGroups(account, contentResolver);
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        String[] strArr2 = {"_id", ExtraContacts.T9LookupColumns.RAW_CONTACT_ID};
        for (GroupsData groupsData : groups2) {
            hashSet.clear();
            Cursor cursorQuery = contentResolver.query(ContactsContract.Data.CONTENT_URI, strArr2, "mimetype=? AND data1=?", new String[]{"vnd.android.cursor.item/group_membership", String.valueOf(groupsData.id)}, null);
            if (cursorQuery != null) {
                while (cursorQuery.moveToNext()) {
                    try {
                        long j4 = cursorQuery.getLong(0);
                        long j5 = cursorQuery.getLong(1);
                        if (hashSet.contains(Long.valueOf(j5))) {
                            hashSet2.add(Long.valueOf(j4));
                        } else {
                            hashSet.add(Long.valueOf(j5));
                        }
                    } catch (Throwable th) {
                        cursorQuery.close();
                        throw th;
                    }
                }
                cursorQuery.close();
            }
        }
        BatchOperation batchOperation = new BatchOperation(contentResolver, "com.android.contacts");
        Iterator it = hashSet2.iterator();
        while (it.hasNext()) {
            batchOperation.add(ContentProviderOperation.newDelete(ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, ((Long) it.next()).longValue())).build());
            if (batchOperation.size() > 100) {
                batchOperation.execute();
            }
        }
        if (batchOperation.size() > 0) {
            batchOperation.execute();
        }
    }

    public static class ContactsInfo {
        private int mCount;
        private List<String> mEmails;
        private String mName;
        private List<String> mPhones;
        private long mPhotoId;
        private long mRawContactId;

        public ContactsInfo(RawContactData rawContactData, int i2) {
            this.mPhotoId = rawContactData.mPhotoId;
            this.mName = TextUtils.isEmpty(rawContactData.mName) ? "" : rawContactData.mName;
            this.mPhones = rawContactData.getDatas().get("vnd.android.cursor.item/phone_v2");
            this.mCount = i2;
            this.mRawContactId = rawContactData.getRawContactId();
            this.mEmails = rawContactData.getDatas().get("vnd.android.cursor.item/email_v2");
        }

        public int getCount() {
            return this.mCount;
        }

        public List<String> getEmails() {
            return this.mEmails;
        }

        public String getName() {
            return this.mName;
        }

        public List<String> getPhones() {
            return this.mPhones;
        }

        public long getPhotoId() {
            return this.mPhotoId;
        }

        public long getRawContactId() {
            return this.mRawContactId;
        }

        public ContactsInfo(long j2, String str, List<String> list, List<String> list2, long j3) {
            this.mPhotoId = j2;
            this.mName = TextUtils.isEmpty(str) ? "" : str;
            this.mPhones = list;
            this.mEmails = list2;
            this.mRawContactId = j3;
        }
    }
}
