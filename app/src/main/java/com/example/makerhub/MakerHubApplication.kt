package com.example.makerhub

import android.app.Application
import com.example.makerhub.di.AppComponent
import com.example.makerhub.di.component.ComponentHolder

class MakerHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ComponentHolder.getComponent(this)
    }

    fun getComponent(): AppComponent = ComponentHolder.getComponent(this)
}
