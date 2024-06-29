package com.aitgacem.openmal.ui.fragments.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.aitgacem.openmal.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val prefs: UserPreferencesRepository,
): ViewModel() {
    fun setNsfw(enableNsfw: Boolean){
        prefs.toggleNsfw(enableNsfw)
    }
    val isNsfwEnabled = prefs.isNsfwEnabledFlow.asLiveData()
}