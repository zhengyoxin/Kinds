package com.duowan.mobile.main.kinds.wrapper;


import com.duowan.mobile.main.kinds.Kind;
import com.duowan.mobile.main.kinds.KindStorage;

abstract class AbstractKindWrapper<T extends Kind, TYPE> implements KindWrapper<T, TYPE> {
    protected final KindStorage mStorage;
    protected final String mStorageKey;
    protected final TYPE mDefaultValue;
    private final String mAlias;
    private final String mGroup;

    protected AbstractKindWrapper(KindStorage storage, String storageKey,
                                  TYPE dfValue, String alias, String group) {
        mStorage = storage;
        mStorageKey = storageKey;
        mDefaultValue = dfValue;
        mAlias = alias;
        mGroup = group;
    }

    @Override
    public final String storageKey() {
        return mStorageKey;
    }

    @Override
    public final String alias() {
        return mAlias;
    }

    @Override
    public final String group() {
        return mGroup;
    }

    @Override
    public boolean enableForDebug() {
        return false;
    }
}