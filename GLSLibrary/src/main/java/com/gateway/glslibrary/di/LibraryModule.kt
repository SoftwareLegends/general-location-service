package com.gateway.glslibrary.di

import android.app.Application

object LibraryModule {
    @Volatile
    lateinit var application: Application

    /**
     * This function should called in MainActivity

     * to provide an application instance for the service.
     *
     * @author Ahmed Mones
     */
    fun initializeDI(application: Application) {
        if (this::application.isInitialized.not())
            synchronized(this.application) { this.application = application }
    }
}