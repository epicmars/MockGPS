package com.androidpi.app.fakegps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class MockLocationService extends Service implements Handler.Callback {

    private static final String[] MOCK_PROVIDERS = {
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
    };

    private static final int MSG_MOCK_LOCATION = 1;

    private LocationManager locationManager;
    private ScheduledExecutorService executorService;
    private Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(this);
        locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        for (String provider : MOCK_PROVIDERS) {
            locationManager.addTestProvider(provider,
                    true, false,
                    false, false,
                    false, false,
                    false, Criteria.POWER_LOW,
                    Criteria.ACCURACY_FINE);
            if (!locationManager.isProviderEnabled(provider)) {
                locationManager.setTestProviderEnabled(provider, true);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(MSG_MOCK_LOCATION);
                }
            }, 0, 3000, TimeUnit.MILLISECONDS);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

    private void mockLocation() {
        for (String provider : MOCK_PROVIDERS) {
            mockLocation(provider);
        }
    }

    private void mockLocation(String provider) {
        Location location = new Location(provider);
        location.setLongitude(121.0);
        location.setLatitude(31.0);
        location.setTime(System.currentTimeMillis());
        location.setAccuracy(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        try {
            locationManager.setTestProviderLocation(provider, location);
            locationManager.requestLocationUpdates(provider, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Timber.d("onLocationChanged: %s", location.toString());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Timber.d("onStatusChanged: %s", provider);

                }

                @Override
                public void onProviderEnabled(String provider) {
                    Timber.d("onProviderEnabled: %s", provider);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Timber.d("onProviderDisabled: %s", provider);
                }
            });
        } catch (IllegalArgumentException | SecurityException e) {
            Timber.e(e);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_MOCK_LOCATION:
                mockLocation();
                return true;
        }
        return false;
    }
}
