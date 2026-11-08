package miui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* JADX INFO: loaded from: classes.dex */
public interface FileAccessable {

    public static abstract class AbstractFileAccessable implements FileAccessable {
        @Override // miui.util.FileAccessable
        public boolean isDirectory() {
            return exists() && !isFile();
        }

        @Override // miui.util.FileAccessable
        public List<FileAccessable> list(FileAccessableFilter fileAccessableFilter) {
            if (fileAccessableFilter == null) {
                return list();
            }
            List<FileAccessable> list = list();
            if (list == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            for (FileAccessable fileAccessable : list) {
                if (fileAccessableFilter.accept(fileAccessable)) {
                    arrayList.add(fileAccessable);
                }
            }
            return arrayList;
        }
    }

    public static class Factory {
        private static HashMap<String, WeakReference<ZipFile>> sZipFiles = new HashMap<>();

        public static void clearCache() {
            synchronized (sZipFiles) {
                sZipFiles.clear();
            }
        }

        public static FileAccessable create(String str, String str2) throws IOException {
            ZipFile zipFile;
            if (new File(str).isDirectory()) {
                return new DeskFile(str, str2);
            }
            synchronized (sZipFiles) {
                try {
                    WeakReference<ZipFile> weakReference = sZipFiles.get(str);
                    zipFile = weakReference == null ? null : weakReference.get();
                    if (zipFile == null) {
                        zipFile = new ZipFile(str);
                        sZipFiles.put(str, new WeakReference<>(zipFile));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            return new ZipInnerFile(zipFile, str2);
        }
    }

    public interface FileAccessableFilter {
        boolean accept(FileAccessable fileAccessable);
    }

    public static class ZipInnerFile extends AbstractFileAccessable {
        String mEntryName;
        boolean mExists;
        boolean mIsFolder;
        ZipFile mZipFile;

        public ZipInnerFile(ZipFile zipFile, String str) {
            init(zipFile, str);
        }

        private void init(ZipFile zipFile, String str) {
            this.mZipFile = zipFile;
            this.mEntryName = str.endsWith("/") ? str.substring(0, str.length() - 1) : str;
            if (this.mZipFile == null) {
                return;
            }
            ZipEntry entry = zipFile.getEntry(str);
            if (entry != null) {
                this.mExists = true;
                this.mIsFolder = entry.isDirectory();
                return;
            }
            if (!str.endsWith("/")) {
                str = str + "/";
            }
            Enumeration<? extends ZipEntry> enumerationEntries = zipFile.entries();
            while (enumerationEntries.hasMoreElements()) {
                if (enumerationEntries.nextElement().getName().startsWith(str)) {
                    this.mExists = true;
                    this.mIsFolder = true;
                    return;
                }
            }
        }

        private static boolean objectEquals(Object obj, Object obj2) {
            if (obj == obj2) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            return obj.equals(obj2);
        }

        @Override // miui.util.FileAccessable
        public FileAccessable createByExtension(String str) {
            return new ZipInnerFile(this.mZipFile, this.mEntryName + str);
        }

        @Override // miui.util.FileAccessable
        public FileAccessable createBySubpath(String str) {
            return new ZipInnerFile(this.mZipFile, this.mEntryName + "/" + str);
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof ZipInnerFile)) {
                return false;
            }
            ZipInnerFile zipInnerFile = (ZipInnerFile) obj;
            return objectEquals(this.mZipFile, zipInnerFile.mZipFile) && objectEquals(this.mEntryName, zipInnerFile.mEntryName);
        }

        @Override // miui.util.FileAccessable
        public boolean exists() {
            return this.mExists;
        }

        @Override // miui.util.FileAccessable
        public InputStream getInputStream() {
            if (this.mExists && !this.mIsFolder) {
                try {
                    return this.mZipFile.getInputStream(this.mZipFile.getEntry(this.mEntryName));
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            return null;
        }

        @Override // miui.util.FileAccessable
        public String getName() {
            int iLastIndexOf = this.mEntryName.lastIndexOf(47);
            String str = this.mEntryName;
            return iLastIndexOf < 0 ? str : str.substring(iLastIndexOf + 1, str.length());
        }

        public int hashCode() {
            ZipFile zipFile = this.mZipFile;
            if (zipFile == null) {
                return this.mEntryName.hashCode();
            }
            return this.mEntryName.hashCode() ^ zipFile.hashCode();
        }

        @Override // miui.util.FileAccessable
        public boolean isFile() {
            return !this.mIsFolder;
        }

        @Override // miui.util.FileAccessable
        public List<FileAccessable> list() {
            if (!this.mExists || !this.mIsFolder) {
                return null;
            }
            Enumeration<? extends ZipEntry> enumerationEntries = this.mZipFile.entries();
            ArrayList arrayList = new ArrayList();
            HashSet hashSet = new HashSet();
            while (enumerationEntries.hasMoreElements()) {
                String str = this.mEntryName + '/';
                ZipEntry zipEntryNextElement = enumerationEntries.nextElement();
                if (zipEntryNextElement.getName().length() > str.length() && zipEntryNextElement.getName().startsWith(str)) {
                    String strSubstring = zipEntryNextElement.getName().substring(str.length());
                    String name = zipEntryNextElement.getName();
                    int iIndexOf = strSubstring.indexOf(47);
                    if (iIndexOf != -1) {
                        name = str + strSubstring.substring(0, iIndexOf);
                    }
                    if (!hashSet.contains(name)) {
                        arrayList.add(new ZipInnerFile(this.mZipFile, name));
                        hashSet.add(name);
                    }
                }
            }
            return arrayList;
        }
    }

    FileAccessable createByExtension(String str);

    FileAccessable createBySubpath(String str);

    boolean exists();

    InputStream getInputStream();

    String getName();

    boolean isDirectory();

    boolean isFile();

    List<FileAccessable> list();

    List<FileAccessable> list(FileAccessableFilter fileAccessableFilter);

    public static class DeskFile extends AbstractFileAccessable {
        File mFile;

        public DeskFile(File file, String str) {
            this.mFile = new File(file, str);
        }

        @Override // miui.util.FileAccessable
        public FileAccessable createByExtension(String str) {
            return new DeskFile(this.mFile.getAbsolutePath() + str);
        }

        @Override // miui.util.FileAccessable
        public FileAccessable createBySubpath(String str) {
            return new DeskFile(this.mFile.getAbsolutePath(), str);
        }

        public boolean equals(Object obj) {
            return obj != null && (obj instanceof DeskFile) && this.mFile.equals(((DeskFile) obj).mFile);
        }

        @Override // miui.util.FileAccessable
        public boolean exists() {
            return this.mFile.exists();
        }

        public File getFile() {
            return this.mFile;
        }

        @Override // miui.util.FileAccessable
        public InputStream getInputStream() {
            try {
                return new FileInputStream(this.mFile);
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
                return null;
            }
        }

        @Override // miui.util.FileAccessable
        public String getName() {
            return this.mFile.getName();
        }

        public int hashCode() {
            return this.mFile.hashCode();
        }

        @Override // miui.util.FileAccessable
        public boolean isFile() {
            return this.mFile.isFile();
        }

        @Override // miui.util.FileAccessable
        public List<FileAccessable> list() {
            String[] list = this.mFile.list();
            ArrayList arrayList = new ArrayList();
            for (String str : list) {
                arrayList.add(new DeskFile(this.mFile, str));
            }
            return arrayList;
        }

        public DeskFile(String str, String str2) {
            this.mFile = new File(str, str2);
        }

        public DeskFile(String str) {
            this.mFile = new File(str);
        }
    }
}
