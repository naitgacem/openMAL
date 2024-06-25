package com.aitgacem.openmal.ui.fragments.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aitgacem.openmal.data.UserPreferencesRepository
import com.aitgacem.openmalnet.data.getToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val prefs: UserPreferencesRepository,
    @Named("api_key") private val apiKey: String,
    val savedStateHandle: SavedStateHandle,
    @ApplicationContext val ctx: Context
) : ViewModel() {
    private var _isLoggedIn = prefs.isLoggedInFlow
    val isLoggedIn = _isLoggedIn.asLiveData()
    private var _loginError = MutableLiveData(false)
    val loginError: LiveData<Boolean> = _loginError
    private val codeChallenge = generateCodeVerifier()

    fun initiateLogin(authorizationCode: String) {

        viewModelScope.launch {
            withContext(NonCancellable){
                try {
                    val codeVerifier: String = runBlocking {
                        prefs.codeChallenge.firstOrNull() ?: ""
                    }
                    if (codeVerifier.isEmpty())
                        throw IllegalStateException("Code challenge not found")
                    val response = getToken(
                        apiKey,
                        "authorization_code",
                        authorizationCode,
                        codeVerifier
                    )
                    prefs.updateAccessToken(response.token)
                    prefs.updateRefreshToken(response.refreshToken)
                    prefs.updateLoginStatus(true)
                    val pm: PackageManager = ctx.packageManager
                    val intent = pm.getLaunchIntentForPackage(ctx.packageName)
                    val mainIntent = Intent.makeRestartActivityTask(intent!!.component)
                    ctx.startActivity(mainIntent)
                    Runtime.getRuntime().exit(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                    prefs.updateLoginStatus(false)
                    _loginError.postValue(true)
                }
            }
        }
    }

    fun logout() {
        runBlocking {
            prefs.updateAccessToken(null)
            prefs.updateLoginStatus(false)
        }
    }

    private fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(64)
        secureRandom.nextBytes(bytes)
        val encoding = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
        return Base64.encodeToString(bytes, encoding)
    }

    fun launchBrowserForLogin(launch: (Intent) -> Unit) {
        val url =
            "https://myanimelist.net/v1/oauth2/authorize?response_type=code&client_id=${apiKey}&code_challenge=${codeChallenge}&code_challenge_method=plain"
        prefs.updateCodeChallenge(codeChallenge)
        launch(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}