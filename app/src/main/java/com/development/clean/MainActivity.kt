package com.development.clean

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.MenuProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.development.clean.base.BaseActivity
import com.development.clean.data.local.sharedprefs.AppSharedPrefs
import com.development.clean.databinding.ActivityMainBinding
import com.development.clean.util.ShakeDetector
import com.development.clean.util.debug.LogcatActivity
import com.development.clean.util.extension.launchActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding, MainActivityViewModel>(R.layout.activity_main),
    ShakeDetector.OnShakeListener {

    override val viewModel: MainActivityViewModel by viewModels()

    private lateinit var mSensorManager: SensorManager
    private lateinit var mShakeDetector: ShakeDetector

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            !viewModel.isLoadSuccess.value
        }
        viewModel.loadData()

        if (AppSharedPrefs.firstOpen) {
            AppSharedPrefs.firstOpen = false
        }

        binding.apply {
            vm = viewModel
        }

        setSupportActionBar(binding.toolbar)

        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment).navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show()
        }

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_settings -> {
                        onShake(1)
                        return true
                    }
                    else -> false
                }
            }
        }, this)

        if (BuildConfig.SHAKE_LOG) {
            mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            mShakeDetector = ShakeDetector(this)
            lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> {
                            mSensorManager.registerListener(
                                mShakeDetector,
                                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                SensorManager.SENSOR_DELAY_UI
                            )
                        }
                        Lifecycle.Event.ON_PAUSE -> {
                            mSensorManager.unregisterListener(mShakeDetector)
                        }
                        else -> {}
                    }
                }
            })
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            @Suppress("UNUSED_VARIABLE") val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data somehow
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
            || super.onSupportNavigateUp()
    }

    override fun onShake(count: Int) {
        if (BuildConfig.SHAKE_LOG) {
            if (count == 1) {
                launchActivity<LogcatActivity>()
            }
        }
    }
}