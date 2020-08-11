package com.rafaelanastacioalves.design.concepts.common

import android.os.Build


object Utility {

    //TODO Implement
    val deviceBrand: String
        get() = Build.MANUFACTURER

    val deviceModel: String
        get() = Build.MODEL

    val androidVersion: String
        get() = Build.VERSION.RELEASE
}
