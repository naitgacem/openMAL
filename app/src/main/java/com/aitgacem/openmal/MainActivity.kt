package com.aitgacem.openmal

import android.os.Build
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aitgacem.openmalnet.data.UserPreferencesRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository
    private val navHostFragment: NavHostFragment
        get() {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.main_activity_host) as NavHostFragment
            return navHostFragment
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fullScreenDest = setOf(
            R.id.search_dest,
            R.id.detail_dest,
            R.id.edit_work_dest,
            R.id.images_view_dest,
            R.id.settings_dest,
        )

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

}