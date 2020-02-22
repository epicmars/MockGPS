package com.androidpi.app.fakegps.amap

import android.content.Context
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.LocationSource
import com.androidpi.app.fakegps.BuildConfig
import timber.log.Timber

class AMapLocationClient(private val context: Context, isOnceLocation: Boolean) : LocationSource {
    private var locationChangedListener: LocationSource.OnLocationChangedListener? = null
    private var locationClient: AMapLocationClient? = null
    private val option: AMapLocationClientOption
    private val locationListener = AMapLocationListener { aMapLocation ->
        if (locationChangedListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.errorCode == 0) {
                locationChangedListener!!.onLocationChanged(aMapLocation) // 显示系统小蓝点
            } else {
                val errText = "定位失败," + aMapLocation.errorCode + ": " + aMapLocation.errorInfo
                Timber.e(errText)
            }
        }
    }

    /**
     * 激活定位
     */
    override fun activate(listener: LocationSource.OnLocationChangedListener) {
        locationChangedListener = listener
        if (locationClient == null) { //初始化定位
            locationClient = AMapLocationClient(context)
            //设置定位回调监听
            locationClient!!.setLocationListener(locationListener)

            //设置定位参数
            locationClient!!.setLocationOption(option)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            locationClient!!.startLocation() //启动定位
        }
    }

    /**
     * 销毁定位客户端。
     */
    override fun deactivate() {
        locationChangedListener = null
        if (locationClient != null) {
            locationClient!!.stopLocation()
            locationClient!!.onDestroy()
        }
        locationClient = null
    }

    fun startLocation() {
        if (null == locationClient || locationClient!!.isStarted) return
        locationClient!!.startLocation()
    }

    fun stopLocation() {
        if (null == locationClient || !locationClient!!.isStarted) return
        locationClient!!.stopLocation()
    }

    init {
        // 初始化定位参数
        option = AMapLocationClientOption()
        if (BuildConfig.DEBUG) {
            option.isMockEnable = true
        }
        if (isOnceLocation) { // 获取一次定位结果：
            // 该方法默认为false。
            option.isOnceLocation = true
            // 获取最近3s内精度最高的一次定位结果：
            // 设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
            // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
            option.isOnceLocationLatest = true
        }
        //设置为省电模式
        option.locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving
    }
}