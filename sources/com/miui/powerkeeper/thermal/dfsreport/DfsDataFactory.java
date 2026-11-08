package com.miui.powerkeeper.thermal.dfsreport;

/* JADX INFO: loaded from: classes.dex */
public class DfsDataFactory implements ThermalDfsInterface.DfsDataInterface {
    @Override // com.miui.powerkeeper.thermal.dfsreport.ThermalDfsInterface.DfsDataInterface
    public DfsEventData creatData(ReportEventNum reportEventNum) {
        return new DfsEventData(reportEventNum);
    }
}
