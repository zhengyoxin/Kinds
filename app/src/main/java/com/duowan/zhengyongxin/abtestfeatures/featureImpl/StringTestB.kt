package com.duowan.zhengyongxin.abtestfeatures.featureImpl

import com.duowan.mobile.main.annotation.StringKindValue
import com.duowan.zhengyongxin.abtestfeatures.feature.StringTest

/**
 * Created by zhengyongxin on 2018/4/27.
 */
@StringKindValue("2")
class StringTestB : StringTest() {
    override fun getName(): String {
        return "方案B"
    }

    override fun getText(): String {
        return "StringTestB"
    }
}