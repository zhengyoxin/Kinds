package com.duowan.zhengyongxin.abtestfeatures.feature;

import com.duowan.mobile.main.annotation.BooleanKindSetting;
import com.duowan.mobile.main.kinds.Kind;

/**
 * Created by zhengyongxin on 2018/5/2.
 */
@BooleanKindSetting(defValue = true, alias = "BooleanTest的昵称", settingPath = "BooleanTest")
public abstract class BooleanTest implements Kind {
    public abstract String getText();
}
