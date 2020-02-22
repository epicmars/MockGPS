package com.androidpi.app.fakegps

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.androidpi.app.fakegps.amap.AmapActivity
import com.androidpi.app.fakegps.baidu.BaiduMapActivity
import com.androidpi.app.fakegps.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.presenter = GpsMockViewHandler()
        binding.viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GpsMockViewModel::class.java)

        setSupportActionBar(binding.toolbar)

        btn_baidu.setOnClickListener {
            BaiduMapActivity.start(this)
        }

        btn_amap.setOnClickListener {
            AmapActivity.start(this)
        }


        if (!EasyPermissions.hasPermissions(this, *PERMS)) {
            EasyPermissions.requestPermissions(this, "", RC_PERMISSION_STORAGE_LOCATION, *PERMS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @AfterPermissionGranted(RC_PERMISSION_STORAGE_LOCATION)
    fun onPermissionGranted() {
        if (EasyPermissions.hasPermissions(this, *PERMS)) {
            //
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    interface GpsMockPresenter {
        fun startMocking()
    }

    private inner class GpsMockViewHandler : GpsMockPresenter {
        override fun startMocking() {
            startService(Intent(this@MainActivity, MockLocationService::class.java))
        }
    }

    companion object {
        const val RC_PERMISSION_STORAGE_LOCATION = 1
        val PERMS = arrayOf(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
