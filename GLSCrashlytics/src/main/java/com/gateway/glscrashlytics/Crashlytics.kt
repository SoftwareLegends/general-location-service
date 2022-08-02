package com.gateway.glscrashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics

object Crashlytics {
    val instance: FirebaseCrashlytics by lazy { FirebaseCrashlytics.getInstance() }
}