package com.radio.broadcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.radio.broadcast.core.common.ui.toImmutableList
import com.radio.broadcast.di.FirebaseUrls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()
    private val _sideEffects = MutableSharedFlow<Boolean>()
    val sideEffects = _sideEffects.asSharedFlow()

    init {
        fetchFirebaseData()
    }

    private fun fetchFirebaseData() {
        val database = Firebase.database.getReference()
        database
            .get()
            .addOnSuccessListener { data ->
                data?.let {
                    val urls = data.child("urls").children
                    val schedule =
                        data.child("programa").children
                            .toList()
                            .map { it.value.toString() }
                            .toImmutableList()
                    FirebaseUrls.radioUrl = urls.first().value.toString()
                    FirebaseUrls.nowPlayingUrl = urls.last()
                        .value
                        .toString()
                        .split("nowplaying")
                        .first()
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            schedule = schedule,
                        )
                    }
                    emitPrepareMediaPlayerEvent()
                }
            }
            .addOnFailureListener {
                emitPrepareMediaPlayerEvent()
                _state.update { state ->
                    state.copy(isLoading = false)
                }
            }
            .addOnCompleteListener {
                _state.update { state ->
                    state.copy(isLoading = false)
                }
            }
    }

    private fun emitPrepareMediaPlayerEvent() {
        viewModelScope.launch {
            _sideEffects.emit(true)
        }
    }
}

