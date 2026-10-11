package com.example.makerhub.di.component

import android.content.Context
import com.example.makerhub.di.AppComponent
import com.example.makerhub.di.DaggerAppComponent
import com.example.makerhub.di.module.ExternalModule

class ComponentHolder private constructor() {
    companion object {
        @Volatile
        private var INSTANCE: AppComponent? = null

        fun getComponent(context: Context): AppComponent {
            return INSTANCE ?: synchronized(this) {
                val existing = INSTANCE
                if (existing != null) {
                    existing
                } else {
                    val newComponent = DaggerAppComponent.builder()
                        .externalModule(ExternalModule(context.applicationContext))
                        .build()
                    INSTANCE = newComponent
                    newComponent
                }
            }
        }
    }
}
