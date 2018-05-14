package com.duowan.zhengyongxin.abtestfeatures.featureImpl;

import android.support.annotation.NonNull;

import com.duowan.mobile.main.annotation.BooleanKindValue;
import com.duowan.zhengyongxin.abtestfeatures.feature.BooleanTest;

/**
 * Created by zhengyongxin on 2018/3/24.
 */
@BooleanKindValue(false)
public class BooleanTestB extends BooleanTest {
    @NonNull
    @Override
    public String getText() {
        return "BooleanTestB";
    }

    @Override
    public String getName() {
        return "方案B";
    }
}
