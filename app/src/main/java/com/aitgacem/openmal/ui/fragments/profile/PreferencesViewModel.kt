package com.aitgacem.openmal.ui.fragments.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.aitgacem.openmalnet.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import openmal.domain.PreferredTitleStyle
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val prefs: UserPreferencesRepository,
): ViewModel() {
    fun setNsfw(enableNsfw: Boolean){
        prefs.toggleNsfw(enableNsfw)
    }
    fun setPreferredTitleStyle(style: PreferredTitleStyle){
        prefs.updatePreferredTitleStyle(style)
    }
    val isNsfwEnabled = prefs.isNsfwEnabledFlow.asLiveData()
    val preferredTitleStyle = prefs.preferredTitleStyle.asLiveData()
}