package com.duowan.mobile.main.kinds;

import com.duowan.mobile.main.kinds.wrapper.KindWrapper;

import java.util.List;

/**
 * Created by ericwu on 2017/9/26.
 */

public class Kinds {
    private static KindsImpl instance;

    public static void init(KindStorage storage, KindsFactory factory) {
        instance = new KindsImpl(storage, factory);
    }

    public static <T extends Kind> T of(Class<T> clz) {
        if (instance == null) {
            throw new UnsupportedOperationException("call Kinds.init().");
        }
        return instance.of(clz);
    }

    public static void addFeatureMap(String... moduleNameArray) {
        if (moduleNameArray != null && moduleNameArray.length > 0) {
            for (String moduleName : moduleNameArray) {
                instance.addFeatureMap(moduleName);
            }
        }
    }

    public static List<KindWrapper> getFeaturesWrapper() {
        return instance.getFeaturesWrapper();
    }
}
