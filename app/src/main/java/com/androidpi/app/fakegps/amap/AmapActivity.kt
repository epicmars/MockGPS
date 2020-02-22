package com.androidpi.app.fakegps.amap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.maps.AMap
import com.androidpi.app.fakegps.R
import kotlinx.android.synthetic.main.activity_amap.*
import kotlinx.android.synthetic.main.btn_locate.*

class AmapActivity : AppCompatActivity() {

    private var amapLocationClient: AMapLocationClient? = null
    private lateinit var amap: AMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amap)
        mapView.onCreate(savedInstanceState)

        amap = mapView.map
        amapLocationClient = AMapLocationClient(this, true)
        amap.setLocationSource(amapLocationClient)
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        amap.isMyLocationEnabled = true

        btn_locate.setOnClickListener {
            amapLocationClient?.startLocation()
        }
    }

    override fun onStart() {
        super.onStart()
        amapLocationClient?.startLocation()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        amapLocationClient?.stopLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, AmapActivity::class.java)
            context.startActivity(starter)
        }
    }

}
