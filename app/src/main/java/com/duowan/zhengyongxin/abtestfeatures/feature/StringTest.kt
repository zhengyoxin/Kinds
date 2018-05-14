package com.duowan.zhengyongxin.abtestfeatures.feature

import com.duowan.mobile.main.annotation.StringKindSetting
import com.duowan.mobile.main.kinds.Kind

/**
 * Created by zhengyongxin on 2018/4/27.
 */
@StringKindSetting(defValue = "1", alias = "StringTest的昵称", settingPath = "SettingTest")
abstract class StringTest : Kind {
    abstract fun getText(): String
}