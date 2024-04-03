package com.aitgacem.openmal

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aitgacem.openmal.data.UserPreferencesRepository
import com.aitgacem.openmal.ui.fragments.login.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fullScreenDest = setOf(
            R.id.search_dest,
            R.id.detail_dest2,
            R.id.edit_work_dest,
        )

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_activity_host) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomBar = findViewById<BottomNavigationView>(R.id.bottom_nav)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in fullScreenDest) {
                bottomBar.visibility = GONE
            } else {
                bottomBar.visibility = VISIBLE
            }
        }

        bottomBar.setupWithNavController(navController)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null && intent.scheme?.startsWith("openmal") == true) {
            viewModel.initiateLogin(intent.data)
        }
    }

}