package com.duowan.zhengyongxin.abtestfeatures.featureImpl;

import android.support.annotation.NonNull;

import com.duowan.mobile.main.annotation.BooleanKindValue;
import com.duowan.zhengyongxin.abtestfeatures.feature.BooleanTest;

/**
 * Created by zhengyongxin on 2018/3/24.
 */
@BooleanKindValue(true)
public class BooleanTestA extends BooleanTest {
    @NonNull
    @Override
    public String getText() {
        return "BooleanTestA";
    }

    @Override
    public String getName() {
        return "方案A";
    }
}
