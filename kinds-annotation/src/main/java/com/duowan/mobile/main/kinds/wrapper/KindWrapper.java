package com.duowan.mobile.main.kinds.wrapper;


import com.duowan.mobile.main.kinds.Kind;

import java.util.List;

/**
 * Wrapper class that provide interfaces of a specified feature.
 *
 * @param <T>
 * @param <TYPE>
 */
public interface KindWrapper<T extends Kind, TYPE> {
    /**
     * Get instance of current feature value.
     *
     * @return
     */
    T instance();

    String storageKey();

    /**
     * Read persistent value.
     *
     * @return
     */
    TYPE storageValue();

    /**
     * Store persistent value.
     *
     * @param value
     */
    void store(TYPE value);

    /**
     * Store persistent value specified by instance.
     *
     * @param instance
     */
    void storeInstance(T instance);

    /**
     * Value type.
     *
     * @return
     */
    Class type();

    /**
     * Alias of feature.
     *
     * @return
     */
    String alias();

    /**
     * Default is true in debug build mode. Only valid for boolean features.
     *
     * @return
     */
    boolean enableForDebug();

    /**
     * group of features
     *
     * @return
     */
    String group();

    /**
     * get instance of all features
     *
     * @return
     */
    List<Kind> getAllFeaturesInstance();

    TYPE getValueByIndex(int index);
}