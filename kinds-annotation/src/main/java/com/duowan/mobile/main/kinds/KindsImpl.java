package com.duowan.mobile.main.kinds;


import com.duowan.mobile.main.kinds.wrapper.KindWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ericwu on 2017/9/26.
 */

public class KindsImpl {
    private final KindStorage mStorage;
    private final KindsFactory mFactory;
    private final ConcurrentHashMap<Class<? extends Kind>, KindWrapper> mFeatures = new ConcurrentHashMap<>();
    private KindSyringeService mSyringeService;

    public KindsImpl(KindStorage storage, KindsFactory factory) {
        this.mStorage = storage;
        this.mFactory = factory;

        mSyringeService = new KindSyringeService();
        mSyringeService.init();
    }

    /**
     * 获取给定feature的实例
     *
     * @param clz
     * @param <T>
     * @return
     */
    public <T extends Kind> T of(Class<T> clz) {
        return wrapper(clz).instance();
    }

    /**
     * 获取给定feature的封装。
     *
     * @param clz
     * @param <T>
     * @return
     */
    public <T extends Kind> KindWrapper<T, ?> wrapper(Class<T> clz) {
        KindWrapper<T, ?> kindWrapper = mFeatures.get(clz);

        if (kindWrapper == null) {
            kindWrapper = create(clz);

            if (kindWrapper == null) {
                throw new RuntimeException("Kind is not used properly, type:" + clz.getSimpleName());
            }
            mFeatures.put(clz, kindWrapper);
        }

        return kindWrapper;
    }

    private <T extends Kind> KindWrapper<T, ?> create(Class<T> clz) {
        return mFactory.create(clz, mStorage);
    }

    public KindWrapper wrapper(String key) {
        return wrapper(mFactory.map(key));
    }

    private Set<Class<? extends Kind>> featureClasses() {
        return mFactory.featureClasses();
    }

    public boolean addFeatureMap(String moudleName) {
        return mSyringeService.inject(moudleName, KindHouse.mFeatureClzs, KindHouse.mFeatureWrappers);
    }

    public List<KindWrapper> getFeaturesWrapper() {
        List<KindWrapper> result = new ArrayList<>();
        Set<Class<? extends Kind>> featureClasses = featureClasses();
        if (featureClasses != null && !featureClasses.isEmpty()) {
            for (Class<? extends Kind> featureClass : featureClasses) {
                result.add(wrapper(featureClass));
            }
        }
        return result;
    }
}
