package com.example.bikestockproject

import android.app.Application
import com.example.bikestockproject.repositori.AppContainer
import com.example.bikestockproject.repositori.BikeStockContainer

class BikeStockApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = BikeStockContainer()
    }
}