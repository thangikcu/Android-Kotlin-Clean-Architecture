package com.development.hiltpractices

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.development.hiltpractices.base.BaseActivity
import com.development.hiltpractices.data.local.sharedprefs.AppSharedPrefs
import com.development.hiltpractices.databinding.ActivityMainBinding
import com.development.hiltpractices.util.ShakeDetector
import com.development.hiltpractices.util.extension.launchActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import vn.viktor.core.util.debug.LogcatActivity

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
        super.onCreate(savedInstanceState)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
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