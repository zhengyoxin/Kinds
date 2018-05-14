package com.duowan.mobile.main.kinds;

import com.duowan.mobile.main.kinds.wrapper.KindWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ericwu on 2017/9/29.
 */

public class KindHouse {

    public static Map<String, Class<? extends Kind>> mFeatureClzs = new HashMap<>();
    public static Map<Class<? extends Kind>, Class<? extends KindWrapper>> mFeatureWrappers = new HashMap<>();

    static void clear() {
        mFeatureClzs.clear();
        mFeatureWrappers.clear();
    }
}
