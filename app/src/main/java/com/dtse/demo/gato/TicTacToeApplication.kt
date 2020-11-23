package com.dtse.demo.gato

import android.app.Application
import com.huawei.hms.api.HuaweiMobileServicesUtil

class TicTacToeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        HuaweiMobileServicesUtil.setApplication(this)

        Preference.init(this)
    }
}