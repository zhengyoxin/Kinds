package com.duowan.zhengyongxin.abtestfeatures.feature

import com.duowan.mobile.main.annotation.IntKindSetting
import com.duowan.mobile.main.kinds.Kind

/**
 * Created by zhengyongxin on 2018/5/3.
 */
@IntKindSetting(defValue = 2, alias = "IntTest的昵称", settingPath = "IntTest")
abstract class IntTest : Kind {
    abstract fun getText(): String
}