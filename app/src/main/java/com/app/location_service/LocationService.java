package com.app.location_service;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.greenttaxi.R;
import com.app.greenttaxi.models.LocationModel;
import com.app.greenttaxi.models.UserModel;
import com.app.greenttaxi.remote.Api;
import com.app.greenttaxi.share.Common;
import com.app.greenttaxi.tags.Tags;
import com.app.greenttaxi.ui.activity_login.LoginActivity;
import com.app.preferences.Preferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;


import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Preferences preferences;
    private UserModel userModel;
private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        context=getApplicationContext();
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest();
    }

    private void initLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5 * 60000);
        locationRequest.setFastestInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(false);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {

            Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.e("not available", "not available");
                    break;
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onLocationChanged(Location location) {
        LoginActivity.dialog.show();
        LocationModel locationModel = new LocationModel(location.getLatitude(), location.getLongitude());
        EventBus.getDefault().post(locationModel);

        Api.getService(Tags.base_url)
                .updateLocation(String.valueOf(userModel.getData().getId()), location.getLatitude(), location.getLongitude())
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        LoginActivity.hideprogress();
                        if (response.isSuccessful() && response.body() != null) {
                            Intent intent = new Intent(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:com.app.greenttaxi"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
//                            userModel.getData().setLatitude(String.valueOf(location.getLatitude()));
//                            userModel.getData().setLongitude(String.valueOf(location.getLongitude()));
//                            preferences.create_update_userdata(com.zawraapharma.location_service.LocationService.this,userModel);

                        } else {
                            try {

                                Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            LoginActivity.hideprogress();
                            if (t.getMessage() != null) {
                                Log.e("errorToken2", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    // Toast.makeText(com.zawraapharma.location_service.LocationService.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    //  Toast.makeText(com.zawraapharma.location_service.LocationService.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleApiClient != null && locationCallback != null) {
            LocationServices.getFusedLocationProviderClient(this)
                    .removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
            googleApiClient = null;
        }
    }
}
