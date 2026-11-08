package com.miui.powerkeeper.thermal.setting;

import com.xiaomi.push.service.PushServiceConstants;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.xml.sax.Attributes;

/* JADX INFO: loaded from: classes.dex */
public interface IParser {

    public static class ElementsParser implements IParser {
        @Override // com.miui.powerkeeper.thermal.setting.IParser
        public void parser(String str, Attributes attributes, SettingDTO settingDTO) {
            if (str.equals("listener")) {
                settingDTO.setsListener(attributes.getValue("name"));
            }
        }
    }

    public static class ProcessorsParser implements IParser {
        Map<String, Set<Integer>> mMap;

        @Override // com.miui.powerkeeper.thermal.setting.IParser
        public void parser(String str, Attributes attributes, SettingDTO settingDTO) {
            if (str.equals(PushServiceConstants.EXTENSION_ELEMENT_GROUP_MSG)) {
                this.mMap.put(attributes.getValue("name"), new HashSet(IParser.split(attributes.getValue("content"))));
            } else if (str.equals("processor")) {
                this.mMap = new HashMap();
                settingDTO.setsProcessor(new SettingDTO.ProcessorDTO(attributes.getValue("name"), this.mMap));
            }
        }
    }

    public static class ScenariosParser implements IParser {
        @Override // com.miui.powerkeeper.thermal.setting.IParser
        public void parser(String str, Attributes attributes, SettingDTO settingDTO) {
            if (str.equals("scenario")) {
                settingDTO.setsScenario(new SettingDTO.ScenarioDTO(attributes.getValue("name"), Integer.parseInt(attributes.getValue("id")), IParser.split(attributes.getValue("defined"))));
            }
        }
    }

    static List<Integer> split(String str) {
        if (str == null) {
            return null;
        }
        return (List) Arrays.stream(str.split(" ")).map(new Function() { // from class: com.miui.powerkeeper.thermal.setting.a
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(Integer.parseInt((String) obj));
            }
        }).collect(Collectors.toList());
    }

    void parser(String str, Attributes attributes, SettingDTO settingDTO);

    public static class NullParser implements IParser {
        @Override // com.miui.powerkeeper.thermal.setting.IParser
        public void parser(String str, Attributes attributes, SettingDTO settingDTO) {
        }
    }
}
