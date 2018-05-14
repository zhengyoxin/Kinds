package com.duowan.mobile.main.kinds.wrapper;

import com.duowan.mobile.main.annotation.StringKindValue;
import com.duowan.mobile.main.kinds.Kind;
import com.duowan.mobile.main.kinds.KindStorage;

/**
 * {@link StringKindValue}
 *
 * @param <T>
 */
public abstract class StringKindWrapper<T extends Kind> extends MixedKindWrapper<T, String> {
    public StringKindWrapper(KindStorage storage, String storageKey,
                             String dfValue, Class<T> clz, int valueCount,
                             String alias, String group) {
        super(storage, storageKey, dfValue, clz, valueCount, alias, group);
    }

    @Override
    public final String storageValue() {
        return mStorage.getString(storageKey(), mDefaultValue);
    }

    @Override
    protected final void storeValue(String value) {
        mStorage.putString(storageKey(), value);
    }

    @Override
    public final Class type() {
        return String.class;
    }
}