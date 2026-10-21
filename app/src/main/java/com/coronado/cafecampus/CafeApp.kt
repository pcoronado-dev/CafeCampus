package com.coronado.cafecampus

import android.app.Application
import com.coronado.cafecampus.data.local.CafeDatabase
import com.coronado.cafecampus.data.local.DatosIniciales
import com.coronado.cafecampus.data.prefs.PreferenciasStore
import com.coronado.cafecampus.data.repository.CafeRepository
import com.coronado.cafecampus.data.repository.CafeRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CafeApp : Application() {

    val database: CafeDatabase by lazy { CafeDatabase.obtener(this) }
    val repository: CafeRepository by lazy { CafeRepositoryRoom(database) }
    val preferencias: PreferenciasStore by lazy { PreferenciasStore(this) }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            DatosIniciales.sembrar(database)
        }
    }
}
