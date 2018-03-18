package kr.co.switchnow.switch_now_client.Util;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;



/**
 * Created by ceo on 2017-04-22.
 */

public class LocationInfo extends Service implements LocationListener {


    private Context ContextAsync;

    Geocoder GeocoderAsync; // 도시정보 등을 기록하기 위한 것, 아직까지는 아님
    public ProgressDialog progress;
    Location location;
    String provider;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private static final int REQUEST_CODE_PERMISSION = 2;

    public double latAsync = 0.0;
    public double lonAsync = 0.0;
//    private LocationResult locationResult;
    LocationManager locationManagerAsync;

    boolean gps_enabled = false;
    boolean network_enabled = false;
    boolean getLocationInfo = false;

    public LocationInfo(Context context) {
        this.ContextAsync = context;
        getLocation();
    }

    public Location getLocation() {

        progress = new ProgressDialog(ContextAsync);
        progress.setCancelable(true);
        progress.setIndeterminate(true);
        progress.setMessage("Loading GPS Info.  Please wait...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCanceledOnTouchOutside(false);
        progress.show();


        locationManagerAsync = (LocationManager) ContextAsync.getSystemService(LOCATION_SERVICE);
//        locationProviderAsync = (LocationProvider) locationManagerAsync.getProvider(LocationManager.GPS_PROVIDER);


        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManagerAsync.getBestProvider(criteria, true);
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(ContextAsync, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ContextAsync, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) this.ContextAsync, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);


                return null;
            }
            locationManagerAsync.requestLocationUpdates(provider, 2 * 60 * 1000, 10, this);
//            Toast.makeText(ContextAsync, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
        }

        try {
            gps_enabled = locationManagerAsync.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("TAG", "GPS ENABLE FLAG-------------------------->" + ex.getMessage().toString());
        }
        try {
            network_enabled = locationManagerAsync.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Log.d("TAG", "NETWORK ENABLE FLAG-------------------------->" + ex.getMessage().toString());
        }

        if (!gps_enabled && !network_enabled) {
            if(progress.isShowing()){
                progress.dismiss();
            }
            showSettingsAlert();
        } else if (gps_enabled && provider.contentEquals("gps")) {

            locationManagerAsync.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            if (locationManagerAsync != null) {

                Log.d("TAG","--------------------------Activate----------------------> GPS PROVIDER");
                location = locationManagerAsync
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latAsync = location.getLatitude();
                    lonAsync = location.getLongitude();
                    if(lonAsync != 0.0  && latAsync!= 0.0){
                        this.getLocationInfo = true;
                        if(progress.isShowing()){
                            progress.dismiss();
                        }
                    }
                }

            }

        } else if (network_enabled) {
            locationManagerAsync.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            if (locationManagerAsync != null) {
                Log.d("TAG","--------------------------Activate----------------------> NETWORK PROVIDER");
                location = locationManagerAsync
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latAsync = location.getLatitude();
                    lonAsync = location.getLongitude();
                    if(lonAsync != 0.0  && latAsync!= 0.0){
                        this.getLocationInfo = true;

                        if(progress.isShowing()){
                            progress.dismiss();
                        }
                    }
                }
            }
        }else{
            locationManagerAsync.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            if (locationManagerAsync != null) {
                location = locationManagerAsync
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latAsync = location.getLatitude();
                    lonAsync = location.getLongitude();
                    if(lonAsync != 0.0  && latAsync!= 0.0){
                        getLocationInfo = true;
                        if(progress.isShowing()){
                            progress.dismiss();
                        }
                    }
                }
            }
        }

        return location;
    }

    public void stopUsingGPS() {
        if (locationManagerAsync != null) {
            locationManagerAsync.removeUpdates(LocationInfo.this);
        }
    }

    public void showSettingsAlert() {
        if(progress.isShowing()){
            progress.dismiss();
        }
        CustomDialog settingDialog = new CustomDialog();
        settingDialog.GPS_DIALOG(ContextAsync, "위치정보 기능을 키시겠습니까?");

    }

    public double getLatitude() {
        if (location != null) {
            latAsync = location.getLatitude();
        }

        return latAsync;
    }

    public double getLongitude(){
        if(location != null){
            lonAsync = location.getLongitude();
        }
        // return longitude
        return lonAsync;
    }

    public boolean canGetLocation() {
        return this.getLocationInfo;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
