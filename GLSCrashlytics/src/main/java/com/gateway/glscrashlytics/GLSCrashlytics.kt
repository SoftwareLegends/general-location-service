package com.gateway.glscrashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics

object GLSCrashlytics {
    val instance: FirebaseCrashlytics by lazy { FirebaseCrashlytics.getInstance() }
}