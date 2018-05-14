package com.duowan.zhengyongxin.abtestfeatures.featureImpl

import com.duowan.mobile.main.annotation.StringKindValue
import com.duowan.zhengyongxin.abtestfeatures.feature.StringTest

/**
 * Created by zhengyongxin on 2018/4/27.
 */
@StringKindValue("1")
class StringTestA : StringTest() {
    override fun getName(): String {
        return "方案A"
    }

    override fun getText(): String {
        return "StringTestA"
    }
}