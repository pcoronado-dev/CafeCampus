package com.coronado.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coronado.cafecampus.data.prefs.PreferenciasStore
import com.coronado.cafecampus.data.repository.CafeRepository

class CafeViewModelFactory(
    private val repo: CafeRepository,
    private val prefs: PreferenciasStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm: ViewModel = when {
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(repo)
            modelClass.isAssignableFrom(SesionViewModel::class.java) -> SesionViewModel(repo, prefs)
            modelClass.isAssignableFrom(PedidosClienteViewModel::class.java) -> PedidosClienteViewModel(repo)
            modelClass.isAssignableFrom(PanelAdminViewModel::class.java) -> PanelAdminViewModel(repo)
            modelClass.isAssignableFrom(PedidosAdminViewModel::class.java) -> PedidosAdminViewModel(repo)
            modelClass.isAssignableFrom(MenuAdminViewModel::class.java) -> MenuAdminViewModel(repo)
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
        return vm as T
    }
}