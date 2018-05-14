package com.duowan.mobile.main.kinds;

import com.duowan.mobile.main.kinds.wrapper.KindWrapper;

import java.util.Map;

/**
 * Created by ericwu on 2017/9/29.
 */

public interface KindMapSyringe {
    void injectFeatureInto(Map<String, Class<? extends Kind>> feature, Map<Class<? extends Kind>, Class<? extends KindWrapper>> wrappers);

}
