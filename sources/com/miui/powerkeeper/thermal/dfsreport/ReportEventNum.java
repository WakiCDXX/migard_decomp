package com.miui.powerkeeper.thermal.dfsreport;

/* JADX INFO: loaded from: classes.dex */
public enum ReportEventNum {
    AmbitionTempReportNum(905001000),
    BoardTempReport(905001001),
    LastBoardTempReport(905001002),
    HighTempDataColltNum(905001003),
    NtcAbnormalNum(905001010),
    BoardTempInaccurateNum(905001011),
    AuxiliaryInformationNum(905004000),
    SceneNumErrorNum(905003050),
    PhoneCallSceneErrorNUM(905003051),
    PerPowerModeErrorNum(905003052),
    IecSceneErrorNum(905003053),
    CloudControlErrorNum(905003060),
    OtherErrorNum(905003070);

    private int dataNum;

    ReportEventNum(int i2) {
        this.dataNum = i2;
    }

    public int getDataNum() {
        return this.dataNum;
    }
}
