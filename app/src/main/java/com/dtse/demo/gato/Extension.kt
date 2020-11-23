package com.dtse.demo.gato

import android.util.Log
import com.huawei.hms.common.ApiException

fun Exception.printLog(tag: String, msg: String) {
    if (this is ApiException) {
        Log.e(tag, "$msg, HMS Status Code: ${this.statusCode}")
    } else {
        Log.e(tag, "$msg, ${this.message}")
    }
}