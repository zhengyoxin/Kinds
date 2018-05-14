package com.duowan.mobile.main.kinds.wrapper;

import com.duowan.mobile.main.annotation.IntKindValue;
import com.duowan.mobile.main.kinds.Kind;
import com.duowan.mobile.main.kinds.KindStorage;

/**
 * {@link IntKindValue}
 *
 * @param <T>
 */
public abstract class IntKindWrapper<T extends Kind> extends MixedKindWrapper<T, Integer> {
    public IntKindWrapper(KindStorage storage, String storageKey,
                          Integer dfValue, Class<T> clz, int valueCount,
                          String alias, String group) {
        super(storage, storageKey, dfValue, clz, valueCount, alias, group);
    }

    @Override
    public final Integer storageValue() {
        return mStorage.getInt(storageKey(), mDefaultValue);
    }

    @Override
    protected final void storeValue(Integer value) {
        mStorage.putInt(storageKey(), value);
    }

    @Override
    public final Class type() {
        return Integer.TYPE;
    }
}