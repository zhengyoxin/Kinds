package com.duowan.mobile.main.kinds;


import com.duowan.mobile.main.kinds.wrapper.KindWrapper;

import java.util.Set;

/**
 * Created by ericwu on 2017/9/26.
 */

public class KindsFactory {

    public KindsFactory() {

    }

    /**
     * Initialize feature wrapper.
     *
     * @param clz
     * @param storage
     * @param <T>
     * @return
     */
    public final <T extends Kind> KindWrapper<T, ?> create(Class<T> clz, KindStorage storage) {
        Class<? extends KindWrapper> wrapperClass = KindHouse.mFeatureWrappers.get(clz);
        try {
            return wrapperClass.getDeclaredConstructor(KindStorage.class, Class.class)
                    .newInstance(storage, clz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get feature classes.
     *
     * @return
     */
    public Set<Class<? extends Kind>> featureClasses() {
        return KindHouse.mFeatureWrappers.keySet();
    }

    /**
     * Map key to Kind class.
     *
     * @param key
     * @return
     */
    public Class<? extends Kind> map(String key) {
        return KindHouse.mFeatureClzs.get(key);
    }
}
