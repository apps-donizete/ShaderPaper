package com.dv.apps.shader.paper.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dv.apps.shader.paper.domain.model.ShaderManifest
import com.dv.apps.shader.paper.domain.repository.ShaderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

internal data class State(
    val shaderManifest: ShaderManifest = ShaderManifest()
)

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    shaderRepository: ShaderRepository
) : ViewModel() {
    val state = shaderRepository
        .getManifest()
        .map(::State)
        .stateIn(viewModelScope, SharingStarted.Eagerly, State())
}