package com.miui.powerkeeper.thermal.processor;

import com.miui.powerkeeper.cloudcontrol.AutoRegister;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public abstract class ThermalProcessor extends AutoRegister implements IProcessor {

    protected interface DataParser {
        void parser(String str, String str2);
    }

    private static class NullDataException extends RuntimeException {
        public NullDataException(String str) {
            super(str);
        }
    }

    @Override // com.miui.powerkeeper.cloudcontrol.AutoRegister
    protected int getModel() {
        return b.b.C;
    }

    public abstract void init(Map<String, Set<Integer>> map);

    protected final void parser(String str, String str2, DataParser dataParser) {
        dataParser.parser(str, str2);
    }

    @Override // com.miui.powerkeeper.cloudcontrol.IUseCloud
    public void updateCloudData() {
    }
}
