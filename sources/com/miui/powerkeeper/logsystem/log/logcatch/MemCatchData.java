package com.miui.powerkeeper.logsystem.log.logcatch;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class MemCatchData {
    private ArrayList<String> mArrayList = new ArrayList<>();
    private StringBuilder mContent;
    private String mTypeName;

    public MemCatchData(String str) {
        this.mTypeName = str;
    }

    public void addContent(String str) {
        if (this.mContent == null) {
            catchStart();
        }
        this.mContent.append(str);
    }

    public void addContentLn(String str) {
        addContent(str);
        this.mContent.append('\n');
    }

    public void catchEnd() {
        StringBuilder sb = this.mContent;
        if (sb == null) {
            return;
        }
        this.mArrayList.add(sb.toString());
        this.mContent = null;
    }

    public void catchStart() {
        StringBuilder sb = new StringBuilder();
        this.mContent = sb;
        sb.append('\n');
        this.mContent.append(this.mTypeName);
        this.mContent.append("------:  ");
        this.mContent.append('\n');
    }

    public ArrayList<String> getList() {
        return this.mArrayList;
    }
}
