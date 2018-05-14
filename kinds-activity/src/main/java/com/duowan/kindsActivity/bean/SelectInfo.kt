package com.duowan.kindsActivity.bean

import com.duowan.mobile.main.kinds.Kind
import com.duowan.mobile.main.kinds.wrapper.KindWrapper

/**
 * Created by zhengyongxin on 2018/5/10.
 */
data class SelectInfo(val wrapper: KindWrapper<Kind, Any>) : BaseInfo() {
    constructor(wrapper: KindWrapper<Kind, Any>, type: Int) : this(wrapper) {
        this.type = type
    }
}